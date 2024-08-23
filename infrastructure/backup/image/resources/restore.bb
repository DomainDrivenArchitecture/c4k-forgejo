#!/usr/bin/env bb

(require '[babashka.tasks :as tasks]
         '[dda.backup.core :as bc]
         '[dda.backup.postgresql :as pg]
         '[dda.backup.restore :as rs])

(def restic-repo {:password-file (bc/env-or-file "RESTIC_PASSWORD_FILE")
                  :restic-repository (bc/env-or-file "RESTIC_REPOSITORY")})

(def file-config (merge restic-repo {:backup-path "files"
                                     :restore-target-directory "/var/backups/restore"
                                     :snapshot-id "latest"}))


(def db-config (merge restic-repo {:backup-path "pg-database"
                                   :pg-host (bc/env-or-file "POSTGRES_SERVICE")
                                   :pg-port (bc/env-or-file "POSTGRES_PORT")
                                   :pg-db (bc/env-or-file "POSTGRES_DB")
                                   :pg-user (bc/env-or-file "POSTGRES_USER")
                                   :pg-password (bc/env-or-file "POSTGRES_PASSWORD")
                                   :snapshot-id "latest"}))

(def aws-config {:aws-access-key-id (bc/env-or-file "AWS_ACCESS_KEY_ID")
                 :aws-secret-access-key (bc/env-or-file "AWS_SECRET_ACCESS_KEY")})

(def dry-run {:dry-run true :debug true})

(defn prepare!
  []
  (pg/create-pg-pass! db-config)
  (bc/create-aws-credentials! aws-config))

(defn restic-restore!
  []
  (rs/restore-file! file-config)
  (tasks/sh "bash" "-c" "rm -rf /var/backups/gitea/*")
  (tasks/sh "bash" "-c" "rm -rf /var/backups/git/repositories/*")
  (tasks/sh "mv" "/var/backups/restore/gitea" "/var/backups/")
  (tasks/sh "mv" "/var/backups/restore/git/repositories" "/var/backups/git/")
  (tasks/sh "chown" "-R" "1000:1000" "/var/backups")
  (pg/drop-create-db! db-config)
  (rs/restore-db! db-config))

(prepare!)
(restic-restore!)
