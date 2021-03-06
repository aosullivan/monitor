(defproject monitor "0.1.0-SNAPSHOT"

  :description "Pings various http and jdbc services and displays results"
  :url "https://github.com/aosullivan/monitor"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [selmer "0.8.2"]
                 [com.taoensso/timbre "4.0.2"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.67"]
                 [environ "1.0.0"]
                 [compojure "1.3.4"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring "1.4.0-RC2"
                  :exclusions [ring/ring-jetty-adapter]]
                 [ring-server "0.4.0"]
                 [cc.qbits/jet "0.6.5"]
                 [metosin/ring-middleware-format "0.6.0"]
                 [metosin/ring-http-response "0.6.2"]
                 [bouncer "0.3.3"]
                 [prone "0.8.2"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [lib-noir "0.9.9"]
                 [migratus "0.8.2"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [instaparse "1.4.1"]
                 [yesql "0.5.0-rc3"]
                 [com.h2database/h2 "1.4.187"]
                 [com.sybase/jconn4 "26792"]
                 [sonian/carica "1.1.0"]
                 [org.immutant/immutant "2.0.2"]
                 [clj-webdriver "0.6.1"]
                 [slingshot "0.12.2"]
                 [org.clojure/core.cache "0.6.3"]
                 [org.clojure/core.memoize "0.5.6" :exclusions [org.clojure/core.cache]]] ;https://github.com/LightTable/LightTable/issues/794

  :min-lein-version "2.0.0"
  :uberjar-name "monitor.jar"
  :jvm-opts ["-server"]

;;enable to start the nREPL server when the application launches
;:env {:repl-port 7001}

  :main monitor.core
  :migratus {:store :database}

  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]
            [migratus-lein "0.1.5"]]
  
  :repositories [["snapshots" "http://bamboo.examen.com:8080/artifactory/repo"]
                 ["releases"  "http://bamboo.examen.com:8080/artifactory/repo"]]
  
  
  :ring {:handler monitor.handler/app
         :init    monitor.handler/init
         :destroy monitor.handler/destroy
         :uberwar-name "monitor.war"}

  
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
             
             :aot :all}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.7.0"]
                        ]
         
         
         :repl-options {:init-ns monitor.core}
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}})
