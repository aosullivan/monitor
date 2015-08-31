(ns monitor.checks.check-fns
  (:require [clojure.java.jdbc :as jdbc]
            [carica.core :refer [config]]))

(defn check-select [env] 
  "Check that we can get back a few rows from an arbitrary table in the given environment" 
  ( = 5 (count 
          (jdbc/query 
            (config env :jdbc) 
                      ["select top 5 * from entity order by entity_id desc"]))))

