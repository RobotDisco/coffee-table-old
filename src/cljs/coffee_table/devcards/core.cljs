(ns coffee-table.devcards.core
  (:require devcards.core
            goog.object
            [reagent.core :as reagent]
            [cljsjs.semantic-ui-react])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-rg]]))

(def semantic-ui js/semanticUIReact)
(def rating (goog.object/get semantic-ui "Rating"))

(dc/defcard-rg view
  [:> rating {:defaultRating 3 :maxRating 5}])
