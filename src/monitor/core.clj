(ns monitor.core
  (:require [monitor.handler :refer [app init destroy]]
            [monitor.db.queries :as queries]
            [monitor.checks.checks :refer [checks]]
            [monitor.jobs.jobs :as jobs]
            [qbits.jet.server :refer [run-jetty]]
            [ring.middleware.reload :as reload]
            [monitor.db.migrations :as migrations]
            [clojure.java.shell :refer :all ]
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [immutant.scheduling :as sch]
            [clj-webdriver.taxi :refer [set-driver! implicit-wait]]
            [clj-webdriver.driver :refer [init-driver]])
  (:import (org.openqa.selenium.phantomjs PhantomJSDriver)
           (org.openqa.selenium.remote DesiredCapabilities))
  (:gen-class))

(defonce server (atom nil))

(defn parse-port [[port]]
  (Integer/parseInt (or port (env :port) "3000")))

(defn start-server [port]
  (init)
  (reset! server
          (run-jetty
            {:ring-handler (if (env :dev) (reload/wrap-reload #'app) app)
             :port port
             :join? false})))

(defn stop-server []
  (sch/stop)
  (when @server
    (destroy)
    (.stop @server)
    (reset! server nil)))

(defn start-web-driver [] 
  
  (try
    (sh "phantomjs.exe")
  (catch java.io.IOException e 
    (prn "phantomjs.exe not found on system path: exiting" e)
    (System/exit 0)))
  
  (set-driver!
  (init-driver
      {:webdriver
        (PhantomJSDriver. 
          (doto (DesiredCapabilities.)
                  (.setCapability "phantomjs.cli.args" 
                                  (into-array String ["--ssl-protocol=any"]))))}))
  (implicit-wait 10000))

(defn start-app [args]
  
  (timbre/info "Resetting environment status...") (queries/reset-checks)
  (timbre/info "Setup environments:" (count (queries/setup-envs)))
  (timbre/info "Setup service checks:" (count (queries/setup-checks checks)))
  (start-web-driver)
  (jobs/start-jobs)
  
  (let [port (parse-port args)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (timbre/info "server is starting on port " port)
    (start-server port)))

(defn -main [& args]
  (cond
    (some #{"migrate" "rollback"} args) (migrations/migrate args)
    :else (start-app args)))
  