(ns dda.c4k-gitea.core
 (:require
  [clojure.string :as cs]
  [clojure.spec.alpha :as s]
  #?(:clj [orchestra.core :refer [defn-spec]]
     :cljs [orchestra.core :refer-macros [defn-spec]])
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.postgres :as postgres]
  [dda.c4k-gitea.gitea :as gitea]))

(def config-defaults {:issuer :staging})

(def config? (s/keys :req-un [::gitea/fqdn]
                     :opt-un [::gitea/issuer]))

(def auth? (s/keys :req-un [::gitea/django-secret-key
                            ::postgres/postgres-db-user ::postgres/postgres-db-password]))

(defn k8s-objects [config]
  (let [storage-class (if (contains? config :postgres-data-volume-path) :manual :local-path)]
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
          (gitea/generate-secret config)
          (gitea/generate-webserver-deployment)
          (gitea/generate-celeryworker-deployment)
          (gitea/generate-ingress config)
          (gitea/generate-certificate config)
          (gitea/generate-service-redis)
          (gitea/generate-service-webserver)
          (gitea/generate-statefulset)])))

; TODO: Remove once cljs release of common worked
(defn-spec generate any?
  [my-config config?
   my-auth auth?]
  (let [resulting-config (merge config-defaults my-config my-auth)]
    (cs/join
     "\n---\n"
     (k8s-objects resulting-config))))
