(ns user
  (:require [schema.core :as s]
            [clojure.pprint :refer [pprint]]
            [reloaded.repl :refer [system init start stop go reset reset-all]]
            [coffee-table.system :refer [dev-system]]))

(s/set-fn-validation! true)

(reloaded.repl/set-init! #'dev-system)
