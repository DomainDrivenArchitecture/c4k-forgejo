#!/usr/bin/env bb

(require '[babashka.tasks :as tasks]
         '[dda.backup.management :as mgm]
         '[dda.backup.backup :as bak])

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
                :restic-repository "restic-repo"}
               ["test-backup"]))

(defn restic-restore!
  []
  (tasks/shell "mkdir" "-p" "test-restore")
  (tasks/shell "restic" "restore" "--password-file" "restic-pwd" "--repo" "restic-repo" "--target" "test-restore" "latest"))

(restic-repo-init!)
(restic-backup!)
(restic-restore!)
