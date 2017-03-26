(ns coffee-table.core
  (:require [re-frisk.core :refer [enable-re-frisk!]]
            [day8.re-frame.http-fx]
            [coffee-table.events]
            [coffee-table.views :as views]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(enable-console-print!)

(defn ^:export main
  []
  (rf/dispatch [:initialize-db])
  (rf/dispatch [:fetch-all-visits])
  (reagent/render [views/app] (js/document.getElementById "app"))
  (enable-re-frisk!))
