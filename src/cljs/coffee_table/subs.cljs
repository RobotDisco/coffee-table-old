(ns coffee-table.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :visits/all
 (fn [db _]
   (:visits/all db)))

(reg-sub
 :app/mode
 (fn [db _]
   (:app/mode db)))

(reg-sub
 :buffer/visit
 (fn [db _]
   (:buffer/visit db)))
