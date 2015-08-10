(ns monitor.checks.startup
  (:require [monitor.db.core :as db]))


(def please-wait "WAITING")

(defrecord check [description status])

(defrecord service-check [id environment_id description updated_date status])


(def check1 (->check "Log in" please-wait))

(defn now [] (new java.util.Date))

(now)

(first (db/get-environments))


(def service-check1 (->service-check 0, 1, (:description check1) (now) (:status check1)))

service-check1

(db/save-service-check!  service-check1)

(db/get-service-checks)                         

(db/delete-service-checks!)

(+ 1 2)