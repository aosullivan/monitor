(ns monitor.checks.jobs
  (:require [monitor.db.queries :as queries]
            [monitor.checks.sql :as sql]
            [taoensso.timbre :as timbre]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.daily-interval :refer [schedule monday-through-friday
                                                                    starting-daily-at time-of-day ending-daily-at
                                                                    with-interval-in-seconds]]))     
  
  (defjob SqlCheck
    [ctx]
    
    (let [status (if (sql/check-select :qa6) "OK!" "BROKEN")]
      (queries/update-service-check-status 3 status)
      (timbre/info "Checked q6 database connection:" status)))
  
  (defn start-jobs []  
    (let [s   (-> (qs/initialize) qs/start)
          job (j/build
              (j/of-type SqlCheck)
              (j/with-identity (j/key "jobs.sqlcheck.1")))
          trigger (t/build
                  (t/with-identity (t/key "triggers.mon.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                  (with-interval-in-seconds 5))))]
    (qs/schedule s job trigger)))