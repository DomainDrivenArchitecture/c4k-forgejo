#!/usr/bin/env bb

(require '[babashka.tasks :as tasks]
         '[dda.backup.core :as bc]
         '[dda.backup.config :as cfg]
         '[dda.backup.restic :as rc]
         '[dda.backup.postgresql :as pg]
         '[dda.backup.backup :as bak]
         '[dda.backup.restore :as rs])

(def config (cfg/read-config "/usr/local/bin/config.edn"))

(def file-pw-change-config (merge (:file-config config)
                                  {:new-password-file (bc/env-or-file "RESTIC_NEW_PASSWORD_FILE")}))

(def file-restore-config (merge
                          (:restic-repo config)
                          {:backup-path "files"
                           :restore-target-directory "/var/backups/restore"
                           :snapshot-id "latest"}))

(defn prepare!
  []
  (tasks/shell "mkdir" "-p" "/tmp/restic-repo")
  (tasks/shell "mkdir" "-p" "/var/backups/gitea")
  (tasks/shell "mkdir" "-p" "/var/backups/git/repositories")
  (spit "/var/backups/gitea/file" "I was here")
  (tasks/shell "mkdir" "-p" "test-restore"))

(defn restic-repo-init!
  []
  (rc/init! (:file-config config))
  (rc/init! (merge (:db-config config)
                   (:dry-run config))))

(defn restic-backup!
  []
  (bak/backup-file! (:file-config config))
  (bak/backup-db! (merge (:db-config config)
                         (:dry-run config))))

(defn list-snapshots!
  []
  (rc/list-snapshots! (:file-config config))
  (rc/list-snapshots! (merge (:db-config config)
                             (:dry-run config))))


(defn restic-restore!
  []
  (pg/drop-create-db! (merge (:db-config config)
                             (:dry-run config)))
  (rs/restore-db! (merge (:db-config config)
                         (:dry-run config)))
  (rs/restore-file! file-restore-config))

(defn change-password!
  []
  (println "change-password!")
  (rc/change-password! file-pw-change-config))

(prepare!)
(restic-repo-init!)
(restic-backup!)
(list-snapshots!)
(restic-restore!)
(change-password!)
