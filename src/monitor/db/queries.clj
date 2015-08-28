(ns monitor.db.queries
  (:require [monitor.db.core :as db]
            [carica.core :refer [config]]))

(defrecord environment [key description status])

(defrecord service-check [environment_id service_check_id description updated_date status])

(defn new-environment [env-key desc] 
  (->environment env-key desc ""))

(defn get-environment-id-from-key [key] 
  (db/get-environment-id-from-key {:key (name key)}))

(defn new-service-check [env-id sc_id desc] 
  (->service-check env-id 
                   sc_id 
                   desc 
                   (new java.util.Date) 
                   "WAITING"))

(def env-ids 
  (map :id (db/get-environments)))

;reset out tables
(defn reset-checks [] (db/reset-environment-counter!)
                      (db/reset-service-check-counter!) 
                      (db/delete-service-checks!)
                      (db/delete-environments!))

;update status
(defn update-service-check-status [env_key sc_id status updated_date] 
  (db/update-service-check-status! {:env_key env_key 
                                    :sc_id sc_id 
                                    :status status 
                                    :updated_date updated_date}))

(defn setup-envs []
  (for [env-key (keys (config))]
    (db/save-environment<! 
      (new-environment (name env-key) 
                       (config env-key :description)))))

(defn setup-checks [checks]
  (for [env env-ids
        check checks]
    (db/save-service-check<! 
      (new-service-check env 
                         (:sc_id check) 
                         (:description check)))))



