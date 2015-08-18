(ns monitor.core
  (:require [monitor.handler :refer [app init destroy]]
            [monitor.checks.startup :as startup]
            [qbits.jet.server :refer [run-jetty]]
            [ring.middleware.reload :as reload]
            [monitor.db.migrations :as migrations]
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]])
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
  (when @server
    (destroy)
    (.stop @server)
    (reset! server nil)))

(defn start-app [args]
  
  (timbre/info "Reset service checks: " (startup/reset-checks)) 
  (timbre/info "Setup service checks:" (count (startup/setup-checks)))
  
  (let [port (parse-port args)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (timbre/info "server is starting on port " port)
    (start-server port)))

(defn -main [& args]
  (cond
    (some #{"migrate" "rollback"} args) (migrations/migrate args)
    :else (start-app args)))
  
