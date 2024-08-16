#!/usr/bin/env bb

(require '[babashka.tasks :as tasks]
         '[dda.backup.management :as mgm]
         '[dda.backup.backup :as bak]
         '[dda.backup.restore :as rs])

(defn restic-repo-init!
  []
  (spit "restic-pwd" "ThePassword")
  (mgm/init! {:password-file "restic-pwd"
              :restic-repository "restic-repo"}))

(defn restic-backup!
  []
  (tasks/shell "mkdir" "-p" "test-backup")
  (spit "test-backup/file" "I was here")
  (bak/backup! {:password-file "restic-pwd"
                :restic-repository "restic-repo"
                :files ["test-backup"]}))

(defn restic-restore!
  []
  (tasks/shell "mkdir" "-p" "test-restore")
  (rs/restore! {:password-file "restic-pwd"
                :restic-repository "restic-repo"
                :target-directory "test-restore"}))

(restic-repo-init!)
(restic-backup!)
(restic-restore!)
