(ns dda.c4k-forgejo.core-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as st]
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-forgejo.core :as cut]
    #?(:cljs [dda.c4k-common.macros :refer-macros [inline-resources]])))

(st/instrument `cut/config-objects)
(st/instrument `cut/auth-objects)

#?(:cljs
   (defmethod yaml/load-resource :forgejo-test [resource-name]
     (get (inline-resources "forgejo-test") resource-name)))

(deftest validate-valid-resources
  (is (s/valid? ::cut/config (yaml/load-as-edn "forgejo-test/valid-config.yaml")))
  (is (s/valid? ::cut/auth (yaml/load-as-edn "forgejo-test/valid-auth.yaml"))))

(deftest test-whole-generation
  (is (= 35
         (count
          (cut/config-objects []
           (yaml/load-as-edn "forgejo-test/valid-config.yaml")))))
  (is (= 5
         (count
          (cut/auth-objects []
           (yaml/load-as-edn "forgejo-test/valid-config.yaml")
           (yaml/load-as-edn "forgejo-test/valid-auth.yaml"))))))

(deftest test-whole-generation-with-runner
  (is (= 38
         (count
          (cut/config-objects []
                              (yaml/load-as-edn "forgejo-test/valid-config-runner.yaml")))))
  (is (= 6
         (count
          (cut/auth-objects []
                            (yaml/load-as-edn "forgejo-test/valid-config-runner.yaml")
                            (yaml/load-as-edn "forgejo-test/valid-auth-runner.yaml"))))))