(defproject sc3 "0.1.0-SNAPSHOT"
  :description "sc3 app"
  :url "https://clojuresystems.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  ;;  :resources-path "/resources"
  :resource-paths ["resources"]

  :dependencies [
                 ;;                 [slamhound "RELEASE"]
                 [org.clojure/clojure "1.10.1"]
;;                 [javax.servlet/servlet-api "2.5"]
;;                 [ring/ring  "1.8.2"]
                 [ring/ring-defaults  "0.3.2"]
                 [ring/ring-jetty-adapter "1.8.2"]
;;                 [ring/ring-core "1.8.2"]
                 [hiccup "2.0.0-alpha2"]
                 [compojure "1.6.2"]
                 [hickory "0.7.1"]
                 [org.clojure/java.classpath "1.0.0"]
                 ;;                 ^:replace [org.clojure/tools.nrepl "RELEASE"]
                 ;;                 [org.clojure/tools.nrepl "0.2.10"]
                 ;;                 [org.clojure/tools.nrepl "0.2.6"]
                 ;;                 [prismatic/dommy "1.1.0"]
                 [org.clojure/tools.namespace "1.1.0"]
                 ]
  ;;:ring {:handler sc3.app2/site}  

  :main sc3.app2
;;  :aot :all
  :aot [sc3.app2]

;;  :prep-tasks ^:replace []
;;  :repositories {"sonatype-oss-public"
  ;;               "https://oss.sonatype.org/content/groups/public/"}

  ;; :profiles {:dev {:dependencies [[clj-ns-browser "1.3.1"]]}}
  ;;  :plugins [ [cider/cider-nrepl "RELEASE"]
  ;;  :plugins [[cider/cider-nrepl "0.9.0-SNAPSHOT"]]
  ;; :plugins [ [cider/cider-nrepl "0.10.0-SNAPSHOT"]
  ;;            [lein-ring "RELEASE"]]
  ;;             [lein-ring "0.8.11"]]
  ;;  :repl-options {:init-ns user})
  )
