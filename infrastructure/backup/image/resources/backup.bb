#!/usr/bin/env bb

(require
 '[dda.backup.core :as bc]
 '[dda.backup.config :as cfg]
 '[dda.backup.restic :as rc]
 '[dda.backup.postgresql :as pg]
 '[dda.backup.backup :as bak])

(def config (cfg/read-config "/usr/local/bin/config.edn"))

(defn prepare!
  []
  (bc/create-aws-credentials! (:aws-config config))
  (pg/create-pg-pass! (:db-config config)))

(defn restic-repo-init!
  []
  (rc/init! (:file-config config))
  (rc/init! (:db-config config)))

(defn restic-backup!
  []
  (bak/backup-file! (:file-config config))
  (bak/backup-db! (:db-config config)))

(prepare!)
(restic-repo-init!)
(restic-backup!)
