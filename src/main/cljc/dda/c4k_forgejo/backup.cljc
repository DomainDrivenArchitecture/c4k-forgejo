(ns dda.c4k-forgejo.backup
 (:require
  [clojure.spec.alpha :as s]
  [dda.c4k-common.yaml :as yaml]
  [dda.c4k-common.base64 :as b64]
  [dda.c4k-common.common :as cm]
  #?(:cljs [dda.c4k-common.macros :refer-macros [inline-resources]])))

(s/def ::aws-access-key-id cm/bash-env-string?)
(s/def ::aws-secret-access-key cm/bash-env-string?)
(s/def ::restic-password cm/bash-env-string?)
(s/def ::restic-repository cm/bash-env-string?)

#?(:cljs
   (defmethod yaml/load-resource :backup [resource-name]
     (get (inline-resources "backup") resource-name)))

(defn generate-config [my-conf]
  (let [{:keys [restic-repository]} my-conf]
    (->
     (yaml/from-string (yaml/load-resource "backup/config.yaml"))
     (cm/replace-key-value :restic-repository restic-repository))))

(defn generate-cron []
   (yaml/from-string (yaml/load-resource "backup/cron.yaml")))

(defn generate-backup-restore-deployment [my-conf]
  (let [backup-restore-yaml (yaml/from-string (yaml/load-resource "backup/backup-restore-deployment.yaml"))]
    (if (and (contains? my-conf :local-integration-test) (= true (:local-integration-test my-conf)))
      (cm/replace-named-value backup-restore-yaml "CERTIFICATE_FILE" "/var/run/secrets/localstack-secrets/ca.crt")
      backup-restore-yaml)))

(defn generate-secret [my-auth]
  (let [{:keys [aws-access-key-id aws-secret-access-key restic-password]} my-auth]
    (->
     (yaml/from-string (yaml/load-resource "backup/secret.yaml"))
     (cm/replace-key-value :aws-access-key-id (b64/encode aws-access-key-id))
     (cm/replace-key-value :aws-secret-access-key (b64/encode aws-secret-access-key))
     (cm/replace-key-value :restic-password (b64/encode restic-password)))))
