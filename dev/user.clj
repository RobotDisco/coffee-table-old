(ns user
  (:require [schema.core :as s]
            [clojure.pprint :refer [pprint]]
            [reloaded.repl :refer [system]]
            [coffee-table.system :refer [dev-system]]
            [coffee-table.db.users :as dbu]
            [buddy.hashers :as bhash]
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

(defn create-admin-user []
  (dbu/insert-user (get-in reloaded.repl/system [:db :spec])
                   {:username "admin"
                    :password (bhash/derive "password")
                    :is_admin true}))
