(ns coffee-table.views
  (:require cljsjs.semantic-ui-react
            goog.object
            [re-frame.core :as rf]
            [cljs-time.format :as time]
            [cljs-time.coerce :as tcoerce]))

(def semantic-ui js/semanticUIReact)

(defn component
  "Get a component from sematic-ui-react:

    (component \"Button\")
    (component \"Menu\" \"Item\")"
  [k & ks]
  (if (seq ks)
    (apply goog.object/getValueByKeys semantic-ui k ks)
    (goog.object/get semantic-ui k)))

(def header (component "Header"))
(def container (component "Container"))
(def segment (component "Segment"))
(def icon (component "Icon"))
(def rating (component "Rating"))

(defn summary [visit]
  [:> segment [:div
               [:div [:strong (:name visit)]]
               [:div [:> icon {:name "calendar"}] (->> (:date visit)
                                                       tcoerce/from-date
                                                       (time/unparse (:date time/formatters)))]
               [:div [:> icon {:name "coffee"}] [:> rating {:defaultRating (:beverage-rating visit)
                                                            :maxRating 5
                                                            :disabled true}]]]])

(defn summaries []
  (let [visits @(rf/subscribe [:visits/all])]
    [:> container (for [visit visits]
                    ^{:key (:id visit)} [summary visit])]))

(defn app []
  [:div
   [:> header {:as "h1" :text-align "center"} "Coffee Table"]
   [summaries]])
