(ns coffee-table.db
  (:require [schema.core :as s]
            [coffee-table.model :as m]))

(s/defschema AppDBSchema
  {:app/mode (s/enum :list :view :edit)
   :app/user (s/maybe m/PublicUser)
   :app/error (s/maybe s/Str)
   :buffer/visit (s/maybe m/Visit)
   :visits/all [m/Visit]
   :visits/loading? s/Bool})

(def initial-value
  {:app/mode :list
   :app/user nil
   :app/error nil
   :buffer/visit nil
   :visits/all []
   :visits/loading? true})
