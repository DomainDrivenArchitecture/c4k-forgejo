#!/usr/bin/env bb

(require
 '[babashka.tasks :as tasks]
 '[dda.backup.core :as bc]
 '[dda.backup.restic :as rc])

(def restic-repo {:password-file (bc/env-or-file "RESTIC_PASSWORD_FILE")
                  :restic-repository (bc/env-or-file "RESTIC_REPOSITORY")})

(def file-config (merge restic-repo {:backup-path "files"}))


(def db-config (merge restic-repo {:backup-path "pg-database"}))

(defn prepare!
  []
  (tasks/shell ["mkdir" "-p" "/root/.aws"])
  (spit "/root/.aws/credentials"
        (str "[default]\n"
             "aws_access_key_id=" (bc/env-or-file "AWS_ACCESS_KEY_ID") "\n"
             "aws_secret_access_key=" (bc/env-or-file "AWS_SECRET_ACCESS_KEY") "\n")))

(defn list-snapshots!
  []
  (rc/list-snapshots! file-config)
  (rc/list-snapshots! db-config))

(prepare!)
(list-snapshots!)
