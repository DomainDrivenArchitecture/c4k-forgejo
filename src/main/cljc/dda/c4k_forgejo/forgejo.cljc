(ns dda.c4k-forgejo.forgejo
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as st]
   #?(:clj [orchestra.core :refer [defn-spec]]
      :cljs [orchestra.core :refer-macros [defn-spec]])
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-common.common :as cm]
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

(s/def ::default-app-name pred/bash-env-string?)
(s/def ::fqdn pred/fqdn-string?)
(s/def ::federation-enabled boolean-string?)
(s/def ::mailer-from pred/bash-env-string?)
(s/def ::mailer-host pred/bash-env-string?)
(s/def ::mailer-port pred/bash-env-string?)
(s/def ::secret-key string?)
(s/def ::service-domain-whitelist domain-list?)
(s/def ::service-noreply-address pred/bash-env-string?)
(s/def ::forgejo-image string?)
(s/def ::sso-mode #{:none :keycloak-additional})
(s/def ::mailer-user string?)
(s/def ::mailer-pw string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::volume-total-storage-size (partial pred/int-gt-n? 5))
(s/def ::max-rate int?)
(s/def ::max-concurrent-requests int?)

(s/def ::session-lifetime pred/bash-env-string?)
(s/def ::allow-only-external-registration pred/bash-env-string?)
(s/def ::register-email-confirm pred/bash-env-string?)

(s/def ::config (s/keys :req-un [::default-app-name
                                 ::fqdn
                                 ::forgejo-image
                                 ::sso-mode
                                 ::mailer-from
                                 ::mailer-host
                                 ::mailer-port
                                 ::service-noreply-address
                                 ::volume-total-storage-size
                                 ::max-rate
                                 ::max-concurrent-requests]
                        :opt-un [::issuer
                                 ::federation-enabled                                 
                                 ::service-domain-whitelist]))

(s/def ::enhanced-config (s/merge ::config
                                  (s/keys :req-un [::service-domain-whitelist
                                                   ::register-email-confirm
                                                   ::allow-only-external-registration
                                                   ::register-email-confirm])))

(s/def ::auth (s/keys :req-un [::postgres/postgres-db-user
                               ::postgres/postgres-db-password
                               ::mailer-user 
                               ::mailer-pw
                               ::secret-key]))

#?(:cljs
   (defmethod yaml/load-resource :forgejo [resource-name]
     (get (inline-resources "forgejo") resource-name)))

(defn-spec dynamic-config ::enhanced-config
  [config ::config]
  (let [{:keys [fqdn
                sso-mode]} config]
    (merge 
     (-> (if (= :none sso-mode)
           {:session-lifetime "86400"
            :allow-only-external-registration "false"
            :register-email-confirm "true"}
           {:session-lifetime "3600"
            :allow-only-external-registration "true"
            :register-email-confirm "false"})
         (assoc :service-domain-whitelist fqdn))
     config)))

(defn-spec generate-appini-env map?
  [config ::config]
  (let [{:keys [default-app-name
                federation-enabled
                fqdn
                session-lifetime
                allow-only-external-registration
                register-email-confirm
                mailer-from
                mailer-host
                mailer-port
                service-domain-whitelist
                service-noreply-address]} 
        (dynamic-config config)]
    (->
     (yaml/load-as-edn "forgejo/appini-env-configmap.yaml")
     (cm/replace-all-matching "APPNAME" default-app-name)
     (cm/replace-all-matching "FQDN" fqdn)
     (cm/replace-all-matching "URL" (str "https://" fqdn))
     (cm/replace-all-matching "SESSION_LIFETIME" session-lifetime)
     (cm/replace-all-matching "ALLOW_ONLY_EXTERNAL_REGISTRATION" allow-only-external-registration)
     (cm/replace-all-matching "REGISTER_EMAIL_CONFIRM" register-email-confirm)
     (cm/replace-all-matching "FROM" mailer-from)
     (cm/replace-all-matching "MAILERHOST" mailer-host)
     (cm/replace-all-matching "MAILERPORT" mailer-port)
     (cm/replace-all-matching "WHITELISTDOMAINS" service-domain-whitelist)
     (cm/replace-all-matching "NOREPLY" service-noreply-address)
     (cm/replace-all-matching "IS_FEDERATED" federation-enabled))))

(defn-spec generate-secret pred/map-or-seq?
  [auth ::auth]
  (let [{:keys [postgres-db-user
                postgres-db-password
                mailer-user
                mailer-pw
                secret-key]} auth]
    (->
     (yaml/load-as-edn "forgejo/secrets.yaml")
     (cm/replace-all-matching "DBUSER" (b64/encode postgres-db-user))
     (cm/replace-all-matching "DBPW" (b64/encode postgres-db-password))
     (cm/replace-all-matching "MAILERUSER" (b64/encode mailer-user))
     (cm/replace-all-matching "MAILERPW" (b64/encode mailer-pw))
     (cm/replace-all-matching "SECRET_KEY" (b64/encode secret-key)))))

(defn-spec generate-data-volume map?
  [config ::config]
  (let [{:keys [volume-total-storage-size]} config]
    (->
     (yaml/load-as-edn "forgejo/datavolume.yaml")
     (cm/replace-all-matching "DATASTORAGESIZE" (str (str volume-total-storage-size) "Gi")))))

(defn-spec generate-deployment map?
  [config ::config]
  (let [{:keys [forgejo-image]} config]
    (->
     (yaml/load-as-edn "forgejo/deployment.yaml")
     (cm/replace-all-matching "IMAGE_NAME" forgejo-image))))

(defn-spec generate-service map?
  []
  (yaml/load-as-edn "forgejo/service.yaml"))

(defn-spec generate-service-ssh map?
  []
  (yaml/load-as-edn "forgejo/service-ssh.yaml"))

(defn-spec config seq?
  [config ::config]
  [(generate-deployment config)
   (generate-service)
   (generate-service-ssh)
   (generate-data-volume config)
   (generate-appini-env config)])

(defn-spec auth seq?
  [config ::config
   auth ::auth]
  [(generate-secret auth)])