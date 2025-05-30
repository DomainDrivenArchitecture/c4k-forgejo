#!/usr/bin/env bb

(require
 '[dda.backup.monitoring :as mon])

(def config {:metrics {:kube_job_status_active 0
                       :kube_job_status_failed 0
                       :kube_job_status_succeeded 0
                       :kube_job_status_start_time (/ (System/currentTimeMillis) 1000)}})

(mon/send-metrics! config)