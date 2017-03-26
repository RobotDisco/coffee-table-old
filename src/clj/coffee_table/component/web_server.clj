(ns coffee-table.component.web-server
  (:require [schema.core :as s]
            [yada.yada :as yada]
            [yada.resources.classpath-resource :refer [new-classpath-resource]]
            [com.stuartsierra.component :as component]
            [coffee-table.resources :refer [new-visit-node-resource new-visit-index-resource]]))

(defn routes [db]
  "Create URI route structure for our application."
  [""
   [["/visits" [["" (new-visit-index-resource db)]
                [["/" :id] (new-visit-node-resource db)]]]
    ["" (new-classpath-resource "public"
                                {:index-files ["index.html"]})]]])

(s/defrecord WebServer [db]
  component/Lifecycle
  (start [component]
    (let [app-routes (routes db)
          listener (yada/listener app-routes {:port 80})]
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
