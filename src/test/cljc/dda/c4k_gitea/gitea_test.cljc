(ns dda.c4k-gitea.gitea-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [dda.c4k-gitea.gitea :as cut]))


(deftest should-generate-webserver-deployment
  (is (= {:apiVersion "apps/v1",
          :kind "Deployment",
          :metadata
          {:name "gitea", :namespace "default", :labels {:app "gitea"}},
          :spec
          {:replicas 1,
           :selector {:matchLabels {:app "gitea"}},
           :template
           {:metadata {:name "gitea", :labels {:app "gitea"}},
            :spec
            {:containers
             [{:name "gitea",
               :image "gitea/gitea:1.16.8",
               :imagePullPolicy "Always",
               :env
               [{:name "GITEA__service__DISABLE_REGISTRATION", :value "true"}
                {:name "GITEA__repository__DEFAULT_PRIVATE", :value "private"}
                {:name "GITEA__service__ENABLE_CAPTCHA", :value "true"}
                {:name "GITEA__database__DB_TYPE", :value "postgres"}
                {:name "GITEA__database__HOST",
                 :value "postgresql-service.postgres.svc.cluster.local:5432"}
                {:name "GITEA__database__NAME", :value "postgres"}
                {:name "GITEA__database__USER", :value "pg-user"}
                {:name "GITEA__database__PASSWD", :value "pg-pw"}],
               :volumeMounts
               [{:name "app-ini-config-volume",
                 :mountPath "/tmp/app.ini",
                 :subPath "app.ini"}
                {:name "gitea-root-volume", :mountPath "/var/lib/gitea"}
                {:name "gitea-data-volume", :mountPath "/data"}],
               :ports
               [{:containerPort 22, :name "git-ssh"}
                {:containerPort 3000, :name "gitea"}]}],
             :volumes
             [{:name "app-ini-config-volume",
               :configMap {:name "gitea-app-ini-config"}}
              {:name "gitea-root-volume",
               :persistentVolumeClaim {:claimName "gitea-root-pvc"}}
              {:name "gitea-data-volume",
               :persistentVolumeClaim {:claimName "gitea-data-pvc"}}]}}}}
         (cut/generate-deployment {:fqdn "test.com" :issuer :staging :postgres-db-user "pg-user" :postgres-db-password "pg-pw"}))))


(deftest should-generate-ingress
  (is (= {:apiVersion "networking.k8s.io/v1",
          :kind "Ingress",
          :metadata
          {:name "ingress-gitea",
           :namespace "default",
           :annotations
           {:kubernetes.io/ingress.class "traefik",
            :cert-manager.io/cluster-issuer "staging"}},
          :spec
          {:tls [{:hosts ["test.com"], :secretName "gitea-ingress-cert"}],
           :rules
           [{:host "test.com",
             :http
             {:paths
              [{:pathType "Prefix",
                :path "/",
                :backend
                {:service {:name "gitea-service", :port {:number 3000}}}}]}}]}}
         (cut/generate-ingress {:fqdn "test.com" :issuer :staging}))))


