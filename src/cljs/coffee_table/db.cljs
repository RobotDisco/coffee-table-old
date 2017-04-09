(ns coffee-table.db
  (:require [schema.core :as s]
            [coffee-table.model :as m]))

(s/defschema AppDBSchema
  {:visits/all [m/Visit]
   :visits/loading? s/Bool})

(def initial-value
  {:visits/all []
   :visits/loading? true})
