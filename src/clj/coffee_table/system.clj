(ns coffee-table.system
  (:require [coffee-table.component
             [database :as dbc]
             [web-server :as ws]]
            [com.stuartsierra.component :as component]))

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
