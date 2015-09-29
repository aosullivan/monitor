(ns monitor.checks.check-fns
  (:require [clojure.java.jdbc :as jdbc]
            [taoensso.timbre :as timbre]
            [carica.core :refer [config]]
            [clj-webdriver.taxi :refer :all]))

  (defn check-select [env] 
    "Check that we can get back a few rows from an arbitrary table in the given environment" 
    ( = 5 (count 
            (jdbc/query 
              (config env :jdbc) 
                        ["select top 5 * from entity order by entity_id desc"]))))

  (defn check-login1 [env] false)
  
  (defn check-login [env]
    "log in to ui with web driver"
    (locking *driver*  ;because webdriver is single threaded, note this constrains the system as a whole
      (try
        (to (config env :webui :url))
        (wait-until #(exists? "#loginForm"))
        (input-text "#username" (config env :webui :username))
        (input-text "#password" (config env :webui :password))
        (click "#btnSignIn")
        (wait-until #(exists? "#modal_popup_div"))
        (exists? "#modal_popup_div")
        true
      (catch  org.openqa.selenium.WebDriverException e
        (timbre/error (.getMessage e)(take-screenshot)) ;TODO access this via ui link on failure, and stop checking till fixed
        false))))


  