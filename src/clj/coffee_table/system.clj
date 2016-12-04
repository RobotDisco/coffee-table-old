(ns coffee-table.system
  (:require [com.stuartsierra.component :as component]
            [coffee-table.bullshit-database :as bsd]
            [coffee-table.web-server :as ws]))

(defn dev-system []
  (component/system-map
   :db (bsd/new-bullshit-database)
   :web (ws/new-web-server)))

(defn test-system []
  (component/system-map
   :db (bsd/new-bullshit-database)))
