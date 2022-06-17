(ns dda.c4k-gitea.gitea
 (:require
  [clojure.spec.alpha :as s]
  #?(:cljs [shadow.resource :as rc])
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-common.predicate :as pred]))

(s/def ::fqdn pred/fqdn-string?)
(s/def ::issuer pred/letsencrypt-issuer?)

#?(:cljs
   (defmethod yaml/load-resource :gitea [resource-name]
     (case resource-name
       "gitea/appini-configmap.yaml" (rc/inline "gitea/appini-configmap.yaml")
       "gitea/appini-env-configmap.yaml" (rc/inline "gitea/appini-env-configmap.yaml")
       "gitea/deployment.yaml" (rc/inline "gitea/deployment.yaml")
       "gitea/ingress.yaml" (rc/inline "gitea/ingress.yaml")  
       "gitea/services.yaml" (rc/inline "gitea/services.yaml")
       "gitea/volumes.yaml" (rc/inline "gitea/volumes.yaml")
       (throw (js/Error. "Undefined Resource!")))))

#?(:cljs
   (defmethod yaml/load-as-edn :gitea [resource-name]
     (yaml/from-string (yaml/load-resource resource-name))))
 
(defn generate-appini-env [config]
  (let [{:keys [postgres-db-user postgres-db-password fqdn]} config]
    (->
     (yaml/load-as-edn "gitea/appini-env-configmap.yaml")
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn)
     (cm/replace-all-matching-values-by-new-value "URL" (str "https://" fqdn))
     (cm/replace-all-matching-values-by-new-value "DBUSER" postgres-db-user)
     (cm/replace-all-matching-values-by-new-value "DBPW" postgres-db-password))))

(defn generate-ingress [config]
  (let [{:keys [fqdn issuer]
         :or {issuer "staging"}} config
        letsencrypt-issuer (name issuer)]
    (->
     (yaml/load-as-edn "gitea/ingress.yaml")
     (assoc-in [:metadata :annotations :cert-manager.io/cluster-issuer] letsencrypt-issuer)
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn))))
  
