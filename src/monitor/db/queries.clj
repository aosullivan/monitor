(ns monitor.db.queries
  (:require [monitor.db.core :as db]
            [carica.core :refer [config]]))

;eventually these will move and contain the check function
(def checks [{:sc_id 1
              :description "Login to UI as 'larisab'"} 
             {:sc_id 2
              :description "Log into database as service account"}])

(defrecord environment [key description status])

(defn new-environment [env-key desc] (->environment env-key desc ""))

(defn get-environment-id-from-key [key] (db/get-environment-id-from-key {:key (name key)}))

(defrecord service-check [environment_id service_check_id description updated_date status])

(defn new-service-check [env-id sc_id desc] (->service-check env-id sc_id desc (new java.util.Date) "WAITING"))

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
    (db/save-environment<! 
      (new-environment (name env-key) (config env-key :description)))))

(defn setup-checks []
  (for [env env-ids
        check checks]
    (db/save-service-check<! 
      (new-service-check env (:sc_id check) (:description check)))))



