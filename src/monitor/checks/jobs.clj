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
  
  (defn jdbc-check-job [env] "Run jdbc check on the target environment and update the status in the db"
    (let [status (if (sql/check-select env) "OK" "FAILED")]
      (queries/update-service-check-status 3 status)
      (timbre/info env "database connection:" status)))

  (defn at-interval [interval offset] "Return a joda interval of the specified length and offset"
    (let [at (clj-time.core/plus (clj-time.core/now) 
                                 (clj-time.core/seconds offset)) ;now plus one sec
          every (clj-time.core/seconds interval)]
      (clj-time.periodic/periodic-seq at every)))

   (defn schedule-jobs [env]
    (let [beep (joda/schedule-seq #(jdbc-check-job env) (at-interval 10 0))]
      (sch/schedule
        (fn []
          (println "unscheduling")
          (sch/stop beep))
        (in 25 :seconds))))

   (def envs (vec (keys (config))))
   
   (defn start-jobs [] "This should be a list comprehension but I can't fathom why it won't work that way"
     (loop [x 0]
       (when (< x (count envs))
         (schedule-jobs (nth envs x))
         (recur (+ x 1)))))

;env id from key   
;(:id (first (queries/get-environment-id-from-key :qa6)))   