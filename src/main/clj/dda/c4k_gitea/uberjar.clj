(ns dda.c4k-gitea.uberjar
  (:gen-class)
  (:require
   [dda.c4k-gitea.core :as core]
   [dda.c4k-gitea.gitea :as gitea]
   [dda.c4k-common.uberjar :as uberjar]))

(defn -main [& cmd-args]
  (uberjar/main-common "c4k-gitea" gitea/config? gitea/auth? gitea/config-defaults core/k8s-objects cmd-args))
