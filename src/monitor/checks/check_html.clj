(ns monitor.checks.check-html
  (:require [taoensso.timbre :as timbre]
            [carica.core :refer [config]]
            [clj-webdriver.taxi :refer :all]))

  (defn password-incorrect []
    "check the html response in webdriver for the incorrect pwd notification"
    (let [incorrect-creds-span (find-element {:ng-message "incorrectCreds"})]
            (and (not (nil? incorrect-creds-span))
                 (= (text incorrect-creds-span) "The username/password entered is invalid" ))))
  
  (defn check-login [env]
    "log in to ui with web driver"
    (locking *driver*  ;because webdriver is single threaded, note this constrains system performance as a whole
      (try
        (to (config env :webui :url))
        (when (exists? "#loginForm")
          (input-text "#username" (config env :webui :username))
          (input-text "#password" (config env :webui :password))
          (click "#btnSignIn")
          
          (if (password-incorrect)
            (do (timbre/info "Invalid login credentials on" env ": raising exception")
                (throw (ex-info "Invalid login credentials" {:type :bad-credentials})))
            (do (wait-until #(exists? "#modal_popup_div"))
              (exists? "#modal_popup_div"))))            
          
      (catch  org.openqa.selenium.WebDriverException e
        (timbre/error (.getMessage e)(take-screenshot)) ;TODO access this via ui link on failure
        false))))
