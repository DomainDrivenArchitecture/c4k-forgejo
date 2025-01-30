(ns dda.c4k-forgejo.browser
  (:require
   [clojure.tools.reader.edn :as edn]
   [dda.c4k-forgejo.core :as core]
   [dda.c4k-common.browser :as br]
   [dda.c4k-common.common :as cm]))

(defn generate-group
  [name
   content]
  [{:type :element
    :tag :div
    :attrs {:class "rounded border border-3  m-3 p-2"}
    :content [{:type :element
               :tag :b
               :attrs {:style "z-index: 1; position: relative; top: -1.3rem;"}
               :content name}
              {:type :element
               :tag :fieldset
               :content content}]}])

(defn generate-content []
  (cm/concat-vec
   [(assoc
     (br/generate-needs-validation) :content
     (cm/concat-vec
      (br/generate-group
       "config"
       (br/generate-text-area
        "config" "Your config.edn:"
        "{:fqdn \"forgejo.your.domain\"
 :mailer-from \"test@test.de\"
 :mailer-host \"test.de\"
 :mailer-port \"25\"                                                                                  
 :deploy-federated \"false\"                                                          
 :service-noreply-address \"no-reply@test.de\"
 :volume-total-storage-size \"20\"
 :restic-repository \"s3://yourbucket/your-repo\"
       :mon-cfg {:cluster-name \"forgejo\"
                       :cluster-stage \"test\"
                       :grafana-cloud-url \"https://prometheus-prod-01-eu-west-0.grafana.net/api/prom/push\"}}"
        "11"))
      (generate-group
       "auth"
       (br/generate-text-area
        "auth" "Your auth.edn:"
        "{:postgres-db-user \"forgejo\"
:postgres-db-password \"forgejo-db-password\"
:mailer-user \"test@test.de\"
:mailer-pw \"mail-test-password\"
:mon-auth {:grafana-cloud-user \"your-user-id\"
           :grafana-cloud-password \"your-cloud-password\"}}"
        "6"))
      [(br/generate-br)]
      (br/generate-button "generate-button" "Generate c4k yaml")))]
   (br/generate-output "c4k-forgejo-output" "Your c4k deployment.yaml:" "25")))

(defn generate-content-div
  []
  {:type :element
   :tag :div
   :content
   (generate-content)})

(defn validate-all! []
  (br/validate! "config" core/config? :deserializer edn/read-string)
  (br/validate! "auth" core/auth? :deserializer edn/read-string)
  (br/set-form-validated!))

(defn add-validate-listener [name]
  (-> (br/get-element-by-id name)
      (.addEventListener "blur" #(do (validate-all!)))))

(defn init []
  (br/append-hickory (generate-content-div))
  (let [config-only false
        auth-only false]
    (-> js/document
        (.getElementById "generate-button")
        (.addEventListener "click"
                           #(do (validate-all!)
                                (-> (cm/generate-cm
                                     (br/get-content-from-element "config" :deserializer edn/read-string)
                                     (br/get-content-from-element "auth" :deserializer edn/read-string)
                                     core/config-defaults
                                     core/config-objects
                                     core/auth-objects
                                     config-only
                                     auth-only)
                                    (br/set-output!))))))
  (add-validate-listener "config")
  (add-validate-listener "auth"))