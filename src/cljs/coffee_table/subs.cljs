(ns coffee-table.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :visits/all
 (fn [db _]
   (:visits/all db)))
