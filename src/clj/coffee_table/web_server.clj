(ns coffee-table.web-server
  (:require [schema.core :as s]
            [yada.yada :as yada]
            [com.stuartsierra.component :as component]
            ))

(def routes
  "Create URI route structure for our application."
  [""
   [true (yada/handler nil)]])

(s/defrecord WebServer []
  component/Lifecycle
  (start [component]
    (let [listener (yada/listener routes {:port 8080})]
      (assoc component :listener listener)))
  (stop [component]
    (when-let [close (get-in component [:listener :close])]
      (close))
    (assoc component :listener nil)))

(defn new-web-server []
  (map->WebServer {}))
