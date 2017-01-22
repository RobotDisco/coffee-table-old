(ns coffee-table.resources
  (:require [yada.yada :as yada]
            [coffee-table.model :refer [Visit]]
            [coffee-table.bullshit-database :as dbc]
            [clojure.pprint :as pprint])
  (:import [java.net URI]))

(defn new-visit-resource [db]
  (yada/resource
   {:description "Café Visit entries"
    :consumes #{"application/json"}
    :produces #{"application/json"}
    :properties (fn [ctx]
                  (if (= :post (:method ctx))
                    {:exists? false}
                    (let [id (get-in ctx [:parameters :path :id])]
                      {:exists? (not (nil? (dbc/visit db id)))})))
    :methods {:post {:parameters {:body Visit}
                     :response (fn [ctx]
                                 (let [visit (dbc/add-visit db (get-in ctx [:parameters :body]))
                                       id (:id visit)]
                                   (URI. (str "/visits/" id))))}
              :get {:parameters {:path {:id Long}}
                    :response (fn [ctx]
                                (let [id (get-in ctx [:parameters :path :id])]
                                  (dbc/visit db id)))}}}))
