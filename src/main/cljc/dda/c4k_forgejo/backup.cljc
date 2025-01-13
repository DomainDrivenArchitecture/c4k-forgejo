(ns dda.c4k-forgejo.backup
  (:require
   [clojure.spec.alpha :as s]
   #?(:clj [orchestra.core :refer [defn-spec]]
      :cljs [orchestra.core :refer-macros [defn-spec]])
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-common.common :as cm]
   [dda.c4k-common.predicate :as p]
   #?(:cljs [dda.c4k-common.macros :refer-macros [inline-resources]])))

(s/def ::aws-access-key-id p/bash-env-string?)
(s/def ::aws-secret-access-key p/bash-env-string?)
(s/def ::restic-password p/bash-env-string?)
(s/def ::restic-new-password p/bash-env-string?)
(s/def ::restic-repository p/bash-env-string?)

(s/def ::config (s/keys :req-un [::restic-repository]))

(s/def ::auth (s/keys :req-un [::restic-password ::aws-access-key-id ::aws-secret-access-key]
                      :opt-un [::restic-new-password]))

#?(:cljs
   (defmethod yaml/load-resource :backup [resource-name]
     (get (inline-resources "backup") resource-name)))

(defn-spec generate-config p/map-or-seq?
  [my-conf ::config]
  (let [{:keys [restic-repository]} my-conf]
    (->
     (yaml/from-string (yaml/load-resource "backup/config.yaml"))
     (cm/replace-key-value :restic-repository restic-repository))))

(defn-spec generate-cron p/map-or-seq?
  []
  (yaml/from-string (yaml/load-resource "backup/cron.yaml")))

(defn-spec generate-backup-restore-deployment p/map-or-seq?
  [my-conf ::config]
  (yaml/from-string (yaml/load-resource "backup/backup-restore-deployment.yaml")))

(defn-spec generate-secret p/map-or-seq?
  [auth ::auth]
  (let [{:keys [aws-access-key-id aws-secret-access-key 
                restic-password restic-new-password]} auth]
    (as-> (yaml/from-string (yaml/load-resource "backup/secret.yaml")) res
      (cm/replace-key-value res :aws-access-key-id (b64/encode aws-access-key-id))
      (cm/replace-key-value res :aws-secret-access-key (b64/encode aws-secret-access-key))
      (cm/replace-key-value res :restic-password (b64/encode restic-password))
      (if (contains? auth :restic-new-password)
        (assoc-in res [:data :restic-new-password] (b64/encode restic-new-password))
        res))))
