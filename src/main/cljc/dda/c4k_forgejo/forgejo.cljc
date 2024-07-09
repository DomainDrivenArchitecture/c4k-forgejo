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
(s/def ::deploy-federated boolean-string?)
(s/def ::federation-enabled boolean-string?)
(s/def ::mailer-from pred/bash-env-string?)
(s/def ::mailer-host pred/bash-env-string?)
(s/def ::mailer-port pred/bash-env-string?)
(s/def ::service-domain-whitelist domain-list?)
(s/def ::service-noreply-address string?)
(s/def ::forgejo-image-version-overwrite string?)
(s/def ::mailer-user pred/bash-env-string?)
(s/def ::mailer-pw pred/bash-env-string?)
(s/def ::issuer pred/letsencrypt-issuer?)
(s/def ::volume-total-storage-size (partial pred/int-gt-n? 5))
(s/def ::max-rate int?) 
(s/def ::max-concurrent-requests int?)

(def config? (s/keys :req-un [::fqdn
                              ::mailer-from
                              ::mailer-host
                              ::mailer-port
                              ::service-noreply-address]
                     :opt-un [::issuer
                              ::deploy-federated
                              ::federation-enabled
                              ::default-app-name
                              ::service-domain-whitelist
                              ::forgejo-image-version-overwrite]))

(def rate-limit-config? (s/keys :req-un [::max-rate
                                         ::max-concurrent-requests]))

(def auth? (s/keys :req-un [::postgres/postgres-db-user ::postgres/postgres-db-password ::mailer-user ::mailer-pw]))

(def vol? (s/keys :req-un [::volume-total-storage-size]))

(defn data-storage-by-volume-size
  [total]
  total)

(def federated-image-name "domaindrivenarchitecture/c4k-forgejo-federated")
(def federated-image-version "latest")
(def non-federated-image-name "codeberg.org/forgejo/forgejo")
(def non-federated-image-version "7.0")

(defn-spec generate-image-str string?
  [config config?]
  (let [{:keys [deploy-federated forgejo-image-version-overwrite]} config
        deploy-federated-bool (boolean-from-string deploy-federated)]
    (if deploy-federated-bool
      (str federated-image-name ":" (or forgejo-image-version-overwrite federated-image-version))
      (str non-federated-image-name ":" (or forgejo-image-version-overwrite non-federated-image-version)))))

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
     (cm/replace-all-matching-values-by-new-value "APPNAME" default-app-name)
     (cm/replace-all-matching-values-by-new-value "FQDN" fqdn)
     (cm/replace-all-matching-values-by-new-value "URL" (str "https://" fqdn))
     (cm/replace-all-matching-values-by-new-value "FROM" mailer-from)
     (cm/replace-all-matching-values-by-new-value "MAILERHOST" mailer-host)
     (cm/replace-all-matching-values-by-new-value "MAILERPORT" mailer-port)
     (cm/replace-all-matching-values-by-new-value "WHITELISTDOMAINS" service-domain-whitelist)
     (cm/replace-all-matching-values-by-new-value "NOREPLY" service-noreply-address)
     (cm/replace-all-matching-values-by-new-value "IS_FEDERATED" 
                                                  (if federation-enabled-bool
                                                    "true"
                                                    "false")))))

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

(defn-spec generate-rate-limit-ingress-and-cert pred/map-or-seq?
  [config config?]
  (->
   (generate-ingress-and-cert config) ; returns a vector
   (#(assoc-in % ; Attention: heavily relying on the output order of ing/generate-ingress-and-cert
               [1 :metadata :annotations :traefik.ingress.kubernetes.io/router.middlewares]
               (str
                (-> (second %) :metadata :annotations :traefik.ingress.kubernetes.io/router.middlewares)
                ", default-ratelimit@kubernetescrd")))))


; using :average and :burst seems sensible, :period may be interesting for fine tuning later on
(defn-spec generate-rate-limit-middleware pred/map-or-seq?
  [config rate-limit-config?]
  (let [{:keys [max-rate max-concurrent-requests]} config]
  (->
   (yaml/load-as-edn "forgejo/middleware-ratelimit.yaml")
   (cm/replace-key-value :average max-rate)
   (cm/replace-key-value :burst max-concurrent-requests))))

(defn-spec generate-data-volume pred/map-or-seq?
  [config vol?]
  (let [{:keys [volume-total-storage-size]} config        
        data-storage-size (data-storage-by-volume-size volume-total-storage-size)]
    (->     
     (yaml/load-as-edn "forgejo/datavolume.yaml")
     (cm/replace-all-matching-values-by-new-value "DATASTORAGESIZE" (str (str data-storage-size) "Gi")))))

(defn-spec generate-deployment pred/map-or-seq?
  [config config?]
    (->
     (yaml/load-as-edn "forgejo/deployment.yaml")
     (cm/replace-all-matching-values-by-new-value "IMAGE_NAME" (generate-image-str config))))

(defn generate-service
  []
  (yaml/load-as-edn "forgejo/service.yaml"))

(defn generate-service-ssh
  []
  (yaml/load-as-edn "forgejo/service-ssh.yaml"))
