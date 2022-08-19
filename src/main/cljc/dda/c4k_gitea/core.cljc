(ns dda.c4k-gitea.core
 (:require
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-gitea.gitea :as gitea]
  [dda.c4k-gitea.gitea :as backup]
  [dda.c4k-common.postgres :as postgres]))

(def config-defaults {:issuer "staging"})

(def config? (s/keys :req-un [::gitea/fqdn 
                              ::gitea/mailer-from 
                              ::gitea/mailer-host-port 
                              ::gitea/service-noreply-address]
                     :opt-un [::gitea/issuer 
                              ::gitea/default-app-name 
                              ::gitea/service-domain-whitelist
                              ::backup/restic-repository]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password
                            ::gitea/mailer-user ::gitea/mailer-pw
                            ::backup/aws-access-key-id ::backup/aws-secret-access-key
                            ::backup/restic-password]))

(def vol? (s/keys :req-un [::gitea/volume-total-storage-size]))

(defn k8s-objects [config]
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)]
    (map yaml/to-string
         (filter #(not (nil? %))
                 (cm/concat-vec
                  [(postgres/generate-config {:postgres-size :2gb :db-name "gitea"})
                   (postgres/generate-secret config)
                   (when (contains? config :postgres-data-volume-path)
                     (postgres/generate-persistent-volume (select-keys config [:postgres-data-volume-path :pv-storage-size-gb])))
                   (postgres/generate-pvc {:pv-storage-size-gb 5
                                           :pvc-storage-class-name storage-class})
                   (postgres/generate-deployment {:postgres-image "postgres:14"
                                                  :postgres-size :2gb})
                   (postgres/generate-service)
                   (gitea/generate-deployment)
                   (gitea/generate-service)
                   (gitea/generate-service-ssh)
                   (gitea/generate-root-volume config)
                   (gitea/generate-data-volume config)
                   (gitea/generate-appini-env config)
                   (gitea/generate-secrets config)
                   (gitea/generate-ingress config)
                   (gitea/generate-certificate config)]
                  (when (contains? config :restic-repository)
                    [(backup/generate-config config)
                     (backup/generate-secret config)
                     (backup/generate-cron)
                     (backup/generate-backup-restore-deployment config)]))))))
