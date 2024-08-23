#!/usr/bin/env bb

(require
 '[dda.backup.core :as bc]
 '[dda.backup.restic :as rc])

(def restic-repo {:password-file (bc/env-or-file "RESTIC_PASSWORD_FILE")
                  :restic-repository (bc/env-or-file "RESTIC_REPOSITORY")})

(def file-config (merge restic-repo {:backup-path "files"}))


(def db-config (merge restic-repo {:backup-path "pg-database"}))

(def aws-config {:aws-access-key-id (bc/env-or-file "AWS_ACCESS_KEY_ID")
                 :aws-secret-access-key (bc/env-or-file "AWS_SECRET_ACCESS_KEY")})

(defn prepare!
  []
  (bc/create-aws-credentials! aws-config))

(defn list-snapshots!
  []
  (rc/list-snapshots! file-config)
  (rc/list-snapshots! db-config))

(prepare!)
(list-snapshots!)
