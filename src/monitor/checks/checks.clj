(ns monitor.checks.checks
  (:require [clojure.java.jdbc :as jdbc]
            [carica.core :refer [config]]))

(defn check-select [env] 
  "Check that we can get back a few rows from an arbitrary table in the given environment" 
  ( = 5 (count 
          (jdbc/query 
            (config env :jdbc) 
                      ["select top 5 * from entity order by entity_id desc"]))))

; checks are boolean functions that take an env keyword as a param
(def checks [{:sc_id 1
                :description "Login to UI as 'larisab'"
                :check-fn (fn [env] (empty? env))}
               {:sc_id 2
                :description "Log into database as service account"
                :check-fn (fn [env] (check-select env))}])
