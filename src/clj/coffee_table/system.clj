(ns coffee-table.system
  (:require [com.stuartsierra.component :as component]
            [coffee-table.bullshit-database :as bsd]))

(def test-system
  (component/system-map
   :db (bsd/bullshit-database)))
