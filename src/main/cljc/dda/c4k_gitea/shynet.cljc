(ns dda.c4k-gitea.gitea
 (:require
  [clojure.spec.alpha :as s]
  #?(:cljs [shadow.resource :as rc])
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-common.predicate :as pred]))

(s/def ::fqdn pred/fqdn-string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::django-secret-key pred/bash-env-string?)

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
 
(defn generate-secret [config]
  (let [{:keys [fqdn django-secret-key postgres-db-user postgres-db-password]} config]
    (->
     (yaml/load-as-edn "gitea/secret.yaml")
     ; TODO: See comment in secret.yaml
     ;(assoc-in [:stringData :ALLOWED_HOSTS] fqdn)
     (assoc-in [:stringData :DJANGO_SECRET_KEY] django-secret-key)
     (assoc-in [:stringData :DB_USER] postgres-db-user)
     (assoc-in [:stringData :DB_PASSWORD] postgres-db-password))))

(defn generate-certificate [config]
  (let [{:keys [fqdn issuer]} config
        letsencrypt-issuer (name issuer)]
    (->
     (yaml/load-as-edn "gitea/certificate.yaml")
     (assoc-in [:spec :commonName] fqdn)
     (assoc-in [:spec :dnsNames] [fqdn])
     (assoc-in [:spec :issuerRef :name] letsencrypt-issuer))))

(defn generate-webserver-deployment []
  (let [gitea-application "gitea-webserver"]
    (-> (yaml/load-as-edn "gitea/deployments.yaml")
        (cm/replace-all-matching-values-by-new-value "gitea-application" gitea-application)
        (update-in [:spec :template :spec :containers 0] dissoc :command))))

(defn generate-celeryworker-deployment []
  (let [gitea-application "gitea-celeryworker"]
    (-> (yaml/load-as-edn "gitea/deployments.yaml")
        (cm/replace-all-matching-values-by-new-value "gitea-application" gitea-application))))

(defn generate-ingress [config]
  (let [{:keys [fqdn issuer]
         :or {issuer :staging}} config
        letsencrypt-issuer (name issuer)]
    (->
     (yaml/load-as-edn "gitea/ingress.yaml")
     (assoc-in [:metadata :annotations :cert-manager.io/cluster-issuer] letsencrypt-issuer)
     (cm/replace-all-matching-values-by-new-value "fqdn" fqdn))))

(defn generate-statefulset []
  (yaml/load-as-edn "gitea/statefulset.yaml"))

(defn generate-service-redis []
  (yaml/load-as-edn "gitea/service-redis.yaml"))

(defn generate-service-webserver []
  (yaml/load-as-edn "gitea/service-webserver.yaml"))
