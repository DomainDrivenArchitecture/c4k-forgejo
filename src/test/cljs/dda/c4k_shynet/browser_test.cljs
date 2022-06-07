(ns dda.c4k-shynet.browser-test
  (:require
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [hickory.render :as hr]
   [dda.c4k-shynet.browser :as cut]))

(deftest should-generate-group
  (is (= "<div class=\"rounded border border-3  m-3 p-2\"><b style=\"z-index: 1; position: relative; top: -1.3rem;\">id1</b><fieldset>content</fieldset></div>"
         (apply hr/hickory-to-html
          (cut/generate-group "id1" "content")))))
