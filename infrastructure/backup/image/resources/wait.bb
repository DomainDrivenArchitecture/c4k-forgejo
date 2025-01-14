#!/usr/bin/env bb
(require
 '[dda.backup.core :as bc]
 '[dda.backup.config :as cfg]
 '[dda.backup.postgresql :as pg])

(def config (cfg/read-config "/usr/local/bin/config.edn"))

(defn prepare!
  []
  (bc/create-aws-credentials! (:aws-config config))
  (pg/create-pg-pass! (:db-config config)))

(defn wait! []
  (while true
    (Thread/sleep 1000)))

(prepare!)
(wait!)
