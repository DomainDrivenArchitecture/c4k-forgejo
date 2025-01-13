#!/usr/bin/env bb
(require
 '[dda.backup.core :as bc]
 '[dda.backup.config :as cfg]
 '[dda.backup.restic :as rc])

(def config (cfg/read-config "/usr/local/bin/config.edn"))

(def file-pw-change-config (merge (:file-config config)
                                  {:new-password-file (bc/env-or-file "RESTIC_NEW_PASSWORD_FILE")}))
(def db-pw-change-config (merge (:db-config config) 
                                {:new-password-file (bc/env-or-file "RESTIC_NEW_PASSWORD_FILE")}))

(defn prepare!
  []
  (bc/create-aws-credentials! (:aws-config config)))

(defn change-password!
  []
  (rc/change-password! file-pw-change-config)
  (rc/change-password! db-pw-change-config))

(prepare!)
(change-password!)
