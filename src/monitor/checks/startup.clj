(ns monitor.checks.startup
  (:require [monitor.db.core :as db]))

(defrecord service-check [environment_id description updated_date status])

(defn new-service-check [env-id desc] (->service-check env-id desc (new java.util.Date) "WAITING")) 

;eventually these will come from a map that also contains the function to run the check
(def check-descs ["Log in to UI as larisab" "Log into database as service account"])

(db/reset-service-check-counter!)

(db/delete-service-checks!)

(def env-ids (map :id (db/get-environments)))

;insert a row for each check in each env. 
(for [env env-ids
      check-desc check-descs] 
  (db/save-service-check<! (new-service-check env check-desc)))   

(db/get-service-checks)



