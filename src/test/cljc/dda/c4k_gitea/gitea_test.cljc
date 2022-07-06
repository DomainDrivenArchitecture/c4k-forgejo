(ns dda.c4k-gitea.gitea-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.test.alpha :as st]
   [dda.c4k-common.common-test :as ct]
   [dda.c4k-gitea.gitea :as cut]))

(st/instrument `cut/generate-appini-env)
(st/instrument `cut/generate-ingress)

(deftest should-generate-appini-env
  (is (= {:GITEA__database__USER-c1 nil,
          :GITEA__database__USER-c2 "pg-user",
          :GITEA__database__PASSWD-c1 nil,
          :GITEA__database__PASSWD-c2 "pg-pw",
          :GITEA__server__DOMAIN-c1 nil,
          :GITEA__server__DOMAIN-c2 "test.com",
          :GITEA__server__ROOT_URL-c1 "https://",
          :GITEA__server__ROOT_URL-c2 "https://test.com"}
         (ct/map-diff (cut/generate-appini-env {})
                      (cut/generate-appini-env {:fqdn "test.com" 
                                                :issuer "staging" 
                                                :postgres-db-user "pg-user" 
                                                :postgres-db-password "pg-pw"})))))

(deftest should-generate-certificate
  (is (= {:name-c2 "prod", :name-c1 "staging"}
         (ct/map-diff (cut/generate-certificate {}) 
                      (cut/generate-certificate {:issuer "prod"})))))
