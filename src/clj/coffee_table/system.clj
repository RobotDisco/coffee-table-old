(ns coffee-table.system
  (:require [com.stuartsierra.component :as component]
            [coffee-table.bullshit-database :as bsd]
            [coffee-table.web-server :as ws]))

(def dev-system
  (component/system-map
   :db (bsd/new-bullshit-database)
   :web (ws/new-web-server)))

(def test-system
  (component/system-map
   :db (bsd/new-bullshit-database)))
