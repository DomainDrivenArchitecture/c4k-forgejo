#!/usr/bin/env bb

(require '[dda.backup.core :as bc]
         '[dda.backup.restic :as rc]
         '[dda.backup.postgresql :as pg]
         '[dda.backup.backup :as bak])

(def restic-repo {:password-file (bc/env-or-file "RESTIC_PASSWORD_FILE")
                  :restic-repository (bc/env-or-file "RESTIC_REPOSITORY")})

(def file-config (merge restic-repo {:backup-path "files"
                                     :files ["/var/backups/" "gitea/" "git/repositories/"]}))


(def db-config (merge restic-repo {:backup-path "pg-database"
                                   :pg-host (bc/env-or-file "POSTGRES_SERVICE")
                                   :pg-port (bc/env-or-file "POSTGRES_PORT")
                                   :pg-db (bc/env-or-file "POSTGRES_DB")
                                   :pg-user (bc/env-or-file "POSTGRES_USER")
                                   :pg-password (bc/env-or-file "POSTGRES_PASSWORD")}))

(def dry-run {:dry-run true :debug true})

(defn prepare!
  []
  (pg/create-pg-pass! db-config))

(defn restic-repo-init!
  []
  (rc/init! file-config)
  (rc/init! db-config))

(defn restic-backup!
  []
  (bak/backup-file! file-config)
  (bak/backup-db! db-config))

(prepare!)
(restic-repo-init!)
(restic-backup!)