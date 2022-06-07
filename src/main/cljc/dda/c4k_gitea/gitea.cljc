(ns dda.c4k-gitea.gitea
 (:require
  [clojure.spec.alpha :as s]
  #?(:cljs [shadow.resource :as rc])
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-common.predicate :as pred]))

(s/def ::fqdn pred/fqdn-string?)
(s/def ::issuer pred/letsencrypt-issuer?)

; TODO
#?(:cljs
   (defmethod yaml/load-resource :gitea [resource-name]
     (case resource-name
       "gitea/secret.yaml" (rc/inline "gitea/secret.yaml")
       "gitea/certificate.yaml" (rc/inline "gitea/certificate.yaml")
       "gitea/deployments.yaml" (rc/inline "gitea/deployments.yaml")
       "gitea/ingress.yaml" (rc/inline "gitea/ingress.yaml")  
       "gitea/service-redis.yaml" (rc/inline "gitea/service-redis.yaml")
       "gitea/service-webserver.yaml" (rc/inline "gitea/service-webserver.yaml")
       "gitea/statefulset.yaml" (rc/inline "gitea/statefulset.yaml")
       (throw (js/Error. "Undefined Resource!")))))

#?(:cljs
   (defmethod yaml/load-as-edn :gitea [resource-name]
     (yaml/from-string (yaml/load-resource resource-name))))
 
(defn generate-appini-configmap []
  (yaml/load-as-edn "gitea/appini-configmap.yaml"))

(defn generate-deployment [config]
  (let [{:keys [postgres-db-user postgres-db-password]} config]
    (->
     (yaml/load-as-edn "gitea/deployment.yaml")
     (cm/replace-named-value "GITEA__database__USER" postgres-db-user)
     (cm/replace-named-value "GITEA__database__PASSWD" postgres-db-password))))

(defn generate-ingress [config]
  (let [{:keys [fqdn issuer]
         :or {issuer :staging}} config
        letsencrypt-issuer (name issuer)]
    (->
     (yaml/load-as-edn "gitea/ingress.yaml")
     (assoc-in [:metadata :annotations :cert-manager.io/cluster-issuer] letsencrypt-issuer)
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn))))

(defn generate-volumes []
  (yaml/load-as-edn "gitea/volumes.yaml"))
