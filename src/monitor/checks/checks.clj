(ns monitor.checks.checks
  (:require [carica.core :refer [config]]
            [monitor.checks.check-fns :refer :all]))

(def checks 
  "Defines an id, description and function for each check.  Functions take an env keyword as a param and return a boolean (pass/fail)"
  [{:sc_id 1
    :description "Login to UI as 'larisab'"
    :check-fn (fn [env] (println "foo" env)  true)}
   {:sc_id 2
    :description "Log into database as service account"
    :check-fn (fn [env] (check-select env))}])
