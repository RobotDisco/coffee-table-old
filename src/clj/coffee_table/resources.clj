(ns coffee-table.resources
  (:require [clojure.pprint :as pprint]
            [coffee-table.component.database :as dbc]
            [coffee-table.model :refer [Visit]]
            [yada.yada :as yada])
  (:import java.net.URI))

(defn new-visit-index-resource [db]
  (yada/resource
   {:description "Café Visit index"
    :access-control {:allow-origin "*"
                     :allow-methods #{:get :post}}
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
    :methods {:delete {:response (fn [ctx]
                                   (let [id (get-in ctx [:parameters :path :id])]
                                     (dbc/delete-visit db id)))}
              :get {:response (fn [ctx]
                                (let [id (get-in ctx [:parameters :path :id])]
                                  (dbc/visit db id)))}
              :put {:parameters {:body Visit}
                    :response (fn [ctx]
                                (let [id (get-in ctx [:parameters :path :id])
                                      updated-visit (get-in ctx [:parameters :body])
                                      updated-visit1 (assoc updated-visit :id id)
                                      res (dbc/update-visit db updated-visit1)]
                                  (if-not (nil? res)
                                    nil
                                    (assoc-in ctx [:response :status] 404))))}}}))
