(ns monitor.checks.jobs
  (:require [monitor.db.queries :as queries]
            [monitor.checks.sql :as sql]
            [taoensso.timbre :as timbre]
            [immutant.scheduling :refer (every at in until limit cron) :as sch]
            [immutant.util       :as util]
            [immutant.scheduling.joda :as joda]
            clj-time.core
            clj-time.periodic))     
  
  (defn jdbc-check-job [env] "Run jdbc check on the target environment and update the status in the db"
    (println env)
    (let [status (if (sql/check-select env) "OK" "FAILED")]
      (queries/update-service-check-status 3 status)
      (timbre/info env "database connection:" status)))

  (defn at-interval [interval offset] "Return a joda interval of the specified length and offset"
    (let [at (clj-time.core/plus (clj-time.core/now) 
                                 (clj-time.core/seconds offset)) ;now plus one sec
          every (clj-time.core/seconds interval)]
      (clj-time.periodic/periodic-seq at every)))

;  (defn start-jobs [envs] "Loop environments and kick off checks for each one"
;    (for [env envs
;      :let [job-fn #(jdbc-check-job env)]]
;      (start-job job-fn)))

      
   (defn start-job [env]
    (let [beep (joda/schedule-seq #(jdbc-check-job env) (at-interval 10 0))]
      (sch/schedule
        (fn []
          (println "unscheduling beep & boop")
          (sch/stop beep))
        (in 25 :seconds))))

   (defn start-jobs [envs] 
     (for [env envs]
     (start-job :qa6) ))

