(ns dda.c4k-gitea.gitea
  (:require
   [clojure.spec.alpha :as s]   
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

(s/def ::default-app-name string?)
(s/def ::fqdn pred/fqdn-string?)
(s/def ::mailer-from pred/bash-env-string?)
(s/def ::mailer-host-port pred/host-and-port-string?)
(s/def ::service-domain-whitelist #(pred/string-of-separated-by? pred/fqdn-string? #"," %))
(s/def ::service-noreply-address string?)
(s/def ::mailer-user pred/bash-env-string?)
(s/def ::mailer-pw pred/bash-env-string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::volume-total-storage-size int?) ;TODO extend this for checking lower size limits

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

(defn root-storage-by-volume-size
  [in]
  (cond
    (<= in 5) (throw (Exception. "Volume smaller or equal 5Gi!\nIncrease volume-total-storage-size to value > 5"))
    (and (> in 5) (<= in 20)) 5
    (and (> in 20) (<= in 100)) 10
    (> in 100) 20))

(defn data-storage-by-volume-size
  [total root]
  (cond
    (and (<= total 20) (> total 5)) (- total root)
    (and (<= total 100) (> total 20)) (- total root)
    (> total 100) (- total root)))


#?(:cljs
   (defmethod yaml/load-resource :gitea [resource-name]
     (case resource-name
       "gitea/appini-env-configmap.yaml" (rc/inline "gitea/appini-env-configmap.yaml")
       "gitea/deployment.yaml" (rc/inline "gitea/deployment.yaml")
       "gitea/certificate.yaml" (rc/inline "gitea/certificate.yaml")
       "gitea/ingress.yaml" (rc/inline "gitea/ingress.yaml")
       "gitea/secrets.yaml" (rc/inline "gitea/secrets.yaml")
       "gitea/services.yaml" (rc/inline "gitea/services.yaml")
       "gitea/traefik-middleware.yaml" (rc/inline "gitea/traefik-middleware.yaml")
       "gitea/rootvolume.yaml" (rc/inline "gitea/rootvolume.yaml")
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

(defn-spec generate-root-volume pred/map-or-seq?
  [config vol?]
  (let [{:keys [volume-total-storage-size]} config
        root-storage-size (root-storage-by-volume-size volume-total-storage-size)]
    (->
     (yaml/load-as-edn "gitea/rootvolume.yaml")
     (cm/replace-all-matching-values-by-new-value "ROOTSTORAGESIZE" (str (str root-storage-size) "Gi")))))

(defn-spec generate-data-volume pred/map-or-seq?
  [config vol?]
  (let [{:keys [volume-total-storage-size]} config
        root-storage-size (root-storage-by-volume-size volume-total-storage-size)
        data-storage-size (data-storage-by-volume-size volume-total-storage-size root-storage-size)]
    (->     
     (yaml/load-as-edn "gitea/datavolume.yaml")
     (cm/replace-all-matching-values-by-new-value "DATASTORAGESIZE" (str (str data-storage-size) "Gi")))))