(ns dda.c4k-forgejo.backup-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.test.alpha :as st]
   [dda.c4k-forgejo.backup :as cut]))

(st/instrument `cut/generate-config)

(deftest should-generate-backup-config
  (testing "federated"
    (is (= {:apiVersion "v1",
            :kind "ConfigMap",
            :metadata
            {:name "backup-config",
             :namespace "forgejo",
             :labels
             #:app.kubernetes.io{:name "backup", :part-of "forgejo"}},
            :data {:restic-repository "s3:s3.amazonaws.com/backup/federated-repo"}}
           (cut/generate-config
            {:restic-repository "s3:s3.amazonaws.com/backup/federated-repo"}))))
  (testing "non-federated"
    (is (= {:apiVersion "v1",
            :kind "ConfigMap",
            :metadata
            {:name "backup-config",
             :namespace "forgejo",
             :labels
             #:app.kubernetes.io{:name "backup", :part-of "forgejo"}},
            :data {:restic-repository "s3:s3.amazonaws.com/backup/repo"}}
           (cut/generate-config
            {:restic-repository "s3:s3.amazonaws.com/backup/repo"})))))