(ns coffee-table.bullshit-database
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s]
            [coffee-table.model :as m :refer [Visit]]))

(def DBVisit
  "Schema for visit that came from a DB"
  (s/conditional #(contains? % :id) Visit))

(def DBVisitResult
  (s/maybe DBVisit))

(s/defrecord BullshitDatabase []
  component/Lifecycle
  (start [this]
    (assoc this :visits (atom [])))
  (stop [this]
    (assoc this :visits nil)))

(s/defn new-bullshit-database
  []
  (map->BullshitDatabase {}))

(s/defn visits :- [(s/maybe DBVisit)]
  [component]
  @(:visits component))

(s/defn visit :- DBVisitResult
  [component
   id :- (s/maybe s/Int)]
  (if (nil? id)
    nil
    (nth (visits component) id nil)))

(defn- visits-atom [component]
  (:visits component))

(s/defn add-visit :- DBVisit
  [component
   visit :- Visit]
  (let [db-visits (visits component)
        db-visit (assoc visit :id (count db-visits))
        _ (swap! (visits-atom component) conj db-visit)]
    db-visit))

(s/defn delete-visit :- s/Bool
  [component
   visit-id :- s/Int]
  (swap! (visits-atom component) assoc visit-id nil)
  true)

(s/defn update-visit :- DBVisitResult
  [component
   visit :- DBVisit]
  (let [update-idx (m/visit-id visit)
        num-visits (count (visits component))]
    (if (< update-idx num-visits)
      (do
        (swap! (visits-atom component) assoc update-idx visit)
        visit)
      nil)))
