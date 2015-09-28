(ns monitor.checks.jobs
  (:require [monitor.db.queries :as queries]
            [monitor.checks.checks :refer [checks]]
            [taoensso.timbre :as timbre]
            [immutant.scheduling :refer (every at in until limit cron) :as sch]
            [immutant.util       :as util]
            [immutant.scheduling.joda :as joda]
            [carica.core :refer [config]]
            clj-time.core
            clj-time.periodic))     

  (def envs 
    "Read config.edn for each environment config"  
    (vec (keys (config))))
  
  (defn run-check [env check] 
    "Run check on the target environment and update the status in the db"
    (let [status 
          (if ((:check-fn check) env) "OK" "FAILED")]
      (queries/update-service-check-status (name env) 
                                           (:sc_id check) 
                                           status 
                                           (new java.util.Date))
      (timbre/info (:description check) env status)))

  (defn at-interval [interval offset] 
    "Return a joda interval of the specified length and offset"
    (let [at (clj-time.core/plus (clj-time.core/now) 
                                 (clj-time.core/seconds offset)) 
          every (clj-time.core/seconds interval)]
      (clj-time.periodic/periodic-seq at every)))

  (defn schedule-job [env check offset]
    "Schedule the job to run at an interval at a future time"
    (timbre/info "Scheduled:" env (:description check))
     (let [check-job (joda/schedule-seq #(run-check env check) 
                                        (at-interval (:interval check) 
                                                     offset))]
       (sch/schedule
         (fn []
           (timbre/info "unscheduling" check-job)
           (sch/stop check-job))
         (in 60 :seconds))))

   (defn start-jobs [] 
     "Start all jobs with 5s offset increments for each successive job."
     (loop [env-idx 0
            offset 0]
       (when (< env-idx (count envs))
         (doseq [check checks] 
           (schedule-job (nth envs env-idx) 
                         check 
                         offset))
         (recur (+ env-idx 1) 
                (+ offset 10)))))  
   
    