(ns user
  (:require [schema.core :as s]
            [clojure.pprint :refer [pprint]]
            [reloaded.repl :refer [system init start stop go reset reset-all]]
            [coffee-table.system :refer [dev-system]]
            [figwheel-sidecar.system :as figsys]))

(s/set-fn-validation! true)

(reloaded.repl/set-init! #'dev-system)

(defn cljs-repl []
  (figsys/cljs-repl (:figwheel reloaded.repl/system)))
