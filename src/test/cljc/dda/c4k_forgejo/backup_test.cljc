(ns dda.c4k-forgejo.backup-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.test.alpha :as st]
   [dda.c4k-forgejo.backup :as cut]))

(st/instrument `cut/generate-secret)
(st/instrument `cut/generate-config)

(deftest should-generate-secret
  (is (= {:apiVersion "v1",
          :kind "Secret",
          :metadata {:name "backup-secret", :namespace "forgejo"},
          :type "Opaque",
          :data
          {:aws-access-key-id "YXdzLWlk",
           :aws-secret-access-key "YXdzLXNlY3JldA==",
           :restic-password "cmVzdGljLXB3"}}
         (cut/generate-secret {:aws-access-key-id "aws-id"
                               :aws-secret-access-key "aws-secret"
                               :restic-password "restic-pw"})))
  (is (= {:apiVersion "v1",
          :kind "Secret",
          :metadata {:name "backup-secret", :namespace "forgejo"},
          :type "Opaque",
          :data
          {:aws-access-key-id "YXdzLWlk",
           :aws-secret-access-key "YXdzLXNlY3JldA==",
           :restic-password "b2xk",
           :restic-new-password "bmV3"}}
         (cut/generate-secret {:aws-access-key-id "aws-id"
                               :aws-secret-access-key "aws-secret"
                               :restic-password "old"
                               :restic-new-password "new"}))))

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