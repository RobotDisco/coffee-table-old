(ns coffee-table.resources
  (:require [yada.yada :as yada]
            [coffee-table.model :refer [Visit]]
            [coffee-table.bullshit-database :as dbc])
  (:import [java.net URI]))

(defn new-visit-resource [db]
  (yada/resource
   {:description "Café Visit entries"
    :consumes #{"application/json"}
    :properties (fn [ctx] {:exists? false})
    :methods {:post {:parameters {:body Visit}
                     :response (fn [ctx]
                                 (let [visit (dbc/add-visit db (get-in ctx [:parameters :body]))
                                       id (:id visit)]
                                   (URI. (str "/visits/" id))))}}}))
