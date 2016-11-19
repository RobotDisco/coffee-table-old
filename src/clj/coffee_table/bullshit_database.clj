(ns coffee-table.bullshit-database
  (:require [com.stuartsierra.component :as component]))

(defrecord BullshitDatabase []
  component/Lifecycle
  (start [this]
    (assoc this :visits (atom [])))
  (stop [this]
    (assoc this :visits nil)))

(defn bullshit-database []
  (map->BullshitDatabase {}))

(defn visits [component]
  @(:visits component))

(defn visits-atom [component]
  (:visits component))

(defn add-visit [component visit]
  (swap! (visits-atom component) conj visit)
  (count (visits component)))
