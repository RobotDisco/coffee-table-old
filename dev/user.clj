(ns user
  (:require [schema.core :as s]
            [clojure.pprint :refer [pprint]]
            [reloaded.repl :refer [system]]
            [coffee-table.system :refer [dev-system]]
            [figwheel-sidecar.system :as figsys]))

(s/set-fn-validation! true)

(reloaded.repl/set-init! #'dev-system)

;; Set up aliases so they don't accidentally
;; get scrubbed from the namespace declaration
(def start reloaded.repl/start)
(def stop reloaded.repl/stop)
(def go reloaded.repl/go)
(def reset reloaded.repl/reset)
(def reset-all reloaded.repl/reset-all)

(defn cljs-repl []
  (figsys/cljs-repl (:figwheel system)))
