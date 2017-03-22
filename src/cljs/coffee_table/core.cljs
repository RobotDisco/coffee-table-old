(ns coffee-table.core
  (:require [re-frisk.core :refer [enable-re-frisk!]]
            [day8.re-frame.http-fx]
            [coffee-table.events]
            [re-frame.core :as rf]))

(defn ^:export main
  []
  (rf/dispatch [:initialize-db])
  (rf/dispatch [:fetch-all-visits])
  (enable-re-frisk!))
