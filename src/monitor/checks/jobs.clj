(ns monitor.checks.jobs
  (:require [taoensso.timbre :as timbre]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.daily-interval :refer [schedule monday-through-friday
                                                                    starting-daily-at time-of-day ending-daily-at
                                                                    with-interval-in-seconds]]))     
  
  (defjob NoOpJob
    [ctx]
    (timbre/info "Running"))
  
  (defn start-jobs []  
    (let [s   (-> (qs/initialize) qs/start)
          job (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.mon.1")))
          trigger (t/build
                  (t/with-identity (t/key "triggers.mon.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                  (with-interval-in-seconds 3))))]
    (qs/schedule s job trigger)))