(ns coffee-table.model
  (:require [schema.core :as s]
            #?(:cljs [cljs-time.format :as time])))

(s/defschema Rating
  "Numeric score for various visit factors"
  (s/enum 1 2 3 4 5))

(s/defschema Address
  "Location information"
  {:address1 s/Str
   (s/optional-key :address2) s/Str
   :city s/Str
   :region s/Str
   :country s/Str})

(s/defschema Visit
  "Schema for coffee table visits"
  {(s/optional-key :id) s/Int
   :name s/Str
   :date s/Inst
   (s/optional-key :address) Address
   (s/optional-key :machine) s/Str
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

(s/defn new-visit [])

(s/defn visit-id [visit :- Visit] :- s/Int
  (:id visit))

(s/defn json-to-visit
  "Convert JSON entity into a visit"
  [json]
  :- Visit
  (update json :date #?(:clj identity
                        :cljs time/parse)))

(s/defschema Summary
  "Schema for coffee table summaries"
  {:name s/Str
   :date s/Inst
   :beverage-rating s/Int})
