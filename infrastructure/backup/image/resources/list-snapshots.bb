#!/usr/bin/env bb

(require
 '[dda.backup.core :as bc]
 '[dda.backup.config :as cfg]
 '[dda.backup.restic :as rc])

(def config (cfg/read-config "/usr/local/bin/config.edn"))

(defn prepare!
  []
  (bc/create-aws-credentials! (:aws-config config)))

(defn list-snapshots!
  []
  (rc/list-snapshots! (:file-config config))
  (rc/list-snapshots! (:db-config config)))

(prepare!)
(list-snapshots!)
