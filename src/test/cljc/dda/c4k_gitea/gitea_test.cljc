(ns dda.c4k-gitea.gitea-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.test.alpha :as st]
   [dda.c4k-common.common-test :as ct]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-gitea.gitea :as cut]))

(st/instrument `cut/generate-appini-env)
(st/instrument `cut/generate-ingress)
(st/instrument `cut/generate-secrets)

(deftest should-generate-appini-env
  (is (= {:GITEA__server__DOMAIN-c1 "",
          :GITEA__server__DOMAIN-c2 "test.com",
          :GITEA__server__ROOT_URL-c1 "https://",
          :GITEA__server__ROOT_URL-c2 "https://test.com",
          :GITEA__mailer__FROM-c1 "",
          :GITEA__mailer__FROM-c2 "test@test.com",
          :GITEA__mailer__HOST-c1 "",
          :GITEA__mailer__HOST-c2 "mail.test.com:123",
          :GITEA__service__EMAIL_DOMAIN_WHITELIST-c1 "",
          :GITEA__service__EMAIL_DOMAIN_WHITELIST-c2 "abc.com,def.com"}
         (ct/map-diff (cut/generate-appini-env {:fqdn ""                                                
                                                :mailer-from ""
                                                :mailer-host-port ""                                                
                                                :service-whitelist-domains ""})
                      (cut/generate-appini-env {:fqdn "test.com"                                                 
                                                :mailer-from "test@test.com"
                                                :mailer-host-port "mail.test.com:123"                                                
                                                :service-whitelist-domains "abc.com,def.com"
                                                })))))

(deftest should-generate-certificate
  (is (= {:name-c2 "prod", :name-c1 "staging"}
         (ct/map-diff (cut/generate-certificate {}) 
                      (cut/generate-certificate {:issuer "prod"})))))

(deftest should-generate-secret
  (is (= {:GITEA__database__USER-c1 "",
          :GITEA__database__USER-c2 "pg-user",
          :GITEA__database__PASSWD-c1 "",
          :GITEA__database__PASSWD-c2 "pg-pw",
          :GITEA__mailer__USER-c1 "",
          :GITEA__mailer__USER-c2 (b64/encode "maileruser"),
          :GITEA__mailer__PASSWD-c1 "",
          :GITEA__mailer__PASSWD-c2 (b64/encode "mailerpw")}
         (ct/map-diff (cut/generate-secrets {:postgres-db-user ""
                                             :postgres-db-password ""
                                             :mailer-user ""
                                             :mailer-pw ""})
                      (cut/generate-secrets {:postgres-db-user "pg-user"
                                             :postgres-db-password "pg-pw"
                                             :mailer-user "maileruser"
                                             :mailer-pw "mailerpw"})))))


(not 
 (= 
  {:GITEA__server__DOMAIN-c2 "test.com",
   :GITEA__mailer__FROM-c1 "",
   :GITEA__service__EMAIL_DOMAIN_WHITELIST-c2 "abc.com,def.com",
   :GITEA__service__EMAIL_DOMAIN_WHITELIST-c1 "",
   :GITEA__mailer__HOST-c1 ":",
   :GITEA__mailer__FROM-c2 "test@test.com",
   :GITEA__mailer__HOST-c2 "mail.test.com:123",
   :GITEA__server__ROOT_URL-c2 "https://test.com",
   :GITEA__server__ROOT_URL-c1 "https://",
   :GITEA__server__DOMAIN-c1 ""} 
  {:GITEA__server__DOMAIN-c1 "",
   :GITEA__server__DOMAIN-c2 "test.com",
   :GITEA__server__ROOT_URL-c1 "https://",
   :GITEA__server__ROOT_URL-c2 "https://test.com",
   :GITEA__service__EMAIL_DOMAIN_WHITELIST-c1 "",
   :GITEA__service__EMAIL_DOMAIN_WHITELIST-c2 "abc.com,def.com"}))