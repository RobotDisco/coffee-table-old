(ns coffee-table.bullshit-database
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s :refer [defn]]
            [coffee-table.model :as m]))

(defrecord BullshitDatabase []
  component/Lifecycle
  (start [this]
    (assoc this :visits (atom [])))
  (stop [this]
    (assoc this :visits nil)))

(defn bullshit-database
  []
  (map->BullshitDatabase {}))

(defn visits :- [m/Visit]
  [component]
  @(:visits component))

(defn- visits-atom [component]
  (:visits component))

(defn add-visit :- s/Int
  [component
   visit :- m/Visit]
  (swap! (visits-atom component) conj visit)
  (dec (count (visits component))))

(defn delete-visit :- s/Bool
  [component
   visit-id :- s/Int]
  (swap! (visits-atom component) #(concat (subvec % 0 visit-id) (subvec % (inc visit-id))))
  true)

(defn update-visit
  [component
   idx :- s/Int
   visit :- m/Visit]
  (swap! (visits-atom component) assoc idx visit))
