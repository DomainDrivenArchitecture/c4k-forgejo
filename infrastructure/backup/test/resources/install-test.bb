#!/usr/bin/env bb

(require '[babashka.tasks :as tasks])

(defn curl-and-check!
  [filename artifact-url sha256-url]
  (let [filepath (str "/tmp/" filename)]
    (tasks/shell "curl" "-SsLo" filepath artifact-url)
    (tasks/shell "curl" "-SsLo" "/tmp/checksum" sha256-url)
    (tasks/shell "bash" "-c" (str "echo \" " filepath "\"|tee -a /tmp/checksum"))
    ;(tasks/shell "sha256sum" "-c" "--status" "/tmp/checksum")
    ))

(defn tar-install!
  [filename binname]
  (let [filepath (str "/tmp/" filename)]
    (tasks/shell "tar" "-C" "/tmp" "-xzf" filepath)
    (tasks/shell "install" "-m" "0700" "-o" "root" "-g" "root" (str "/tmp/" binname) "/usr/local/bin/")))

(defn install!
  [filename]
  (tasks/shell "install" "-m" "0700" "-o" "root" "-g" "root" (str "/tmp/" filename) "/usr/local/bin/"))

(tasks/shell "bb" "/tmp/test.bb")
(curl-and-check!
 "provs-syspec.jar"
 "https://repo.prod.meissa.de/attachments/0a1da41e-aa5b-4a3e-a3b1-215cf2d5b021"
 "https://repo.prod.meissa.de/attachments/f227cf65-cb0f-46a7-a6cd-28f46917412a")
(install! "provs-syspec.jar")
(tasks/shell "apt" "update")
(tasks/shell "apt" "install" "-qqy" "openjdk-17-jre-headless")
(tasks/shell "java" "-jar" "/usr/local/bin/provs-syspec.jar" "local" "-c" "/tmp/spec.yml" )
