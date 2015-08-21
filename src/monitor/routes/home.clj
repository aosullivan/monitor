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

(defn services-page []
  (layout/render "services-angular.html" ))

(defn services-page-old [{:keys [flash]}]
  (layout/render "services-selmer.html" {:environments   (db/get-environments)
                                         :service-checks (db/get-service-checks)}))

(defn helloworld-page []
  (layout/render "example2.html" ))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/services" request (services-page))
  (GET "/services-old" request (services-page-old request))
  (GET "/environments/json"  [] {:body (db/get-environments)})
  (GET "/service-checks/json"  [] {:body (db/get-service-checks)}))

