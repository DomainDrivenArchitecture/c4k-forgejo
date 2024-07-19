(ns dda.c4k-forgejo.uberjar
  (:gen-class)
  (:require
   [dda.c4k-forgejo.core :as core]
   [dda.c4k-common.uberjar :as uberjar]))

(set! *warn-on-reflection* true)

(defn -main [& cmd-args]
  (uberjar/main-cm
   "c4k-forgejo"
   core/config?
   core/auth?
   core/config-defaults
   core/config-objects
   core/auth-objects
   cmd-args))
