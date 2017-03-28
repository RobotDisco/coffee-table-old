(ns coffee-table.views
  (:require cljsjs.semantic-ui-react
            goog.object))

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

(defn summary-widget []
  [:> segment [:div
               [:div [:strong "Café Name"]]
               [:div [:> icon {:name "calendar"}] "01/01/83"]
               [:div [:> icon {:name "coffee"}] [:> rating {:defaultRating 3
                                                            :maxRating 5
                                                            :disabled true}]]]])

(defn summaries []
  [:> container (repeatedly 5 summary-widget)])

(defn app []
  [:div
   [:> header {:as "h1" :text-align "center"} "Coffee Table"]
   [summaries]])
