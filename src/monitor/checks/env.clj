(ns monitor.db.checks
  (:require
    [clojure.java.jdbc :as jdbc]
    [carica.core :refer [config]]))

(defn syb-spec [env]
  (let [get (partial (config env))]
  {:classname   (get :classname)
   :subprotocol (get :subprotocol)
   :subname     (get :subname)
   :user        (get :user)
   :password    (get :password) }))

(jdbc/query (syb-spec :qa6) ["select top 5 * from entity order by entity_id desc"])

