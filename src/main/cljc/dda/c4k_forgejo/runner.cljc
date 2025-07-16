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
(s/def ::runner-token string?)
