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
