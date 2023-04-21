(ns dda.c4k-forgejo.browser
  (:require
   [clojure.string :as st]
   [clojure.tools.reader.edn :as edn]
   [dda.c4k-forgejo.core :as core]
   [dda.c4k-forgejo.forgejo :as forgejo]
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
      (generate-group
       "domain"
       (cm/concat-vec
        (br/generate-input-field "fqdn" "Your fqdn:" "repo.test.de")
        (br/generate-input-field "mailer-from" "Your mailer email address:" "test@test.de")
        (br/generate-input-field "mailer-host-port" "Your mailer host with port:" "test.de:123")
        (br/generate-input-field "service-noreply-address" "Your noreply domain:" "test.de")
        (br/generate-input-field "issuer" "(Optional) Your issuer prod/staging:" "")
        (br/generate-input-field "app-name" "(Optional) Your app name:" "")
        (br/generate-input-field "domain-whitelist" "(Optional) Domain whitelist for registration email-addresses:" "")))
      (generate-group
       "provider"
       (cm/concat-vec
        (br/generate-input-field "volume-total-storage-size" "Your forgejo volume-total-storage-size:" "20")))
      (generate-group
       "credentials"
       (br/generate-text-area
        "auth" "Your auth.edn:"
        "{:postgres-db-user \"forgejo\"
         :postgres-db-password \"forgejo-db-password\"
         :mailer-user \"test@test.de\"
         :mailer-pw \"mail-test-password\"}"
        "5"))
      [(br/generate-br)]
      (br/generate-button "generate-button" "Generate c4k yaml")))]
   (br/generate-output "c4k-forgejo-output" "Your c4k deployment.yaml:" "25")))

(defn generate-content-div
  []
  {:type :element
   :tag :div
   :content
   (generate-content)})

(defn config-from-document []
  (let [issuer (br/get-content-from-element "issuer" :optional true)
        app-name (br/get-content-from-element "app-name" :optional true)
        domain-whitelist (br/get-content-from-element "domain-whitelist" :optional true)]
    (merge
     {:fqdn (br/get-content-from-element "fqdn")
      :mailer-from (br/get-content-from-element "mailer-from")
      :mailer-host-port (br/get-content-from-element "mailer-host-port")
      :service-noreply-address (br/get-content-from-element "service-noreply-address")
      :volume-total-storage-size (br/get-content-from-element "volume-total-storage-size" :deserializer js/parseInt)}     
     (when (not (st/blank? issuer))
       {:issuer issuer})
     (when (not (st/blank? app-name))
       {:default-app-name app-name})
     (when (not (st/blank? domain-whitelist))
       {:service-domain-whitelist domain-whitelist})
     )))

(defn validate-all! []
  (br/validate! "fqdn" ::forgejo/fqdn)
  (br/validate! "mailer-from" ::forgejo/mailer-from)
  (br/validate! "mailer-host-port" ::forgejo/mailer-host-port)
  (br/validate! "service-noreply-address" ::forgejo/service-noreply-address)
  (br/validate! "issuer" ::forgejo/issuer :optional true)
  (br/validate! "app-name" ::forgejo/default-app-name :optional true)
  (br/validate! "domain-whitelist" ::forgejo/service-domain-whitelist :optional true)  
  (br/validate! "volume-total-storage-size" ::forgejo/volume-total-storage-size :deserializer js/parseInt)
  (br/validate! "auth" forgejo/auth? :deserializer edn/read-string)
  (br/set-form-validated!))

(defn add-validate-listener [name]
  (-> (br/get-element-by-id name)
      (.addEventListener "blur" #(do (validate-all!)))))


(defn init []
  (br/append-hickory (generate-content-div))
  (-> js/document
      (.getElementById "generate-button")
      (.addEventListener "click"
                         #(do (validate-all!)
                              (-> (cm/generate-common
                                   (config-from-document)
                                   (br/get-content-from-element "auth" :deserializer edn/read-string)
                                   forgejo/config-defaults
                                   core/k8s-objects)
                               (br/set-output!)))))
  (add-validate-listener "fqdn")
  (add-validate-listener "mailer-from")
  (add-validate-listener "mailer-host-port")
  (add-validate-listener "service-noreply-address")
  (add-validate-listener "app-name")
  (add-validate-listener "domain-whitelist")  
  (add-validate-listener "volume-total-storage-size")
  (add-validate-listener "issuer")
  (add-validate-listener "auth"))