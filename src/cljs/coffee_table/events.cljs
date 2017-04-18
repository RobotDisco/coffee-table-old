(ns coffee-table.events
  (:require [coffee-table.db :as db]
            [coffee-table.model :as m]
            [re-frame.core :as rf]
            [schema.core :as s]
            [ajax.core :as ajax]))


;; Interceptors

(defn check-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when (s/check a-spec db)
    (throw (ex-info (str "schema check failed: " (s/check a-spec db)) {}))))

;; Event handlers change state, that's their job. But what happens if there's
;; a bug which corrupts app state in some subtle way? This interceptor is run after
;; each event handler has finished, and it checks app-db against a spec.  This
;; helps us detect event handler bugs early.
(def check-schema-interceptor (rf/after (partial check-and-throw db/AppDBSchema)))

;; Each event handler can have its own set of interceptors (middleware)
;; But we use the same set of interceptors for all event handlers
(def coffee-table-interceptors [check-schema-interceptor
                                (when ^boolean js/goog.DEBUG rf/debug)])


(rf/reg-event-db
 :initialize-db
 #_ coffee-table-interceptors
 (fn [_ _]
   db/initial-value))

(rf/reg-event-fx
 :fetch-all-visits
 #_ coffee-table-interceptors
 (fn [{:keys [db]} _]
   {:db (assoc db :visits/loading? true)
    :http-xhrio {:method :get
                 :uri "http://localhost:8080/visits"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:load-all-visits]
                 :on-failure [:bad-response]}}))

(rf/reg-event-db
 :load-all-visits
 #_ coffee-table-interceptors
 (fn [db [_ response]]
   (-> db
       (assoc :visits/loading? false)
       (assoc :visits/all (->> response
                              js->clj
                              (mapv m/JSON-Visit))))))

(rf/reg-event-db
 :bad-response
 #_ coffee-table-interceptors
 (fn [_ _]))
