(ns coffee-table.views
  (:require cljsjs.semantic-ui-react
            goog.object
            [coffee-table.login :as llogin]
            [re-frame.core :as rf]
            [reagent.core :as r]
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
      [:> field {:label "Café Name"
                 :control "input"
                 :required true
                 :value (:name visit)
                 :on-change #(rf/dispatch [:update-buffer :name (-> % .-target .-value)])}]
      [:> field {:label "Visit Date"
                 :control "input"
                 :type "date"
                 :required true
                 :value (->> (:date visit) tcoerce/from-date (time/unparse-local (:date time/formatters)))
                 :on-change #(rf/dispatch [:update-buffer :date (->> % .-target .-value (time/parse-local (:date time/formatters)) tcoerce/to-date)])}]
      [:> field {:label "Machine Used"
                 :control "input"
                 :value (:machine visit)
                 :on-change #(rf/dispatch [:update-buffer :machine (-> % .-target .-value)])}]
      [:> field {:label "Grinder Used"
                 :control "input"
                 :value (:grinder visit)
                 :on-change #(rf/dispatch [:update-buffer :grinder (-> % .-target .-value)])}]
      [:> field {:label "Coffee Roast"
                 :control "input"
                 :value (:roast visit)
                 :on-change #(rf/dispatch [:update-buffer :roast (-> % .-target .-value)])}]
      [:> field {:label "Beverage Ordered"
                 :control "input"
                 :required true
                 :value (:beverage-ordered visit)
                 :on-change #(rf/dispatch [:update-buffer :beverage-ordered (-> % .-target .-value)])}]
      [:> field {:label "Beverage Rating"
                 :control "input"
                 :required true
                 :value (:beverage-rating visit)
                 :on-change #(rf/dispatch [:update-buffer :beverage-rating (-> % .-target .-value)])}]
      [:> field {:label "Beverage Notes"
                 :control "textarea"
                 :on-change #(rf/dispatch [:update-buffer :beverage-notes (-> % .-target .-value)])}
       (:beverage-notes visit)]
      [:> field {:label "Service Rating"
                 :control "input"
                 :value (:service-rating visit)
                 :on-change #(rf/dispatch [:update-buffer :service-rating (-> % .-target .-value)])}]
      [:> field {:label "Service Notes"
                 :control "textarea"
                 :on-change #(rf/dispatch [:update-buffer :service-notes (-> % .-target .-value)])}
       (:service-notes visit)]
      [:> field {:label "Ambience Rating"
                 :control "input"
                 :value (:ambience-rating visit)
                 :on-change #(rf/dispatch [:update-buffer :ambience-rating (-> % .-target .-value)])}]
      [:> field {:label "Ambience Notes"
                 :control "textarea"
                 :on-change #(rf/dispatch [:update-buffer :ambience-notes (-> % .-target .-value)])}
       (:ambience-notes visit)]
      [:> field {:label "Other Notes"
                 :control "textarea"
                 :on-change #(rf/dispatch [:update-buffer :other-notes (-> % .-target .-value)])}
       (:other-notes visit)]]
     [:> button-group {}
      [:> button {:on-click #(rf/dispatch [:cancel-edit])} "Cancel"]
      [:> button-or]
      [:> button {:positive true
                  :on-click #(rf/dispatch [:submit-visit])}
       (if (nil? (:id visit))
         "Add"
         "Save")]]]))


(defn login []
  (let [state (r/atom {:username ""
                       :password ""})
        on-blur (fn [key e]
                  (swap! state assoc key (-> e .-target .-value)))
        on-submit #(rf/dispatch [:login-attempt (:username @state) (:password @state)])]
    (fn []
      [:> container
       [:> header {:as "h1"} "Log the heck in"]
       [:> form {:on-submit on-submit}
        [:> field {:label "User Name" :name "username" :control "input" :on-blur (partial on-blur :username)}]
        [:> field {:label "Password" :name "password" :control "input" :type "password" :on-blur (partial on-blur :password)}]
        [:> button {:positive true :role "submit"} "Login"]]])))

(defn error-message []
  (let [error @(rf/subscribe [:app/error])]
    (if (nil? error)
      nil
      [:> segment {:color :red :inverted true :raised true} error])))

(defn valid-user-login [user]
  (not (nil? user)))

(defn app []
  (let [mode @(rf/subscribe [:app/mode])
        user @(rf/subscribe [:app/user])]
    [:> container {}
     [:> header {:as "h1" :text-align "center"} "Coffee Table"]
     [error-message]
     (cond
       (not (valid-user-login user)) [login]
       (= :list mode) [:div [summaries]
              [:> button {:on-click #(rf/dispatch [:add-visit])} "Add Visit"]]
       (= :view mode) [visit]
       true nil)]))
