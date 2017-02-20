(ns coffee-table.system
  (:require [com.stuartsierra.component :as component]
            [coffee-table.database :as dbc]
            [coffee-table.bullshit-database :as bsd]
            [coffee-table.web-server :as ws]))

(defn dev-system []
  (component/system-map
   :db (dbc/new-database)
   :web (component/using
         (ws/new-web-server)
         [:db])))

(defn test-system []
  (component/system-map
   :db (dbc/new-database)
   :web (component/using
         (ws/new-mock-web-server)
         [:db])))
