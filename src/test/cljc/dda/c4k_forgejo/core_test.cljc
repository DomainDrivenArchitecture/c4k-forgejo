(ns dda.c4k-forgejo.core-test
  (:require
   #?(:cljs [shadow.resource :as rc])
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.alpha :as s]
   [dda.c4k-common.yaml :as yaml]
   [dda.c4k-forgejo.core :as cut]))

#?(:cljs
   (defmethod yaml/load-resource :forgejo-test [resource-name]
     (case resource-name
       "forgejo-test/valid-auth.yaml"   (rc/inline "forgejo-test/valid-auth.yaml")
       "forgejo-test/valid-config.yaml" (rc/inline "forgejo-test/valid-config.yaml")
       (throw (js/Error. "Undefined Resource!")))))

(deftest validate-valid-resources
  (is (s/valid? cut/config? (yaml/load-as-edn "forgejo-test/valid-config.yaml")))
  (is (s/valid? cut/auth? (yaml/load-as-edn "forgejo-test/valid-auth.yaml"))))