(ns dda.c4k-gitea.gitea
 (:require
  [clojure.spec.alpha :as s]
  #?(:cljs [shadow.resource :as rc])
  #?(:clj [orchestra.core :refer [defn-spec]]
     :cljs [orchestra.core :refer-macros [defn-spec]])
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.common :as cm]
  [dda.c4k-common.base64 :as b64]
  [dda.c4k-common.predicate :as pred]
  [dda.c4k-common.postgres :as postgres]))

(s/def ::fqdn pred/fqdn-string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::mailer-user pred/bash-env-string?)
(s/def ::mailer-pw pred/bash-env-string?)

(def config-defaults {:issuer "staging"})

(def config? (s/keys :req-un [::fqdn]
                     :opt-un [::issuer]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password ::mailer-user ::mailer-pw]))

#?(:cljs
   (defmethod yaml/load-resource :gitea [resource-name]
     (case resource-name
       "gitea/appini-configmap.yaml" (rc/inline "gitea/appini-configmap.yaml")
       "gitea/appini-env-configmap.yaml" (rc/inline "gitea/appini-env-configmap.yaml")
       "gitea/deployment.yaml" (rc/inline "gitea/deployment.yaml")
       "gitea/certificate.yaml" (rc/inline "gitea/certificate.yaml")
       "gitea/ingress.yaml" (rc/inline "gitea/ingress.yaml")
       "gitea/secrets.yaml" (rc/inline "gitea/secrets.yaml")
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
  (let [{:keys [default-app-name
                fqdn
                mailer-from
                mailer-host-port
                mailer-domain-whitelist
                service-noreply-address]
         :or {default-app-name "Gitea instance"
              mailer-domain-whitelist fqdn}}
        config]
    (->
     (yaml/load-as-edn "gitea/appini-env-configmap.yaml")
     (cm/replace-all-matching-values-by-new-value "APPNAME" default-app-name)
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn)
     (cm/replace-all-matching-values-by-new-value "URL" (str "https://" fqdn))
     (cm/replace-all-matching-values-by-new-value "FROM" mailer-from)
     (cm/replace-all-matching-values-by-new-value "HOSTANDPORT" mailer-host-port)
     (cm/replace-all-matching-values-by-new-value "WHITELISTDOMAINS" mailer-domain-whitelist)
     (cm/replace-all-matching-values-by-new-value "NOREPLY" service-noreply-address))))

(defn-spec generate-secrets pred/map-or-seq?
  [auth auth?]
  (let [{:keys [postgres-db-user postgres-db-password mailer-user mailer-pw]} auth]
    (->
     (yaml/load-as-edn "gitea/secrets.yaml")
     (cm/replace-all-matching-values-by-new-value "DBUSER" (b64/encode postgres-db-user))
     (cm/replace-all-matching-values-by-new-value "DBPW" (b64/encode postgres-db-password))
     (cm/replace-all-matching-values-by-new-value "MAILERUSER" (b64/encode mailer-user))
     (cm/replace-all-matching-values-by-new-value "MAILERPW" (b64/encode mailer-pw)))))

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