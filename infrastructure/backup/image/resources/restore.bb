#!/usr/bin/env bb

(require '[babashka.tasks :as tasks]
         '[dda.backup.core :as bc]
         '[dda.backup.config :as cfg]
         '[dda.backup.postgresql :as pg]
         '[dda.backup.restore :as rs])

(def config (cfg/read-config "/usr/local/bin/config.edn"))

(def file-config (merge
                  (:restic-repo config)
                  {:backup-path "files"
                   :restore-target-directory "/var/backups/restore"
                   :snapshot-id "latest"}))

(def db-config (merge (:db-config config)
                      {:snapshot-id "latest"}))


(def dry-run {:dry-run true :debug true})

(defn prepare!
  []
  (bc/create-aws-credentials! (:aws-config config))
  (pg/create-pg-pass! db-config))

(defn restic-restore!
  []
  (rs/restore-file! file-config)
  (tasks/shell ["bash" "-c" "rm -rf /var/backups/gitea/*"])
  (tasks/shell ["bash" "-c" "rm -rf /var/backups/git/repositories/*"])
  (tasks/shell ["mv" "/var/backups/restore/gitea" "/var/backups/"])
  (tasks/shell ["mv" "/var/backups/restore/git/repositories" "/var/backups/git/"])
  (tasks/shell ["chown" "-R" "1000:1000" "/var/backups"])
  (pg/drop-create-db! db-config)
  (rs/restore-db! db-config))

(prepare!)
(restic-restore!)
