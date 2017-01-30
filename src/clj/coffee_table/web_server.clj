(ns coffee-table.web-server
  (:require [schema.core :as s]
            [yada.yada :as yada]
            [com.stuartsierra.component :as component]
            [coffee-table.resources :refer [new-visit-node-resource new-visit-index-resource]]))

(defn routes [db]
  "Create URI route structure for our application."
  [""
   [["/visits" [["" (new-visit-index-resource db)]
                [["/" :id] (new-visit-node-resource db)]]]
    [true (yada/as-resource nil)]]])

(s/defrecord WebServer [db]
  component/Lifecycle
  (start [component]
    (let [app-routes (routes db)
          listener (yada/listener app-routes {:port 8080})]
      (assoc component :listener listener)))
  (stop [component]
    (when-let [close (get-in component [:listener :close])]
      (close))
    (assoc component :listener nil)))

(s/defrecord MockWebServer [db]
  component/Lifecycle
  (start [component]
    (assoc component :routes (routes db))))

(defn new-web-server []
  (map->WebServer {}))

(defn new-mock-web-server []
  (map->MockWebServer {}))
