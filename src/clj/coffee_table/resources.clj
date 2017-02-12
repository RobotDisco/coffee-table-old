(ns coffee-table.resources
  (:require [yada.yada :as yada]
            [coffee-table.model :refer [Visit]]
            [coffee-table.bullshit-database :as dbc]
            [clojure.pprint :as pprint])
  (:import [java.net URI]))

(defn new-visit-index-resource [db]
  (yada/resource
   {:description "Café Visit index"
    :consumes #{"application/json"}
    :produces #{"application/json"}
    :methods {:get {:response (fn [ctx]
                                (dbc/visits db))}
              :post {:parameters {:body Visit}
                     :response (fn [ctx]
                                 (let [visit (dbc/add-visit db (get-in ctx [:parameters :body]))
                                       id (:id visit)]
                                   (URI. (str "/visits/" id))))}}}))

(defn new-visit-node-resource [db]
  (yada/resource
   {:description "Café Visit entries"
    :consumes #{"application/json"}
    :produces #{"application/json"}
    :parameters {:path {:id Long}}
    :properties (fn [ctx]
                  (let [id (get-in ctx [:parameters :path :id])]
                    {:exists? (not (nil? (dbc/visit db id)))}))
    :methods {:get {:response (fn [ctx]
                                (let [id (get-in ctx [:parameters :path :id])]
                                  (dbc/visit db id)))}
              :put {:parameters {:body Visit}
                    :response (fn [ctx]
                                (let [id (get-in ctx [:parameters :path :id])
                                      updated-visit (get-in ctx [:parameters :body])
                                      res (dbc/update-visit db id updated-visit)]
                                  (if-not (nil? res)
                                    nil
                                    (assoc-in ctx [:response :status] 404))))}}}))
