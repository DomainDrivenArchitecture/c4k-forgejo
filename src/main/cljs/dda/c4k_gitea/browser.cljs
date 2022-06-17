(ns dda.c4k-gitea.browser
  (:require
   [clojure.string :as st]
   [clojure.tools.reader.edn :as edn]
   [dda.c4k-gitea.core :as core]
   [dda.c4k-gitea.gitea :as gitea]
   [dda.c4k-common.browser :as br]
   [dda.c4k-common.postgres :as pgc]
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
        (br/generate-input-field "fqdn" "Your fqdn:" "gitea.prod.meissa-gmbh.de")
        (br/generate-input-field "issuer" "(Optional) Your issuer prod/staging:" "")))
      (generate-group
       "provider"
       (cm/concat-vec
        (br/generate-input-field "postgres-data-volume-path" "(Optional) Your postgres-data-volume-path if Persistent Volumes are not generated by an Operator:" "")))
      (generate-group
       "credentials"
       (br/generate-text-area
        "auth" "Your auth.edn:"
        "{:postgres-db-user \"gitea\"
:postgres-db-password \"gitea-db-password\"}"
        "5"))
      [(br/generate-br)]
      (br/generate-button "generate-button" "Generate c4k yaml")))]
   (br/generate-output "c4k-gitea-output" "Your c4k deployment.yaml:" "25")))

(defn generate-content-div
  []
  {:type :element
   :tag :div
   :content
   (generate-content)})

(defn config-from-document []
  (let [postgres-data-volume-path (br/get-content-from-element "postgres-data-volume-path" :optional true)
        issuer (br/get-content-from-element "issuer" :optional true :deserializer keyword)]
    (js/console.log postgres-data-volume-path)
    (merge
     {:fqdn (br/get-content-from-element "fqdn")}
     (when (not (st/blank? postgres-data-volume-path))
       {:postgres-data-volume-path postgres-data-volume-path})
     (when (not (st/blank? issuer))
       {:issuer issuer})
     )))

(defn validate-all! []
  (br/validate! "fqdn" ::gitea/fqdn)
  (br/validate! "issuer" ::gitea/issuer :optional true :deserializer keyword)
  (br/validate! "postgres-data-volume-path" ::pgc/postgres-data-volume-path :optional true)
  (br/validate! "auth" core/auth? :deserializer edn/read-string)
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
                                   core/config-defaults
                                   core/k8s-objects)
                                  (br/set-output!)))))
  (add-validate-listener "fqdn")
  (add-validate-listener "postgres-data-volume-path")
  (add-validate-listener "issuer")
  (add-validate-listener "auth"))