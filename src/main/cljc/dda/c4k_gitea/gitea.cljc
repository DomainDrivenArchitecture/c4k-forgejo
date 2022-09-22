(ns dda.c4k-gitea.gitea
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as st]
   #?(:cljs [shadow.resource :as rc])
   #?(:clj [orchestra.core :refer [defn-spec]]
      :cljs [orchestra.core :refer-macros [defn-spec]])
   #?(:clj [clojure.edn :as edn]
      :cljs [cljs.reader :as edn])
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-common.common :as cm]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-common.predicate :as pred]
   [dda.c4k-common.postgres :as postgres]))

(defn domain-list?
  [input]
  (or
   (st/blank? input)
   (pred/string-of-separated-by? pred/fqdn-string? #"," input)))

(s/def ::default-app-name string?)
(s/def ::fqdn pred/fqdn-string?)
(s/def ::mailer-from pred/bash-env-string?)
(s/def ::mailer-host-port pred/host-and-port-string?)
(s/def ::service-domain-whitelist domain-list?)
(s/def ::service-noreply-address string?)
(s/def ::mailer-user pred/bash-env-string?)
(s/def ::mailer-pw pred/bash-env-string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::volume-total-storage-size (partial pred/int-gt-n? 5))

(def config-defaults {:issuer "staging"})

(def config? (s/keys :req-un [::fqdn 
                              ::mailer-from 
                              ::mailer-host-port 
                              ::service-noreply-address]
                     :opt-un [::issuer 
                              ::default-app-name 
                              ::service-domain-whitelist]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password ::mailer-user ::mailer-pw]))

(def vol? (s/keys :req-un [::volume-total-storage-size]))

(defn data-storage-by-volume-size
  [total]
  total)


#?(:cljs
   (defmethod yaml/load-resource :gitea [resource-name]
     (case resource-name
       "gitea/appini-env-configmap.yaml" (rc/inline "gitea/appini-env-configmap.yaml")
       "gitea/deployment.yaml" (rc/inline "gitea/deployment.yaml")
       "gitea/certificate.yaml" (rc/inline "gitea/certificate.yaml")
       "gitea/ingress.yaml" (rc/inline "gitea/ingress.yaml")
       "gitea/secrets.yaml" (rc/inline "gitea/secrets.yaml")
       "gitea/service.yaml" (rc/inline "gitea/service.yaml")
       "gitea/service-ssh.yaml" (rc/inline "gitea/service-ssh.yaml")       
       "gitea/datavolume.yaml" (rc/inline "gitea/datavolume.yaml")
       (throw (js/Error. "Undefined Resource!")))))

#?(:cljs
   (defmethod yaml/load-as-edn :gitea [resource-name]
     (yaml/from-string (yaml/load-resource resource-name))))

(defn-spec generate-appini-env pred/map-or-seq?  
  [config config?]
  (let [{:keys [default-app-name
                fqdn
                mailer-from
                mailer-host-port
                service-domain-whitelist
                service-noreply-address]
         :or {default-app-name "Gitea instance"
              service-domain-whitelist fqdn}}
        config]
    (->
     (yaml/load-as-edn "gitea/appini-env-configmap.yaml")
     (cm/replace-all-matching-values-by-new-value "APPNAME" default-app-name)
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn)
     (cm/replace-all-matching-values-by-new-value "URL" (str "https://" fqdn))
     (cm/replace-all-matching-values-by-new-value "FROM" mailer-from)
     (cm/replace-all-matching-values-by-new-value "HOSTANDPORT" mailer-host-port)
     (cm/replace-all-matching-values-by-new-value "WHITELISTDOMAINS" service-domain-whitelist)
     (cm/replace-all-matching-values-by-new-value "NOREPLY" service-noreply-address))))

(defn-spec generate-secrets pred/map-or-seq?
  [auth auth?]
  (let [{:keys [postgres-db-user 
                postgres-db-password 
                mailer-user 
                mailer-pw]} auth]
    (->
     (yaml/load-as-edn "gitea/secrets.yaml")
     (cm/replace-all-matching-values-by-new-value "DBUSER" (b64/encode postgres-db-user))
     (cm/replace-all-matching-values-by-new-value "DBPW" (b64/encode postgres-db-password))
     (cm/replace-all-matching-values-by-new-value "MAILERUSER" (b64/encode mailer-user))
     (cm/replace-all-matching-values-by-new-value "MAILERPW" (b64/encode mailer-pw)))))

(defn-spec generate-ingress pred/map-or-seq?
  [config config?]
  (let [{:keys [fqdn]} config]
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

(defn-spec generate-data-volume pred/map-or-seq?
  [config vol?]
  (let [{:keys [volume-total-storage-size]} config        
        data-storage-size (data-storage-by-volume-size volume-total-storage-size)]
    (->     
     (yaml/load-as-edn "gitea/datavolume.yaml")
     (cm/replace-all-matching-values-by-new-value "DATASTORAGESIZE" (str (str data-storage-size) "Gi")))))

(defn-spec generate-deployment pred/map-or-seq?
  []
  (yaml/load-as-edn "gitea/deployment.yaml"))

(defn-spec generate-service pred/map-or-seq?
  []
  (yaml/load-as-edn "gitea/service.yaml"))

(defn-spec generate-service-ssh pred/map-or-seq?
  []
  (yaml/load-as-edn "gitea/service-ssh.yaml"))
