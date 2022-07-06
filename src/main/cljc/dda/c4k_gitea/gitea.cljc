(ns dda.c4k-gitea.gitea
 (:require
  [clojure.spec.alpha :as s]
  #?(:cljs [shadow.resource :as rc])
  #?(:clj [orchestra.core :refer [defn-spec]]
     :cljs [orchestra.core :refer-macros [defn-spec]])
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-common.predicate :as pred]
  [dda.c4k-common.postgres :as postgres]))

(s/def ::fqdn pred/fqdn-string?)
(s/def ::issuer pred/letsencrypt-issuer?)

(def config-defaults {:issuer "staging"})

(def config? (s/keys :req-un [::fqdn]
                     :opt-un [::issuer]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password]))

#?(:cljs
   (defmethod yaml/load-resource :gitea [resource-name]
     (case resource-name
       "gitea/appini-configmap.yaml" (rc/inline "gitea/appini-configmap.yaml")
       "gitea/appini-env-configmap.yaml" (rc/inline "gitea/appini-env-configmap.yaml")
       "gitea/deployment.yaml" (rc/inline "gitea/deployment.yaml")
       "gitea/certificate.yaml" (rc/inline "gitea/certificate.yaml")
       "gitea/ingress.yaml" (rc/inline "gitea/ingress.yaml")  
       "gitea/services.yaml" (rc/inline "gitea/services.yaml")
       "gitea/traefik-middleware.yaml" (rc/inline "gitea/traefik-middleware.yaml")
       "gitea/volumes.yaml" (rc/inline "gitea/volumes.yaml")       
       (throw (js/Error. "Undefined Resource!")))))

#?(:cljs
   (defmethod yaml/load-as-edn :gitea [resource-name]
     (yaml/from-string (yaml/load-resource resource-name))))
 
(defn-spec generate-appini-env pred/map-or-seq?
  ; TODO: fix this to require the merged spec of auth and config instead of any
  [config any?]
  (let [{:keys [postgres-db-user postgres-db-password fqdn]} config]
    (->
     (yaml/load-as-edn "gitea/appini-env-configmap.yaml")
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn)
     (cm/replace-all-matching-values-by-new-value "URL" (str "https://" fqdn))
     (cm/replace-all-matching-values-by-new-value "DBUSER" postgres-db-user)
     (cm/replace-all-matching-values-by-new-value "DBPW" postgres-db-password))))

(defn-spec generate-ingress pred/map-or-seq? 
  [config config?]
  (let [{:keys [fqdn issuer]} config]
    (->
     (yaml/load-as-edn "gitea/ingress.yaml")
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn))))
  
(defn-spec generate-certificate pred/map-or-seq?
  [config config?]
  (let [{:keys [fqdn issuer]
         :or {issuer "staging"}} config
        letsencrypt-issuer (name issuer)]
    (->
     (yaml/load-as-edn "gitea/certificate.yaml")
     (assoc-in [:spec :issuerRef :name] letsencrypt-issuer)
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn))))