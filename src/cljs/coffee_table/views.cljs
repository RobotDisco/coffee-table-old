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
(def form (component "Form"))
(def field (component "Form" "Field"))
(def input (component "Form" "Input"))
(def button-group (component "Button" "Group"))
(def button-or (component "Button" "Or"))
(def button (component "Button"))

(defn summary [visit]
  [:> segment [:div
               {:on-click #(rf/dispatch [:select-visit  visit])}
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

(defn visit []
  (let [visit @(rf/subscribe [:buffer/visit])]
    [:> container
     [:> form
      [:> field {:label "Café Name" :control "input" :required true :defaultValue (:name visit)}]
      [:> field {:label "Visit Date" :control "input" :type "date" :required true :defaultValue (->> (:date visit)
                                                                                       tcoerce/from-date
                                                                                       (time/unparse (:date time/formatters)))}]
      [:> field {:label "Machine Used" :control "input" :defaultValue (:machine visit)}]
      [:> field {:label "Grinder Used" :control "input" :defaultValue (:grinder visit)}]
      [:> field {:label "Coffee Roast" :control "input" :defaultValue (:roast visit)}]
      [:> field {:label "Beverage Ordered" :control "input" :required true :defaultValue (:beverage-ordered visit)}]
      [:> field {:label "Beverage Rating" :control "input" :required true :defaultValue (:beverage-rating visit)}]
      [:> field {:label "Beverage Notes" :control "textarea"} (:beverage-notes visit)]
      [:> field {:label "Service Rating" :control "input" :defaultValue (:service-rating visit)}]
      [:> field {:label "Service Notes" :control "textarea"} (:service-notes visit)]
      [:> field {:label "Ambience Rating" :control "input" :defaultValue (:ambience-rating visit)}]
      [:> field {:label "Ambience Notes" :control "textarea"} (:ambience-notes visit)]
      [:> field {:label "Other Notes" :control "textarea"} (:other-notes visit)]]
     [:> button-group {}
      [:> button {} "Cancel"]
      [:> button-or]
      [:> button {:positive true} "Save"]]]))

(defn app []
  (let [mode @(rf/subscribe [:app/mode])]
    [:div
     [:> header {:as "h1" :text-align "center"} "Coffee Table"]
     (condp = mode
       :list [summaries]
       :view [visit]
       nil)]))
