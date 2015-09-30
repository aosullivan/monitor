(ns monitor.checks.check-html
  (:require [taoensso.timbre :as timbre]
            [carica.core :refer [config]]
            [clj-webdriver.taxi :refer :all]))

  ;global switch to disable logins so we don't lock accounts
  (def password-ok true)  ;TODO needs to be a map
  
  (defn password-correct []
    "check the html response in webdriver for the incorrect pwd msg"
    (let [incorrectCreds (find-element {:ng-message "incorrectCreds"})]
            (or (nil? incorrectCreds)
                (not= (text incorrectCreds) "The username/password entered is invalid" ))))
  
  (defn check-login-webdriver [env]
    "log in to ui with web driver"
    (locking *driver*  ;because webdriver is single threaded, note this constrains system performance as a whole
      (try
        (to (config env :webui :url))
        (when (exists? "#loginForm")
          (input-text "#username" (config env :webui :username))
          (input-text "#password" (config env :webui :password))
          ;(input-text "#password" "foo") ;for testing
          (click "#btnSignIn")
          
          (if (password-correct)
            (do (wait-until #(exists? "#modal_popup_div"))
              (def password-ok true)
              (exists? "#modal_popup_div"))            
            (do (timbre/info "Disabling login test on" env)
                (def password-ok false)
                false)))
          
      (catch  org.openqa.selenium.WebDriverException e
        (timbre/error (.getMessage e)(take-screenshot)) ;TODO access this via ui link on failure, and stop checking till fixed
        false))))

    
  (defn check-login [env]
    "login check only if password in not invalid"
    (if password-ok
      (check-login-webdriver env)
      (do (timbre/info "Login test has been disabled due to incorrect password on" env)
           false)))  ;TODO propogate up so time log isn't updated
  ;password-ok
  ;(check-login :qa6)
  ;(check-login-webdriver :qa6)
  ;(def env :qa6)
  