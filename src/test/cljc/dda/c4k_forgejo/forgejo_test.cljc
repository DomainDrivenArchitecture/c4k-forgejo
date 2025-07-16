(ns dda.c4k-forgejo.forgejo-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.test.alpha :as st]
   [clojure.spec.alpha :as s]
   [dda.c4k-common.test-helper :as th]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-forgejo.forgejo :as cut]))

(st/instrument `cut/dynamic-config)
(st/instrument `cut/generate-deployment)
(st/instrument `cut/generate-appini-env)
(st/instrument `cut/generate-secrets)

(def config {:default-app-name "test forgejo"
             :federation-enabled "true"
             :service-name "forgejo-service"
             :service-port 3000
             :sso-mode :none
             :fqdn "test.com"
             :mailer-from "test@test.com"
             :mailer-host "mail.test.com"
             :mailer-port "456"
             :service-noreply-address "noreply@test.com"
             :forgejo-image "codeberg.org/forgejo/forgejo:8.0.3"
             :volume-total-storage-size 50
             :max-rate 100,
             :max-concurrent-requests 150})

(deftest should-enhance-config-dynamic
  (is (= "86400"
         (get-in (cut/dynamic-config config)
                 [:session-lifetime])))
  (is (= "3600"
         (get-in (cut/dynamic-config (merge config {:sso-mode :keycloak-additional}))
                 [:session-lifetime])))
  (is (= "test.com"
         (get-in (cut/dynamic-config config)
                 [:service-domain-whitelist]))))



(deftest should-generate-appini-env
  (is (= {:APP_NAME "test forgejo",
          :FORGEJO__admin__DEFAULT_EMAIL_NOTIFICATIONS "enabled",
          :FORGEJO__attachments__PATH "/data/gitea/attachments",
          :FORGEJO__database__DB_TYPE "postgres",
          :FORGEJO__database__HOST "postgresql-service:5432",
          :FORGEJO__database__LOG_SQL "false",
          :FORGEJO__database__NAME "forgejo",
          :FORGEJO__database__SSL_MODE "disable",
          :FORGEJO__federation__ENABLED "true",
          :FORGEJO__indexer__ISSUE_INDEXER_PATH
          "/data/gitea/indexers/issues.bleve",
          :FORGEJO__log__LEVEL "Info",
          :FORGEJO__log__MODE "console, file",
          :FORGEJO__log__ROOT_PATH "/data/gitea/log",
          :FORGEJO__mailer__ENABLED "true",
          :FORGEJO__mailer__FROM "test@test.com",
          :FORGEJO__mailer__PROTOCOL "smtp+starttls",
          :FORGEJO__mailer__SMTP_ADDR "mail.test.com",
          :FORGEJO__mailer__SMTP_PORT "456",
          :FORGEJO__oauth2__ENABLED "true",
          :FORGEJO__openid__ENABLE_OPENID "false",
          :FORGEJO__oauth2__REGISTER_EMAIL_CONFIRM "true",
          :FORGEJO__oauth2__ENABLE_AUTO_REGISTRATION "true",
          :FORGEJO__oauth2__ACCOUNT_LINKING "login",
          :FORGEJO__picture__AVATAR_UPLOAD_PATH "/data/gitea/avatars",
          :FORGEJO__picture__DISABLE_GRAVATAR "false",
          :FORGEJO__picture__ENABLE_FEDERATED_AVATAR "true",
          :FORGEJO__picture__REPOSITORY_AVATAR_UPLOAD_PATH
          "/data/gitea/repo-avatars",
          :FORGEJO__repository__DEFAULT_PRIVATE "last",
          :FORGEJO__repository__LOCAL_COPY_PATH "/data/gitea/tmp/local-repo",
          :FORGEJO__repository__ROOT "/data/git/repositories",
          :FORGEJO__repository__TEMP_PATH "/data/gitea/uploads",
          :FORGEJO__security__INSTALL_LOCK "true",
          :FORGEJO__server__DOMAIN "test.com",
          :FORGEJO__server__HTTP_PORT "3000",
          :FORGEJO__server__ROOT_URL "https://test.com",
          :FORGEJO__server__SSH_DOMAIN "test.com",
          :FORGEJO__server__SSH_PORT "2222",
          :FORGEJO__service__ALLOW_ONLY_EXTERNAL_REGISTRATION "false",
          :FORGEJO__service__DEFAULT_ALLOW_CREATE_ORGANIZATION "true",
          :FORGEJO__service__DEFAULT_ENABLE_TIMETRACKING "true",
          :FORGEJO__service__DEFAULT_KEEP_EMAIL_PRIVATE "true",
          :FORGEJO__service__DISABLE_REGISTRATION "false",
          :FORGEJO__service__EMAIL_DOMAIN_ALLOWLIST "test.com",
          :FORGEJO__service__ENABLE_BASIC_AUTHENTICATION "true",
          :FORGEJO__service__ENABLE_CAPTCHA "false",
          :FORGEJO__service__ENABLE_NOTIFY_MAIL "true",
          :FORGEJO__service__NO_REPLY_ADDRESS "noreply@test.com",
          :FORGEJO__service__REGISTER_EMAIL_CONFIRM "true",
          :FORGEJO__service__REQUIRE_SIGNIN_VIEW "false",
          :FORGEJO__session__COOKIE_SECURE "true",
          :FORGEJO__session__PROVIDER "file",
          :FORGEJO__session__PROVIDER_CONFIG "/data/gitea/sessions",
          :FORGEJO__session__SAME_SITE "strict",
          :FORGEJO__session__SESSION_LIFE_TIME "86400",
          :RUN_MODE "prod",
          :RUN_USER "git"}
         (get-in (cut/generate-appini-env config)
                 [:data])))
  (is (= "true"
         (get-in (cut/generate-appini-env (merge config {:sso-mode :keycloak-additional}))
                 [:data :FORGEJO__service__ALLOW_ONLY_EXTERNAL_REGISTRATION])))
   (is (= "false"
         (get-in (cut/generate-appini-env (merge config {:sso-mode :keycloak-additional}))
                 [:data :FORGEJO__service__REGISTER_EMAIL_CONFIRM]))))

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
                 (cut/generate-deployment config)))))

(deftest should-generate-service
  (is (= {:kind "Service",
          :apiVersion "v1",
          :metadata {:name "forgejo-service", :namespace "forgejo"},
          :spec {:selector {:app "forgejo"}, :ports [{:name "forgejo-http", :port 3000}]}}
         (cut/generate-service config))))

(deftest should-generate-secret
  (is (= {:data
          {:FORGEJO__database__PASSWD "cGctcGFzcw==",
           :FORGEJO__database__USER "cGctdXNlcg==",
           :FORGEJO__mailer__PASSWD "bWFpbGVyLXB3",
           :FORGEJO__mailer__USER "bWFpbGVyLXVzZXI=",
           :FORGEJO__security__SECRET_KEY "c2VjcmV0LWtleQ=="},
          :metadata {:name "forgejo-secrets", :namespace "forgejo"},
          :kind "Secret",
          :apiVersion "v1"}
         (cut/generate-secret {:postgres-db-user "pg-user"
                               :postgres-db-password "pg-pass"
                               :mailer-user "mailer-user"
                               :mailer-pw "mailer-pw"
                               :secret-key "secret-key"}))))
