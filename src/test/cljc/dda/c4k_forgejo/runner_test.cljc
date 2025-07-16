(ns dda.c4k-forgejo.runner-test
  (:require
   #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
      :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
   [clojure.spec.test.alpha :as st]
   [clojure.spec.alpha :as s]
   [dda.c4k-common.test-helper :as th]
   [dda.c4k-common.base64 :as b64]
   [dda.c4k-forgejo.runner :as cut]))

(st/instrument `cut/dynamic-config)
(st/instrument `cut/generate-deployment)
(st/instrument `cut/generate-appini-env)
(st/instrument `cut/generate-secrets)

(def config {:runner-id "runner"
             :service-name "service"
             :service-port 3000})

(def auth {:runner-token "adefbc345ffaaegb4533"})

(deftest should-generate-deployment
  (is (= [{:name "runner",
           :image "code.forgejo.org/forgejo/runner:7.0.0",
           :imagePullPolicy "IfNotPresent",
           :command ["/bin/bash" "-c"],
           :args
           ["while ! nc -z localhost 2376 </dev/null ; do\n  echo 'waiting for docker daemon...' ;\n  sleep 5 ;\n  done ;\necho \"Done starting runner, registering\"\nforgejo-runner create-runner-file --secret ${RUNNER_TOKEN} --name ${RUNNER_NAME} --instance ${FORGEJO_INSTANCE_URL};\necho \"=== Conf Dir ===\"\necho $(ls -la /conf)\necho \"Done registering, starting runner\"\nforgejo-runner --config /conf/config.yaml daemon\n"],
           :env
           [{:name "DOCKER_HOST", :value "tcp://localhost:2376"}
            {:name "DOCKER_CERT_PATH", :value "/certs/client"}
            {:name "DOCKER_TLS_VERIFY", :value "0"}
            {:name "RUNNER_TOKEN", :valueFrom {:secretKeyRef {:name "runner-secret", :key "token"}}}
            {:name "RUNNER_NAME", :valueFrom {:configMapRef {:name "forgejo-runner-config", :key "runner-id"}}}
            {:name "FORGEJO_INSTANCE_URL", :value "http://service:3000"}],
           :resources
           {:limits {:cpu "1", :ephemeral-storage "3Gi", :memory "4Gi"},
            :requests {:cpu "100m", :ephemeral-storage "0", :memory "64Mi"}},
           :volumeMounts
           [{:name "docker-certs", :mountPath "/certs"}
            {:name "runner-data", :mountPath "/data"}
            {:name "tmp", :mountPath "/tmp"}
            {:name "config", :mountPath "/conf", :readOnly true}],
           :securityContext
           {:allowPrivilegeEscalation false,
            :capabilities {:drop ["ALL"]},
            :privileged false,
            :readOnlyRootFilesystem true,
            :runAsNonRoot true,
            :seccompProfile {:type "RuntimeDefault"}},
           :ports [{:containerPort 80, :name "runner-port"}]}
          {:name "daemon",
           :image "docker.io/docker:28.3.0-dind",
           :env [{:name "DOCKER_TLS_CERTDIR", :value "/certs"}],
           :resources
           {:limits {:cpu "1", :ephemeral-storage "3Gi", :memory "4Gi"},
            :requests {:cpu "100m", :ephemeral-storage "0", :memory "64Mi"}},
           :securityContext {:privileged true},
           :volumeMounts [{:name "docker-certs", :mountPath "/certs"}]}]
         (:containers (:spec (:template (:spec (cut/generate-deployment config))))))))

(deftest should-generate-secret
  (is (= {:apiVersion "v1",
          :kind "Secret",
          :metadata {:name "runner-secret", :namespace "forgejo"},
          :stringData {:token "adefbc345ffaaegb4533"}}
         (cut/generate-secret auth))))

(deftest should-generate-configmap
  (is (= "runner"
         (:runner-id (:data (cut/generate-configmap config))))))