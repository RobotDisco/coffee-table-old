(ns coffee-table.model
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            #?(:cljs [cljs-time.coerce :as dcoerce])))

(s/defschema User
  "Coffee Table User accounts"
  {:id s/Int
   :username s/Str
   :password s/Str
   :is_admin s/Bool})

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

(s/defn visit-id :- s/Int [visit :- Visit]
  (:id visit))

(s/defn new-visit :- Visit [name :- s/Str
                            date :- s/Inst
                            bev-ordered :- s/Str
                            bev-rating :- Rating]
  {:name name
   :date date
   :beverage-ordered bev-ordered
   :beverage-rating bev-rating})

#?(:cljs (defn json-visit-coercion-matcher
           [schema]
           (or ({s/Inst (coerce/safe (fn [x] (-> x
                                                 dcoerce/from-string
                                                 dcoerce/to-date)))} schema)
               (coerce/json-coercion-matcher schema))))

#?(:cljs (def JSON-Visit
           (coerce/coercer Visit json-visit-coercion-matcher)))

#?(:cljs (defn Visit-JSON [visit]
           (-> visit
               (update :date dcoerce/to-string)
               clj->js)))

(s/defschema Summary
  "Schema for coffee table summaries"
  {:name s/Str
   :date s/Inst
   :beverage-rating s/Int})
