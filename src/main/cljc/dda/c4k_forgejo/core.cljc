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

(def config-defaults {:namespace "forgejo"
                      :issuer "staging"
                      :deploy-federated "false"
                      :db-name "forgejo"
                      :pv-storage-size-gb 5
                      :pvc-storage-class-name ""
                      :postgres-image "postgres:14"
                      :postgres-size :2gb})
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

(defn config-objects [config] ; ToDo: ADR for generate functions - vector or no vector?
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  (ns/generate config)
                  [(postgres/generate-configmap config)
                   (when (contains? config :postgres-data-volume-path)
                     (postgres/generate-persistent-volume (select-keys config [:postgres-data-volume-path :pv-storage-size-gb])))
                   (postgres/generate-pvc (merge config {:pvc-storage-class-name storage-class}))
                   (postgres/generate-deployment config)
                   (postgres/generate-service config)
                   (forgejo/generate-deployment config)
                   (forgejo/generate-service)
                   (forgejo/generate-service-ssh)
                   (forgejo/generate-data-volume config)
                   (forgejo/generate-appini-env config)]
                  (forgejo/generate-ratelimit-ingress-and-cert config) ; this function has a vector as output
                  (when (contains? config :restic-repository)
                    [(backup/generate-config config)
                     (backup/generate-cron)
                     (backup/generate-backup-restore-deployment config)])
                  (when (contains? config :mon-cfg)
                    (mon/generate-config)))))))

(defn auth-objects [config auth]
  (map yaml/to-string
       (filter #(not (nil? %))
               (cm/concat-vec
                (ns/generate config)
                [(postgres/generate-secret config auth)
                 (forgejo/generate-secrets auth)]
                (when (contains? config :restic-repository)
                  [(backup/generate-secret auth)])
                (when (contains? config :mon-cfg)
                  (mon/generate-auth (:mon-cfg config) (:mon-auth auth)))))))
