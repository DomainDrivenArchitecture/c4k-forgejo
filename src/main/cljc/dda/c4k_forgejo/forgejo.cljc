(ns dda.c4k-forgejo.forgejo
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
   [dda.c4k-common.ingress :as ing]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-common.predicate :as pred]
   [dda.c4k-common.postgres :as postgres]))

(defn domain-list?
  [input]
  (or
   (st/blank? input)
   (pred/string-of-separated-by? pred/fqdn-string? #"," input)))

(s/def ::default-app-name string?)
(s/def ::forgejo-image-name string?)
(s/def ::fqdn pred/fqdn-string?)
(s/def ::mailer-from pred/bash-env-string?)
(s/def ::mailer-host pred/bash-env-string?)
(s/def ::mailer-port pred/bash-env-string?)
(s/def ::service-domain-whitelist domain-list?)
(s/def ::service-noreply-address string?)
(s/def ::mailer-user pred/bash-env-string?)
(s/def ::mailer-pw pred/bash-env-string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::volume-total-storage-size (partial pred/int-gt-n? 5))

(def config-defaults {:issuer "staging"})

(def config? (s/keys :req-un [::fqdn
                              ::forgejo-image-name
                              ::mailer-from
                              ::mailer-host
                              ::mailer-port
                              ::service-noreply-address
                              ::deploy-federated]
                     :opt-un [::issuer 
                              ::default-app-name 
                              ::service-domain-whitelist]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password ::mailer-user ::mailer-pw]))

(def vol? (s/keys :req-un [::volume-total-storage-size]))

(defn data-storage-by-volume-size
  [total]
  total)

#?(:cljs
   (defmethod yaml/load-resource :forgejo [resource-name]
     (case resource-name
       "forgejo/appini-env-configmap.yaml" (rc/inline "forgejo/appini-env-configmap.yaml")
       "forgejo/deployment.yaml" (rc/inline "forgejo/deployment.yaml")
       "forgejo/secrets.yaml" (rc/inline "forgejo/secrets.yaml")
       "forgejo/service.yaml" (rc/inline "forgejo/service.yaml")
       "forgejo/service-ssh.yaml" (rc/inline "forgejo/service-ssh.yaml")       
       "forgejo/datavolume.yaml" (rc/inline "forgejo/datavolume.yaml")
       (throw (js/Error. "Undefined Resource!")))))

(defn generate-appini-env
  [config]
  (let [{:keys [default-app-name
                fqdn
                mailer-from
                mailer-host
                mailer-port
                service-domain-whitelist
                service-noreply-address]
         :or {default-app-name "forgejo instance"
              service-domain-whitelist fqdn}}
        config]
    (->
     (yaml/load-as-edn "forgejo/appini-env-configmap.yaml")
     (cm/replace-all-matching-values-by-new-value "APPNAME" default-app-name)
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn)
     (cm/replace-all-matching-values-by-new-value "URL" (str "https://" fqdn))
     (cm/replace-all-matching-values-by-new-value "FROM" mailer-from)
     (cm/replace-all-matching-values-by-new-value "MAILERHOST" mailer-host)
     (cm/replace-all-matching-values-by-new-value "MAILERPORT" mailer-port)
     (cm/replace-all-matching-values-by-new-value "WHITELISTDOMAINS" service-domain-whitelist)
     (cm/replace-all-matching-values-by-new-value "NOREPLY" service-noreply-address))))

(defn generate-secrets
  [auth]
  (let [{:keys [postgres-db-user 
                postgres-db-password 
                mailer-user 
                mailer-pw]} auth]
    (->
     (yaml/load-as-edn "forgejo/secrets.yaml")
     (cm/replace-all-matching-values-by-new-value "DBUSER" (b64/encode postgres-db-user))
     (cm/replace-all-matching-values-by-new-value "DBPW" (b64/encode postgres-db-password))
     (cm/replace-all-matching-values-by-new-value "MAILERUSER" (b64/encode mailer-user))
     (cm/replace-all-matching-values-by-new-value "MAILERPW" (b64/encode mailer-pw)))))

(defn generate-ingress-and-cert
  [config]
  (let [{:keys [fqdn]} config]
    (ing/generate-ingress-and-cert
     (merge
      {:service-name "forgejo-service"
       :service-port 3000
       :fqdns [fqdn]}
      config))))

(defn-spec generate-data-volume pred/map-or-seq?
  [config vol?]
  (let [{:keys [volume-total-storage-size]} config        
        data-storage-size (data-storage-by-volume-size volume-total-storage-size)]
    (->     
     (yaml/load-as-edn "forgejo/datavolume.yaml")
     (cm/replace-all-matching-values-by-new-value "DATASTORAGESIZE" (str (str data-storage-size) "Gi")))))
; ToDo: Need to add  federated-image-name to config? Or hardcode?
; ToDo: Need to add  default image-name to config? Or hardcode?
(defn-spec generate-deployment pred/map-or-seq?
  [config config?]
  (let [{:keys [deploy-federated]} config
        deploy-federated-bool (boolean (Boolean/valueOf deploy-federated))]
    (->
     (yaml/load-as-edn "forgejo/deployment.yaml")
     #(if deploy-federated-bool
       (cm/replace-all-matching-values-by-new-value % "IMAGE_NAME" federated-image-name)
       (cm/replace-all-matching-values-by-new-value %"IMAGE_NAME" default-name)))))

(defn generate-service
  []
  (yaml/load-as-edn "forgejo/service.yaml"))

(defn generate-service-ssh
  []
  (yaml/load-as-edn "forgejo/service-ssh.yaml"))
