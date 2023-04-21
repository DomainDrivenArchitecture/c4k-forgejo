(ns dda.c4k-forgejo.uberjar
  (:gen-class)
  (:require
   [dda.c4k-forgejo.core :as core]
   [dda.c4k-common.uberjar :as uberjar]))

(defn -main [& cmd-args]
  (uberjar/main-common 
   "c4k-forgejo"
   core/config?
   core/auth?
   core/config-defaults
   core/k8s-objects
   cmd-args))
