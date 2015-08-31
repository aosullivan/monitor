(ns monitor.checks.check-fns
  (:require [clojure.java.jdbc :as jdbc]
            [carica.core :refer [config]]
            [clj-webdriver.taxi :refer :all]))

  (defn check-select [env] 
    "Check that we can get back a few rows from an arbitrary table in the given environment" 
    ( = 5 (count 
            (jdbc/query 
              (config env :jdbc) 
                        ["select top 5 * from entity order by entity_id desc"]))))

  (defn check-login [env] false)
  
  (defn check-login1 [env] 
      (to "https://q6cl.examen.com/aui/index.html#/login")
    (wait-until #(exists? "#loginForm"))
    (input-text "#username" "larisab")
    (input-text "#password" "go")
    (click "#btnSignIn")
    (try
      (wait-until #(exists? "#modal_popup_div"))
      (exists? "#modal_popup_div")
      (catch  org.openqa.selenium.WebDriverException e
        (take-screenshot) 
        false)))
  
