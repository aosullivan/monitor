(ns monitor.checks.jobs
  (:require [monitor.db.queries :as queries]
            [monitor.checks.sql :as sql]
            [taoensso.timbre :as timbre]
            [immutant.scheduling :refer (every at in until limit cron) :as sch]
            [immutant.util       :as util]
            [immutant.scheduling.joda :as joda]
            [carica.core :refer [config]]
            clj-time.core
            clj-time.periodic))     

  ; checks are boolean functions that take an env keyword as a param
  (def checks [{:sc_id 1
                :description "Login to UI as 'larisab'"
                :check-fn (fn [env] (empty? env))}
               {:sc_id 2
                :description "Log into database as service account"
                :check-fn (fn [env] (sql/check-select env))}])
  
  
  (defn run-check [env check] "Run check on the target environment and update the status in the db"
    (let [status (if ((:check-fn check) env) "OK" "FAILED")]
      (queries/update-service-check-status (name env) (:sc_id check) status (new java.util.Date))
      (timbre/info env "database connection:" status)))

  (defn at-interval [interval offset] "Return a joda interval of the specified length and offset"
    (let [at (clj-time.core/plus (clj-time.core/now) 
                                 (clj-time.core/seconds offset)) 
          every (clj-time.core/seconds interval)]
      (clj-time.periodic/periodic-seq at every)))

   (defn schedule-job [env offset]
    (let [beep (joda/schedule-seq #(run-check env (second checks)) (at-interval 10 offset))]
      (sch/schedule
        (fn []
          (println "unscheduling")
          (sch/stop beep))
        (in 35 :seconds))))

   (def envs (vec (keys (config))))
   
   (defn start-jobs [] "Start jobs with 5s offset times.  This should be a list comprehension."
     (loop [env-id 0
            offset 0]
       (when (< env-id (count envs))
         (schedule-job (nth envs env-id) offset)
         (recur (+ env-id 1) (+ offset 5)))))

   ;(run-check :qa6 (second checks)) 
   