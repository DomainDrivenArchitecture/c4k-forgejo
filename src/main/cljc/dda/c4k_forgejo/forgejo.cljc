(ns dda.c4k-forgejo.forgejo
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as st]
   #?(:clj [orchestra.core :refer [defn-spec]]
      :cljs [orchestra.core :refer-macros [defn-spec]])
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-common.common :as cm]
   [dda.c4k-common.ingress :as ing]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-common.predicate :as pred]
   [dda.c4k-common.postgres :as postgres]
   #?(:cljs [dda.c4k-common.macros :refer-macros [inline-resources]])))

(defn domain-list?
  [input]
  (or
   (st/blank? input)
   (pred/string-of-separated-by? pred/fqdn-string? #"," input)))

(defn boolean-from-string [input]
  (cond
    (= input "true") true
    (= input "false") false
    :else nil))

(defn boolean-string?
  [input]
  (and
   (string? input)
   (boolean? (boolean-from-string input))))

(s/def ::default-app-name string?)
(s/def ::fqdn pred/fqdn-string?)
(s/def ::federation-enabled boolean-string?)
(s/def ::mailer-from pred/bash-env-string?)
(s/def ::mailer-host pred/bash-env-string?)
(s/def ::mailer-port pred/bash-env-string?)
(s/def ::service-domain-whitelist domain-list?)
(s/def ::service-noreply-address string?)
(s/def ::forgejo-image string?)
(s/def ::mailer-user pred/bash-env-string?)
(s/def ::mailer-pw pred/bash-env-string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::volume-total-storage-size (partial pred/int-gt-n? 5))
(s/def ::max-rate int?)
(s/def ::max-concurrent-requests int?)

(s/def ::config (s/keys :req-un [::fqdn
                                 ::forgejo-image
                                 ::mailer-from
                                 ::mailer-host
                                 ::mailer-port
                                 ::service-noreply-address
                                 ::volume-total-storage-size
                                 ::max-rate
                                 ::max-concurrent-requests]
                        :opt-un [::issuer
                                 ::federation-enabled
                                 ::default-app-name
                                 ::service-domain-whitelist
                                 ]))

(s/def ::auth (s/keys :req-un [::postgres/postgres-db-user 
                               ::postgres/postgres-db-password 
                               ::mailer-user ::mailer-pw]))

#?(:cljs
   (defmethod yaml/load-resource :forgejo [resource-name]
     (get (inline-resources "forgejo") resource-name)))

(defn generate-appini-env
  [config]
  (let [{:keys [default-app-name
                federation-enabled
                fqdn
                mailer-from
                mailer-host
                mailer-port
                service-domain-whitelist
                service-noreply-address]
         :or {default-app-name "forgejo instance"
              service-domain-whitelist fqdn}} config
        federation-enabled-bool (boolean-from-string federation-enabled)]
    (->
     (yaml/load-as-edn "forgejo/appini-env-configmap.yaml")
     (cm/replace-all-matching "APPNAME" default-app-name)
     (cm/replace-all-matching "FQDN" fqdn)
     (cm/replace-all-matching "URL" (str "https://" fqdn))
     (cm/replace-all-matching "FROM" mailer-from)
     (cm/replace-all-matching "MAILERHOST" mailer-host)
     (cm/replace-all-matching "MAILERPORT" mailer-port)
     (cm/replace-all-matching "WHITELISTDOMAINS" service-domain-whitelist)
     (cm/replace-all-matching "NOREPLY" service-noreply-address)
     (cm/replace-all-matching "IS_FEDERATED"
                              (if federation-enabled-bool
                                "true"
                                "false")))))

(defn-spec generate-secrets pred/map-or-seq?
  [auth ::auth]
  (let [{:keys [postgres-db-user
                postgres-db-password
                mailer-user
                mailer-pw]} auth]
    (->
     (yaml/load-as-edn "forgejo/secrets.yaml")
     (cm/replace-all-matching "DBUSER" (b64/encode postgres-db-user))
     (cm/replace-all-matching "DBPW" (b64/encode postgres-db-password))
     (cm/replace-all-matching "MAILERUSER" (b64/encode mailer-user))
     (cm/replace-all-matching "MAILERPW" (b64/encode mailer-pw)))))

(defn-spec generate-ratelimit-ingress-and-cert pred/map-or-seq?
  [config ::config]
  (let [{:keys [fqdn max-rate max-concurrent-requests namespace]} config]
    (ing/generate-simple-ingress (merge
                                  {:service-name "forgejo-service"
                                   :service-port 3000
                                   :fqdns [fqdn]
                                   :average-rate max-rate
                                   :burst-rate max-concurrent-requests
                                   :namespace namespace}
                                  config))))

(defn-spec generate-data-volume pred/map-or-seq?
  [config ::config]
  (let [{:keys [volume-total-storage-size]} config]
    (->
     (yaml/load-as-edn "forgejo/datavolume.yaml")
     (cm/replace-all-matching "DATASTORAGESIZE" (str (str volume-total-storage-size) "Gi")))))

(defn-spec generate-deployment pred/map-or-seq?
  [config ::config]
  (let [{:keys [forgejo-image]} config]
    (->
     (yaml/load-as-edn "forgejo/deployment.yaml")
     (cm/replace-all-matching "IMAGE_NAME" forgejo-image))))

(defn generate-service
  []
  (yaml/load-as-edn "forgejo/service.yaml"))

(defn generate-service-ssh
  []
  (yaml/load-as-edn "forgejo/service-ssh.yaml"))
