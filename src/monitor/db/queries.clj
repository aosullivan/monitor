(ns monitor.db.queries
  (:require [monitor.db.core :as db]
            [carica.core :refer [config]]))

(defrecord environment [key description status])

(defrecord service-check [environment_id description updated_date status])

(defn new-environment [env-key desc] (->environment env-key desc "WAITING"))

(defn new-service-check [env-id desc] (->service-check env-id desc (new java.util.Date) "WAITING"))

;eventually these will come from a map that also contains the function to run the check
(def check-descs ["Login to UI as 'larisab'" "Log into database as service account"])

(def env-ids (map :id (db/get-environments)))

;reset out tables
(defn reset-checks [] (db/reset-environment-counter!)
                      (db/reset-service-check-counter!) 
                      (db/delete-service-checks!)
                      (db/delete-environments!))

;update status
(defn update-service-check-status [id status] (db/update-service-check-status! {:id id :status status}))

(defn setup-envs []
  (for [env-key (keys (config))]
    (db/save-environment<! (new-environment (name env-key) (config env-key :description)))))

(defn setup-checks []
  (for [env env-ids
        check-desc check-descs]
    (db/save-service-check<! (new-service-check env check-desc))))






