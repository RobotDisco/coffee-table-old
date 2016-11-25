(ns coffee-table.bullshit-database
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s :refer [defn]]
            [coffee-table.model :as m]))

(def DBVisit
  (s/conditional #(contains? % :id) m/Visit))

(def DBVisitResult
  (s/maybe DBVisit))

(defrecord BullshitDatabase []
  component/Lifecycle
  (start [this]
    (assoc this :visits (atom [])))
  (stop [this]
    (assoc this :visits nil)))

(defn bullshit-database
  []
  (map->BullshitDatabase {}))

(defn visits :- [DBVisit]
  [component]
  @(:visits component))

(defn visit :- DBVisitResult
  [component
   id :- s/Int]
  (nth (visits component) id nil))

(defn- visits-atom [component]
  (:visits component))

(defn add-visit :- DBVisit
  [component
   visit :- m/Visit]
  (let [db-visits (visits component)
        db-visit (assoc visit :id (count db-visits))
        _ (swap! (visits-atom component) conj db-visit)]
    db-visit))

(defn delete-visit :- s/Bool
  [component
   visit-id :- s/Int]
  (swap! (visits-atom component) #(concat (subvec % 0 visit-id) (subvec % (inc visit-id))))
  true)

(defn update-visit :- DBVisitResult
  [component
   visit :- DBVisit]
  (let [update-idx (m/visit-id visit)
        num-visits (count (visits component))]
    (when (< update-idx num-visits)
      (do
        (swap! (visits-atom component) assoc update-idx visit)
        visit))))
