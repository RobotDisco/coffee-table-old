(ns coffee-table.resources
  (:require [yada.yada :as yada]
            [coffee-table.model :refer [Visit]])
  (:import [java.net URI]))

(defn new-visit-resource []
  (yada/resource
   {:description "Café Visit entries"
    :consumes #{"application/json"}
    :properties (fn [ctx] {:exists? false})
    :methods {:post {:parameters {:body Visit}
                     :response (URI. "/visits/1")}}}))
