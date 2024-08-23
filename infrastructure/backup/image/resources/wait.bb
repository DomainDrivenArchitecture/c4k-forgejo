#!/usr/bin/env bb

(require
 '[dda.backup.core :as bc]
 '[dda.backup.postgresql :as pg])


(def restic-repo {:password-file (bc/env-or-file "RESTIC_PASSWORD_FILE")
                  :restic-repository (bc/env-or-file "RESTIC_REPOSITORY")})

(def db-config (merge restic-repo {:backup-path "pg-database"
                                   :pg-host (bc/env-or-file "POSTGRES_SERVICE")
                                   :pg-port (bc/env-or-file "POSTGRES_PORT")
                                   :pg-db (bc/env-or-file "POSTGRES_DB")
                                   :pg-user (bc/env-or-file "POSTGRES_USER")
                                   :pg-password (bc/env-or-file "POSTGRES_PASSWORD")}))

(defn prepare!
  []
  (pg/create-pg-pass! db-config))

(defn wait! []
  (while true
    (Thread/sleep 1000)))

(prepare!)
(wait!)