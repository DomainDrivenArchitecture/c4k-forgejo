#!/usr/bin/env bb

(require '[babashka.tasks :as tasks]
         '[dda.backup.core :as bc]
         '[dda.backup.restic :as rc]
         '[dda.backup.postgresql :as pg]
         '[dda.backup.backup :as bak]
         '[dda.backup.restore :as rs])

(def restic-repo {:password-file "restic-pwd"
                  :restic-repository "restic-repo"})

(def file-config (merge restic-repo {:backup-path "files"
                                     :files ["test-backup"]
                                     :restore-target-directory "test-restore"}))


(def db-config (merge restic-repo {:backup-path "db"
                                   :pg-db "mydb"
                                   :pg-user "user"
                                   :pg-password "password"}))

(def dry-run {:dry-run true :debug true})

(defn prepare!
  []
  (spit "/tmp/file_password" "file-password")
  (println (bc/env-or-file "FILE_PASSWORD"))
  (println (bc/env-or-file "ENV_PASSWORD"))
  (spit "restic-pwd" "ThePassword")
  (tasks/shell "mkdir" "-p" "test-backup")
  (spit "test-backup/file" "I was here")
  (tasks/shell "mkdir" "-p" "test-restore")
  (pg/create-pg-pass! db-config))

(defn restic-repo-init!
  []
  (rc/init! file-config)
  (rc/init! (merge db-config dry-run)))

(defn restic-backup!
  []
  (bak/backup-file! file-config)
  (bak/backup-db! (merge db-config dry-run)))

(defn list-snapshots!
  []
  (rc/list-snapshots! file-config)
  (rc/list-snapshots! (merge db-config dry-run)))


(defn restic-restore!
  []
  (rs/restore-file! file-config)
  (pg/drop-create-db! (merge db-config dry-run))
  (rs/restore-db! (merge db-config dry-run)))

(prepare!)
(restic-repo-init!)
(restic-backup!)
(list-snapshots!)
(restic-restore!)
