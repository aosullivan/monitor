(ns monitor.checks.checks
  (:require [carica.core :refer [config]]
            [monitor.checks.check-fns :refer :all]))

(def checks 
  "Defines an id, description, interval (secs) and function for each check.  
   Functions take an env keyword as a param and return a boolean (pass/fail)"
  
  [{:sc_id 1
    :description "Login to UI as 'larisab'"
    :interval 25
    :check-fn (fn [env] (check-login env))}
   
   {:sc_id 2
    :description "Log into database as service account and run a query"
    :interval 15
    :check-fn (fn [env] (check-select env))}])
