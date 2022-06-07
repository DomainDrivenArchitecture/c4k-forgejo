(ns dda.c4k-gitea.core
 (:require
  [clojure.spec.alpha :as s]
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-common.postgres :as postgres]
  [dda.c4k-gitea.gitea :as gitea]))

(def config-defaults {:issuer :staging})

(def config? (s/keys :req-un [::gitea/fqdn]
                     :opt-un [::gitea/issuer]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password]))

(defn k8s-objects [config]
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)]
    (cm/concat-vec
     [(yaml/load-resource "gitea/volumes.yaml")
      (yaml/load-resource "gitea/services.yaml")]
     (map yaml/to-string
          [(postgres/generate-config {:postgres-size :2gb :db-name "gitea"})
           (postgres/generate-secret config)
           (when (contains? config :postgres-data-volume-path)
             (postgres/generate-persistent-volume (select-keys config [:postgres-data-volume-path :pv-storage-size-gb])))
           (postgres/generate-pvc {:pv-storage-size-gb 20
                                   :pvc-storage-class-name storage-class})
           (postgres/generate-deployment {:postgres-image "postgres:14"
                                          :postgres-size :2gb})
           (postgres/generate-service)
           (gitea/generate-appini-configmap)
           (gitea/generate-deployment config)
           (gitea/generate-ingress config)]))))
