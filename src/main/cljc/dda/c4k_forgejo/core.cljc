(ns dda.c4k-forgejo.core
 (:require
  [clojure.spec.alpha :as s]
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-forgejo.forgejo :as forgejo]
  [dda.c4k-forgejo.backup :as backup]
  [dda.c4k-common.postgres :as postgres]))

(def config-defaults {:issuer "staging"})

(def config? (s/keys :req-un [::forgejo/fqdn 
                              ::forgejo/mailer-from 
                              ::forgejo/mailer-host-port 
                              ::forgejo/service-noreply-address]
                     :opt-un [::forgejo/issuer 
                              ::forgejo/default-app-name 
                              ::forgejo/service-domain-whitelist
                              ::backup/restic-repository]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password
                            ::forgejo/mailer-user ::forgejo/mailer-pw
                            ::backup/aws-access-key-id ::backup/aws-secret-access-key]
                   :opt-un [::backup/restic-password])) ; TODO gec: Is restic password opt or req?

(def vol? (s/keys :req-un [::forgejo/volume-total-storage-size]))

(defn k8s-objects [config]
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  [(postgres/generate-config {:postgres-size :2gb :db-name "forgejo"})
                   (postgres/generate-secret config)
                   (when (contains? config :postgres-data-volume-path)
                     (postgres/generate-persistent-volume (select-keys config [:postgres-data-volume-path :pv-storage-size-gb])))
                   (postgres/generate-pvc {:pv-storage-size-gb 5
                                           :pvc-storage-class-name storage-class})
                   (postgres/generate-deployment {:postgres-image "postgres:14"
                                                  :postgres-size :2gb})
                   (postgres/generate-service)
                   (forgejo/generate-deployment)
                   (forgejo/generate-service)
                   (forgejo/generate-service-ssh)                   
                   (forgejo/generate-data-volume config)
                   (forgejo/generate-appini-env config)
                   (forgejo/generate-secrets config)
                   (forgejo/generate-ingress config)
                   (forgejo/generate-certificate config)]
                  (when (contains? config :restic-repository)
                    [(backup/generate-config config)
                     (backup/generate-secret config)
                     (backup/generate-cron)
                     (backup/generate-backup-restore-deployment config)]))))))
