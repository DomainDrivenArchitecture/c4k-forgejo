(ns dda.c4k-gitea.core
 (:require
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-gitea.gitea :as gitea]
  [dda.c4k-common.postgres :as postgres]))

(defn k8s-objects [config]
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)]
    (cm/concat-vec
     (map yaml/to-string
          (filter #(not (nil? %))
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
                   (gitea/generate-data-volume config)
                   (gitea/generate-appini-env config)
                   (gitea/generate-secrets config)
                   (gitea/generate-ingress config)
                   (gitea/generate-certificate config)])))))
