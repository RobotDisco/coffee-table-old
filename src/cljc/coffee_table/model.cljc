(ns coffee-table.model
  (:require [schema.core :as s :refer [defn]]))

(def Rating
  "Numeric score for various visit factors"
  (s/enum 1 2 3 4 5))

(def Address
  "Location information"
  {:address1 s/Str
   (s/optional-key :address2) s/Str
   :city s/Str
   :region s/Str
   :country s/Str})

(def Visit
  "Schema for coffee table visits"
  {(s/optional-key :id) s/Int
   :name s/Str
   :date s/Inst
   (s/optional-key :address) Address
   (s/optional-key :espresso-machine) s/Str
   (s/optional-key :grinder) s/Str
   (s/optional-key :roast) s/Str
   :beverage-ordered s/Str
   :beverage-rating Rating
   (s/optional-key :beverage-notes) s/Str
   (s/optional-key :service-rating) Rating
   (s/optional-key :service-notes) s/Str
   (s/optional-key :ambience-rating) Rating
   (s/optional-key :ambience-notes) s/Str
   (s/optional-key :other-notes) s/Str})

(defn visit-id [visit :- Visit] :- s/Int
  (:id visit))

(def Summary
  "Schema for coffee table summaries"
  {:name s/Str
   :date s/Inst
   :beverage-rating s/Int})
