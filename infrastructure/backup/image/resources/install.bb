#!/usr/bin/env bb

(require 
 '[dda.image.ubuntu :as ub]
 '[dda.image.install :as in])


(ub/upgrade-system!)
(in/install! "bb-backup.edn" :target-name "bb.edn" :mod "0400")
(in/install! "backup.bb")
(in/install! "restore.bb")
(in/install! "list-snapshots.bb")
(in/install! "wait.bb")
(ub/cleanup-container!)