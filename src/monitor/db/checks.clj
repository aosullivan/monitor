(ns monitor.db.checks
  (:require
    [clojure.java.jdbc :as jdbc]
    [carica.core :as carica] ))

(carica/config :author)

(def syb-spec
  {:classname   (carica/config :qa6 :classname)
   :subprotocol (carica/config :qa6 :subprotocol)
   :subname (carica/config :qa6 :subname)
   :user (carica/config :qa6 :user)
   :password (carica/config :qa6 :password) })

(jdbc/query syb-spec ["select top 5 * from entity order by entity_id desc"])

