(ns dda.c4k-forgejo.core-test
  (:require
   [clojure.test :refer [deftest is are testing run-tests]]
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as st]
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-forgejo.core :as cut]))

(st/instrument `cut/config-objects)
(st/instrument `cut/auth-objects)

(deftest validate-valid-resources
  (is (s/valid? ::cut/config (yaml/load-as-edn "forgejo-test/valid-config.yaml")))
  (is (s/valid? ::cut/auth (yaml/load-as-edn "forgejo-test/valid-auth.yaml")))
  (is (s/valid? ::cut/config (yaml/load-as-edn "runner-test/valid-config-runner.yaml")))
  (is (s/valid? ::cut/auth (yaml/load-as-edn "runner-test/valid-auth-runner.yaml"))))

(deftest test-whole-generation
  (is (= 38
         (count
          (cut/config-objects []
           (yaml/load-as-edn "forgejo-test/valid-config.yaml")))))
  (is (= 5
         (count
          (cut/auth-objects []
           (yaml/load-as-edn "forgejo-test/valid-config.yaml")
           (yaml/load-as-edn "forgejo-test/valid-auth.yaml"))))))

(deftest test-whole-generation-with-runner
  (is (= 42
         (count
          (cut/config-objects []
                              (yaml/load-as-edn "runner-test/valid-config-runner.yaml")))))
  (is (= 6
         (count
          (cut/auth-objects []
                            (yaml/load-as-edn "runner-test/valid-config-runner.yaml")
                            (yaml/load-as-edn "runner-test/valid-auth-runner.yaml"))))))