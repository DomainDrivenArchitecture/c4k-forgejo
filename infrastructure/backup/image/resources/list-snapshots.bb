#!/usr/bin/env bb

(require '[dda.backup.core :as bc]
         '[dda.backup.restic :as rc])

(def restic-repo {:password-file (bc/env-or-file "RESTIC_PASSWORD_FILE")
                  :restic-repository (bc/env-or-file "RESTIC_REPOSITORY")})

(defn list-snapshots!
  []
  (rc/list-snapshots! file-config)
  (rc/list-snapshots! db-config))

(list-snapshots!)
