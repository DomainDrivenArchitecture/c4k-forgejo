(ns dda.c4k-gitea.gitea-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [dda.c4k-gitea.gitea :as cut]))


(deftest should-generate-appini-env
  (is (= {:apiVersion "v1",
          :kind "ConfigMap",
          :metadata {:name "gitea-env", :namespace "default"},
          :data
          {:GITEA__database__DB_TYPE "postgres",
           :GITEA__database__HOST
           "postgresql-service.default.svc.cluster.local:5432",
           :GITEA__database__NAME "gitea",
           :GITEA__database__USER "pg-user",
           :GITEA__database__PASSWD "pg-pw",
           :GITEA__server__DOMAIN "test.com",
           :GITEA__server__ROOT_URL "https://test.com"}}
         (cut/generate-appini-env {:fqdn "test.com" :issuer "staging" :postgres-db-user "pg-user" :postgres-db-password "pg-pw"}))))


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
         (cut/generate-ingress {:fqdn "test.com" :issuer "staging"}))))
