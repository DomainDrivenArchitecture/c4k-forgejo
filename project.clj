(defproject org.domaindrivenarchitecture/c4k-forgejo "4.1.1-SNAPSHOT"
  :description "forgejo c4k-installation package"
  :url "https://domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.12.0" :scope "provided"]
                 [org.clojure/tools.reader "1.5.0"]
                 [org.domaindrivenarchitecture/c4k-common-clj "9.0.1"]
                 [hickory "0.7.1" :exclusions [viebel/codox-klipse-theme]]]
  :target-path "target/%s/"
  :source-paths ["src/main/cljc"
                 "src/main/clj"]
  :resource-paths ["src/main/resources"
                   "project.clj"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]
                        ["releases" {:sign-releases false :url "https://clojars.org/repo"}]]
  :profiles {:test {:test-paths ["src/test/cljc"]
                    :resource-paths ["src/test/resources"]
                    :dependencies [[dda/data-test "0.1.1"]]}
             :dev {:plugins [[lein-shell "0.5.0"]]}
             :uberjar {:aot :all
                       :main dda.c4k-forgejo.uberjar
                       :uberjar-name "c4k-forgejo-standalone.jar"
                       :dependencies [[org.clojure/tools.cli "1.1.230"]
                                      [ch.qos.logback/logback-classic "1.5.16"
                                       :exclusions [com.sun.mail/javax.mail]]
                                      [org.slf4j/jcl-over-slf4j "2.0.16"]
                                      [com.github.clj-easy/graal-build-time "1.0.5"]]}}
  :release-tasks [["test"]
                  ["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["change" "version" "leiningen.release/bump-version"]])
