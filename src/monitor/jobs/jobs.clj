(ns monitor.jobs.jobs
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
    "Read config.edn for each environment id"  
    (vec (keys (config))))
  
  (defn enabled-key [env check] 
    "construct the key against which the mutable state of the check is stored in the atom"
    [(keyword (str (:sc_id check))) env])
  
  (def checks-enabled-atom 
    "atomic mutability to allow enabled/disabled state changes" 
    (atom (into {} (for [check checks
                         env envs] [(enabled-key env check) (:enabled check)]))))
  
  (defn is-enabled? [env check]
    "gets the enabled value for the check from the enabled map" 
    (@checks-enabled-atom (enabled-key env check)))
 
  (defn update-status [env check status] 
    "save status to db"
    (queries/update-service-check-status (name env) 
                                         (:sc_id check) 
                                         status 
                                         (new java.util.Date)))
  
  (defn set-disabled [env check]
    "set global atom enabled state"
    (swap! checks-enabled-atom assoc (enabled-key env check) false)  
    (update-status env check "DISABLED")
    (timbre/info "Invalid credentials for " (:description check) env ": disabled check")
    (timbre/debug @checks-enabled-atom))
  
  (defn run-check [env check] 
    "Run check on the target environment and update the status in the db
     Disable the check if exception reports bad credentials so we don't lock any accounts"
    (try 
      (let [status (if ((:check-fn check) env) "OK" "FAILED")]
        (update-status env check status)
        (timbre/info "Ran check:" (name env) (:description check) ":" status))
    (catch clojure.lang.ExceptionInfo e
      (if (= :bad-credentials (-> e ex-data :type))
        (set-disabled env check) 
        (timbre/error e)))))
    
  (defn skip-check [env check]
    (timbre/info "Skipped disabled check:" (name env) (:description check)))
  
  (defn run-check-if-enabled [env check] 
    "Run check if enabled"
    (if (is-enabled? env check)
      (run-check env check)
      (skip-check env check)))
  
  (defn at-interval [interval offset] 
      "Return a joda interval of the specified length and offset"
    (let [at (clj-time.core/plus (clj-time.core/now) 
                                 (clj-time.core/seconds offset)) 
          every (clj-time.core/seconds interval)]
      (clj-time.periodic/periodic-seq at every)))

  (defn schedule-job [env check offset]
    "Schedule the job to run at an interval at a future time"
    (let [check-job (joda/schedule-seq #(run-check-if-enabled env check) 
                                       (at-interval (:interval check) offset))]
       (sch/schedule
         (fn []
           (timbre/info "unscheduling" check-job)
           (sch/stop check-job))
         (in 120 :seconds))))  ;this isn't working

   (defn start-jobs [] 
     "Start all jobs with time offset increments for each successive job to spread them out."
     (let [i (atom 0)]
         (doseq [check checks
                 env envs]
           (if (not (is-enabled? env check))
             (update-status env check "DISABLED"))
           (swap! i inc)  
           (timbre/info "Scheduling job:" env (:description check) 
                        "every" (:interval check) "secs, starting in" (* 10 @i) "secs")
           (schedule-job env 
                         check 
                         (* 10 @i)))))

;(def env :qa6)
;(def check (first checks))
;(schedule-job :qa6 (first @checks-atom) 10)
;(assoc checks :enabled false)

