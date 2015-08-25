(ns monitor.checks.jobs
  (:require [monitor.db.queries :as queries]
            [monitor.checks.sql :as sql]
            [carica.core :refer [config]]
            [taoensso.timbre :as timbre]
            [immutant.scheduling :refer (every at in until limit cron) :as sch]
            [immutant.util       :as util]
            [immutant.scheduling.joda :as joda]
            clj-time.core
            clj-time.periodic))     
  
  (defn sql-db-check-job [env]
    (let [status (if (sql/check-select env) "OK" "BROKEN")]
      (queries/update-service-check-status 3 status)
      (timbre/info env " database connection:" status)))

  (defn every-3s-lazy-seq [interval offset]
    (let [at (clj-time.core/plus (clj-time.core/now) 
                                 (clj-time.core/seconds offset)) ;now plus one sec
          every (clj-time.core/seconds interval)]
      (clj-time.periodic/periodic-seq at every)))
  
  (defn start-jobs [] 
    (let [beep (joda/schedule-seq #(sql-db-check-job :qa6) (every-3s-lazy-seq 3 0))
          boop (joda/schedule-seq #(println "boop") (every-3s-lazy-seq 3 2))]
      (sch/schedule
        (fn []
          (println "unscheduling beep & boop")
          (sch/stop beep)
          (sch/stop boop))
        (in 5 :seconds))))

