(ns dda.c4k-forgejo.core
  (:require
   [clojure.spec.alpha :as s]
   #?(:clj [orchestra.core :refer [defn-spec]]
      :cljs [orchestra.core :refer-macros [defn-spec]])
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-common.common :as cm]
   [dda.c4k-common.predicate :as p]
   [dda.c4k-common.monitoring :as mon]
   [dda.c4k-forgejo.forgejo :as forgejo]
   [dda.c4k-forgejo.backup :as backup]
   [dda.c4k-common.postgres :as postgres]
   [dda.c4k-common.namespace :as ns]))

(def config-defaults {:namespace "forgejo"
                      :issuer "staging"
                      :deploy-federated "false"
                      :federation-enabled "false"
                      :db-name "forgejo"
                      :pv-storage-size-gb 5
                      :pvc-storage-class-name ""
                      :postgres-image "postgres:14"
                      :postgres-size :2gb
                      :max-rate 10, 
                      :max-concurrent-requests 5})

(def config? (s/keys :req-un [::forgejo/fqdn
                              ::forgejo/mailer-from
                              ::forgejo/mailer-host
                              ::forgejo/mailer-port
                              ::forgejo/service-noreply-address]
                     :opt-un [::forgejo/issuer
                              ::forgejo/deploy-federated
                              ::forgejo/federation-enabled
                              ::forgejo/default-app-name
                              ::forgejo/service-domain-whitelist
                              ::forgejo/forgejo-image-version-overwrite
                              ::backup/restic-repository
                              ::mon/mon-cfg]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password
                            ::forgejo/mailer-user ::forgejo/mailer-pw]
                   :opt-un [::backup/restic-password
                            ::backup/restic-new-password
                            ::backup/aws-access-key-id 
                            ::backup/aws-secret-access-key
                            ::mon/mon-auth]))

(defn-spec config-objects p/map-or-seq?
  [config config?]
  (let [resolved-config (merge config-defaults config)
        storage-class (if (contains? resolved-config :postgres-data-volume-path) :manual :local-path)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  (ns/generate resolved-config)
                  [(postgres/generate-configmap resolved-config)
                   (when (contains? resolved-config :postgres-data-volume-path)
                     (postgres/generate-persistent-volume (select-keys resolved-config [:postgres-data-volume-path :pv-storage-size-gb])))
                   (postgres/generate-pvc (merge resolved-config {:pvc-storage-class-name storage-class}))
                   (postgres/generate-deployment resolved-config)
                   (postgres/generate-service resolved-config)
                   (forgejo/generate-deployment resolved-config)
                   (forgejo/generate-service)
                   (forgejo/generate-service-ssh)
                   (forgejo/generate-data-volume resolved-config)
                   (forgejo/generate-appini-env resolved-config)]
                  (forgejo/generate-ratelimit-ingress-and-cert resolved-config) ; this function has a vector as output
                  (when (contains? resolved-config :restic-repository)
                    [(backup/generate-config resolved-config)
                     (backup/generate-cron)
                     (backup/generate-backup-restore-deployment resolved-config)])
                  (when (contains? resolved-config :mon-cfg)
                    (mon/generate-config)))))))

(defn-spec auth-objects p/map-or-seq?
  [config config?
   auth auth?]
  (let [resolved-config (merge config-defaults config)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  (ns/generate resolved-config)
                  [(postgres/generate-secret resolved-config auth)
                   (forgejo/generate-secrets auth)]
                  (when (contains? resolved-config :restic-repository)
                    [(backup/generate-secret auth)])
                  (when (contains? resolved-config :mon-cfg)
                    (mon/generate-auth (:mon-cfg resolved-config) (:mon-auth auth))))))))
