(ns dda.c4k-forgejo.core
  (:require
   [clojure.spec.alpha :as s]
   #?(:clj [orchestra.core :refer [defn-spec]]
      :cljs [orchestra.core :refer-macros [defn-spec]])
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-common.common :as cm]
   [dda.c4k-common.monitoring :as mon]
   [dda.c4k-common.backup :as backup]
   [dda.c4k-forgejo.forgejo :as forgejo]
   [dda.c4k-common.postgres :as postgres]
   [dda.c4k-common.namespace :as ns]))

(def config-defaults {:namespace "forgejo"
                      :default-app-name "forgejo instance"
                      :issuer "staging"
                      :federation-enabled "false"
                      :forgejo-image "codeberg.org/forgejo/forgejo:11.0.1"
                      :sso-mode :none
                      :db-name "forgejo"
                      :volume-total-storage-size 50
                      :pv-storage-size-gb 5
                      :pvc-storage-class-name :local-path
                      :postgres-image "postgres:14"
                      :postgres-size :2gb
                      :backup-image "domaindrivenarchitecture/c4k-forgejo-backup"
                      :app-name "forgejo"
                      :backup-postgres true
                      :backup-volume-mount {:mount-name "forgejo-data-volume"
                                            :pvc-name "forgejo-data-pvc"
                                            :mount-path "/var/backups"}
                      :max-rate 100, 
                      :max-concurrent-requests 150})

(def config? (s/keys :req-un [::forgejo/fqdn
                              ::forgejo/mailer-from
                              ::forgejo/mailer-host
                              ::forgejo/mailer-port
                              ::forgejo/service-noreply-address]
                     :opt-un [::forgejo/issuer
                              ::forgejo/federation-enabled
                              ::forgejo/default-app-name
                              ::forgejo/session-lifetime
                              ::forgejo/service-domain-whitelist
                              ::forgejo/forgejo-image
                              ::backup/restic-repository
                              ::mon/mon-cfg]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password
                            ::forgejo/mailer-user ::forgejo/mailer-pw
                            ::forgejo/secret-key]
                   :opt-un [::backup/restic-password
                            ::backup/restic-new-password
                            ::backup/aws-access-key-id 
                            ::backup/aws-secret-access-key
                            ::mon/mon-auth]))

(defn-spec config-objects seq?
  [config config?]
  (let [resolved-config (merge config-defaults config)
        storage-class (if (contains? resolved-config :postgres-data-volume-path) :manual :local-path)
        {:keys [fqdn max-rate max-concurrent-requests namespace]} resolved-config]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  (ns/generate resolved-config)
                  [(postgres/generate-configmap resolved-config)
                   (when (contains? resolved-config :postgres-data-volume-path)
                     (postgres/generate-persistent-volume
                      (select-keys resolved-config [:postgres-data-volume-path :pv-storage-size-gb])))
                   (postgres/generate-pvc (merge resolved-config {:pvc-storage-class-name storage-class}))
                   (postgres/generate-deployment resolved-config)
                   (postgres/generate-service resolved-config)]
                  (forgejo/config resolved-config)
                  (ing/generate-simple-ingress (merge
                                                {:service-name "forgejo-service"
                                                 :service-port 3000
                                                 :fqdns [fqdn]
                                                 :average-rate max-rate
                                                 :burst-rate max-concurrent-requests
                                                 :namespace namespace}
                                                resolved-config))
                  (when (contains? resolved-config :restic-repository)
                    (backup/config-objects resolved-config))
                  (when (contains? resolved-config :mon-cfg)
                    (mon/generate-config)))))))

(defn-spec auth-objects seq?
  [config config?
   auth auth?]
  (let [resolved-config (merge config-defaults config)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  (ns/generate resolved-config)
                  [(postgres/generate-secret resolved-config auth)]
                  (forgejo/auth auth)
                  (when (contains? resolved-config :restic-repository)
                    (backup/auth-objects resolved-config auth))
                  (when (contains? resolved-config :mon-cfg)
                    (mon/generate-auth (:mon-cfg resolved-config) (:mon-auth auth))))))))
