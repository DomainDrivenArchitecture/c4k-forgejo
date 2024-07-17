(ns dda.c4k-forgejo.core
  (:require
   [clojure.spec.alpha :as s]
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-common.common :as cm]
   [dda.c4k-common.monitoring :as mon]
   [dda.c4k-forgejo.forgejo :as forgejo]
   [dda.c4k-forgejo.backup :as backup]
   [dda.c4k-common.postgres :as postgres]
   [dda.c4k-common.namespace :as ns]))

(def config-defaults {:issuer "staging", :deploy-federated "false"})
(def rate-limit-defaults {:max-rate 10, :max-concurrent-requests 5})

(def config? (s/keys :req-un [::forgejo/fqdn
                              ::forgejo/mailer-from
                              ::forgejo/mailer-host
                              ::forgejo/mailer-port
                              ::forgejo/service-noreply-address]
                     :opt-un [::forgejo/issuer
                              ::forgejo/deploy-federated
                              ::forgejo/default-app-name
                              ::forgejo/service-domain-whitelist
                              ::forgejo/forgejo-image-version-overwrite
                              ::backup/restic-repository
                              ::mon/mon-cfg]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password
                            ::forgejo/mailer-user ::forgejo/mailer-pw
                            ::backup/aws-access-key-id ::backup/aws-secret-access-key]
                   :opt-un [::backup/restic-password ; TODO gec: Is restic password opt or req?
                            ::mon/mon-cfg]))

(def vol? (s/keys :req-un [::forgejo/volume-total-storage-size]))

(def postgres-config {:db-name "forgejo"
                      :pv-storage-size-gb 5
                      :pvc-storage-class-name ""
                      :postgres-image "postgres:14"
                      :postgres-size :2gb})

(defn config-objects [config] ; ToDo: ADR for generate functions - vector or no vector?
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)
        resolved-config (merge {:namespace "forgejo"} postgres-config config)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  (ns/generate resolved-config)
                  [(postgres/generate-config resolved-config)
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
                  (when (:contains? resolved-config :mon-cfg)
                    (mon/generate-config)))))))

(defn auth-objects [config auth] ; ToDo: ADR for generate functions - vector or no vector?
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)
        resolved-config (merge {:namespace "forgejo"} postgres-config config)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  (ns/generate resolved-config)
                  [(postgres/generate-secret {:namespace "forgejo"} auth)
                   (forgejo/generate-secrets auth)]
                  (when (contains? resolved-config :restic-repository)
                    [(backup/generate-secret auth)])
                  (when (:contains? resolved-config :mon-cfg)
                    (mon/generate-auth (:mon-cfg resolved-config) (:mon-auth auth))))))))
