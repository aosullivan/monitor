(ns monitor.routes.home
  (:require [monitor.layout :as layout]
            [monitor.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [noir.io :as noir]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn services-page-old [{:keys [flash]}]
  (layout/render "services.html" {:environments   (db/get-environments)
                                  :service-checks (db/get-service-checks)}))

(defn services-page [{:keys [flash]}]
  (noir/slurp-resource "/example2.html" ))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/services" request (services-page request))
  (GET "/services-old" request (services-page-old request))
  (GET "/environments/json"  [] {:body (db/get-environments)})
  (GET "/service-checks/json"  [] {:body (db/get-service-checks)}))

