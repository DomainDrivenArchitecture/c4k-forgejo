(ns dda.c4k-forgejo.forgejo-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.test.alpha :as st]
   [dda.c4k-common.test-helper :as th]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-forgejo.forgejo :as cut]))

(st/instrument `cut/generate-deployment)
(st/instrument `cut/generate-appini-env)
(st/instrument `cut/generate-ingress)
(st/instrument `cut/generate-secrets)

(deftest should-generate-appini-env
  (is (= {:APP_NAME-c1 "",
          :APP_NAME-c2 "test forgejo",
          :FORGEJO__federation__ENABLED-c1 "false",
          :FORGEJO__federation__ENABLED-c2 "true",
          :FORGEJO__mailer__FROM-c1 "",
          :FORGEJO__mailer__FROM-c2 "test@test.com",
          :FORGEJO__mailer__SMTP_ADDR-c1 "m.t.de",
          :FORGEJO__mailer__SMTP_ADDR-c2 "mail.test.com",
          :FORGEJO__mailer__SMTP_PORT-c1 "123",
          :FORGEJO__mailer__SMTP_PORT-c2 "456",
          :FORGEJO__server__DOMAIN-c1 "test.de",
          :FORGEJO__server__DOMAIN-c2 "test.com",
          :FORGEJO__server__ROOT_URL-c1 "https://test.de",
          :FORGEJO__server__ROOT_URL-c2 "https://test.com",
          :FORGEJO__server__SSH_DOMAIN-c1 "test.de",
          :FORGEJO__server__SSH_DOMAIN-c2 "test.com",
          :FORGEJO__service__EMAIL_DOMAIN_ALLOWLIST-c1 "adb.de",
          :FORGEJO__service__EMAIL_DOMAIN_ALLOWLIST-c2 "test.com,test.net",
          :FORGEJO__service__NO_REPLY_ADDRESS-c1 "",
          :FORGEJO__service__NO_REPLY_ADDRESS-c2 "noreply@test.com"}
         (th/map-diff (cut/generate-appini-env {:default-app-name ""
                                                :federation-enabled "false"
                                                :fqdn "test.de"
                                                :mailer-from ""
                                                :mailer-host "m.t.de"
                                                :mailer-port "123"
                                                :service-domain-whitelist "adb.de"
                                                :service-noreply-address ""
                                                :forgejo-image "codeberg.org/forgejo/forgejo:8.0.3"})
                      (cut/generate-appini-env {:default-app-name "test forgejo"
                                                :federation-enabled "true"
                                                :fqdn "test.com"
                                                :mailer-from "test@test.com"
                                                :mailer-host "mail.test.com"
                                                :mailer-port "456"
                                                :service-domain-whitelist "test.com,test.net"
                                                :service-noreply-address "noreply@test.com"
                                                :forgejo-image "codeberg.org/forgejo/forgejo:8.0.3"})))))

(deftest should-generate-deployment
  (testing "non-federated"
    (is (= {:apiVersion "apps/v1",
            :kind "Deployment",
            :metadata {:name "forgejo", :namespace "forgejo", :labels {:app "forgejo"}},
            :spec
            {:replicas 1,
             :selector {:matchLabels {:app "forgejo"}},
             :template
             {:metadata {:name "forgejo", :labels {:app "forgejo"}},
              :spec
              {:containers
               [{:name "forgejo",
                 :image "codeberg.org/forgejo/forgejo:8.0.3",
                 :imagePullPolicy "IfNotPresent",
                 :envFrom [{:configMapRef {:name "forgejo-env"}} {:secretRef {:name "forgejo-secrets"}}],
                 :volumeMounts [{:name "forgejo-data-volume", :mountPath "/data"}],
                 :ports [{:containerPort 22, :name "git-ssh"} {:containerPort 3000, :name "forgejo"}]}],
               :volumes [{:name "forgejo-data-volume", :persistentVolumeClaim {:claimName "forgejo-data-pvc"}}]}}}}
           (cut/generate-deployment
            {:default-app-name ""
             :deploy-federated "false"
             :fqdn "test.de"
             :mailer-from ""
             :mailer-host "m.t.de"
             :mailer-port "123"
             :service-domain-whitelist "adb.de"
             :service-noreply-address ""
             :volume-total-storage-size 10
             :max-rate 10
             :max-concurrent-requests 1
             :forgejo-image "codeberg.org/forgejo/forgejo:8.0.3"})))))

(deftest should-generate-secret
  (is (= {:FORGEJO__database__USER-c1 "",
          :FORGEJO__database__USER-c2 (b64/encode "pg-user"),
          :FORGEJO__database__PASSWD-c1 "",
          :FORGEJO__database__PASSWD-c2 (b64/encode "pg-pw"),
          :FORGEJO__mailer__USER-c1 "",
          :FORGEJO__mailer__USER-c2 (b64/encode "maileruser"),
          :FORGEJO__mailer__PASSWD-c1 "",
          :FORGEJO__mailer__PASSWD-c2 (b64/encode "mailerpw")}
         (th/map-diff (cut/generate-secrets {:postgres-db-user ""
                                             :postgres-db-password ""
                                             :mailer-user ""
                                             :mailer-pw ""})
                      (cut/generate-secrets {:postgres-db-user "pg-user"
                                             :postgres-db-password "pg-pw"
                                             :mailer-user "maileruser"
                                             :mailer-pw "mailerpw"})))))
