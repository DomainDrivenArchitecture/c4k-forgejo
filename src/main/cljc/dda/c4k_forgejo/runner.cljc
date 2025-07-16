(ns dda.c4k-forgejo.runner
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

(s/def ::runner-id string?)
(s/def ::service-name string?)
(s/def ::service-port int?)
(s/def ::runner-token string?)

(s/def ::config (s/keys :req-un [::runner-id
                                 ::service-name
                                 ::service-port]))
(s/def ::auth (s/keys :req-un [::runner-token]))

#?(:cljs
   (defmethod yaml/load-resource :runner [resource-name]
     (get (inline-resources "runner") resource-name)))

(defn-spec generate-configmap map?
  [config ::config]
  (let [{:keys [runner-id]} config]
    (->
     (yaml/load-as-edn "runner/configmap-runner.yaml")
     (cm/replace-all-matching "RUNNER_ID" runner-id))))

(defn-spec generate-secret pred/map-or-seq?
  [auth ::auth]
  (let [{:keys [runner-token]} auth]
    (->
     (yaml/load-as-edn "runner/secret-runner.yaml")
     (cm/replace-all-matching "RUNNER_SECRET" runner-token))))

(defn-spec generate-deployment map?
  [config ::config]
  (let [{:keys [service-name
                service-port]} config]
    (->
     (yaml/load-as-edn "runner/deployment-runner.yaml")
     (cm/replace-all-matching "FORGEJO_SERVICE_URL" (str "http://" service-name ":" service-port)))))

(defn-spec generate-service map?
  []
  (yaml/load-as-edn "runner/service-runner.yaml"))

(defn-spec config seq?
  [config ::config]
  [(generate-deployment config)
   (generate-service)
   (generate-configmap config)])

(defn-spec auth seq?
  [auth ::auth]
  [(generate-secret auth)])
