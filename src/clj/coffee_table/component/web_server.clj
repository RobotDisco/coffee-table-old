(ns coffee-table.component.web-server
  (:require [schema.core :as s]
            [yada.yada :as yada]
            [yada.resources.classpath-resource :refer [new-classpath-resource]]
            [com.stuartsierra.component :as component]
            [coffee-table.resources :as r :refer [new-visit-node-resource new-visit-index-resource]]))

(defn routes
  "Create URI route structure for our application."
  [db]
  [""
   [["/visits" [["" (new-visit-index-resource db)]
                [["/" :id] (new-visit-node-resource db)]]]
    ["/login" (r/new-login-resource db)]
    ["/refresh" (r/new-login-refresh)]
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
    (assoc component :routes (routes db)))
  (stop [component]))

(defn new-web-server []
  (map->WebServer {}))

(defn new-mock-web-server []
  (map->MockWebServer {}))
