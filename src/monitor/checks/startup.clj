(ns monitor.checks.startup
  (:require [monitor.db.core :as db]))

(defrecord service-check [environment_id description updated_date status])

(defn new-service-check [env-id desc] (->service-check env-id desc (new java.util.Date) "WAITING")) ;autogen keys

;eventually these will come from a map that also contains the function to run the check
(def checks ["Log in to UI as larisab" "Log into database as service account"])

(db/reset-service-check-counter!)

(db/delete-service-checks!)

(def env-ids (map :id (db/get-environments)))

;insert a row for each check in each env. 
(for [env env-ids
      check checks] 
  (db/save-service-check<! (new-service-check env check)))   




(db/get-service-checks)



