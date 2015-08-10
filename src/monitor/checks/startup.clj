(ns monitor.checks.startup
  (:require [monitor.db.core :as db]))

(defrecord service-check [environment_id description updated_date status])

(def service-check1 (->service-check 1 "foo" (new java.util.Date) "WAITING"))

(def service-check2 (->service-check 1 "bar" (new java.util.Date) "WAITING"))

(defn new-service-check [desc] (->service-check 1 desc (new java.util.Date) "WAITING")) ;autogen keys

(def service-check1 (new-service-check "foo")) 

service-check1

(db/delete-service-checks!)

(db/save-service-check<! service-check1)

(db/get-service-checks)                         



