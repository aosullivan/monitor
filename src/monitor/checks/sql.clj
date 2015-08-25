(ns monitor.checks.sql
  (:require
    [clojure.java.jdbc :as jdbc]
    [carica.core :refer [config]]))

(defn db-spec [env]
  (let [get (partial (config env :jdbc))]
  {:classname   (get :classname)
   :subprotocol (get :subprotocol)
   :subname     (get :subname)
   :user        (get :user)
   :password    (get :password) }))

(defn check-select [env] ( = 5 (count (jdbc/query (db-spec env) ["select top 5 * from entity order by entity_id desc"]))))
