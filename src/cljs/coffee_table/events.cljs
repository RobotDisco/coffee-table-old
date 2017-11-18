(ns coffee-table.events
  (:require [goog.string :as gstring]
            [coffee-table.db :as db]
            [coffee-table.model :as m]
            [re-frame.core :as rf]
            [schema.core :as s]
            [ajax.core :as ajax]
            [cljs-time.core :as time]))


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
                 :headers {:Authorization (str "Bearer " (get-in db [:app/user :token]))}
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:load-all-visits]
                 :on-failure [:bad-response]}}))

(rf/reg-event-db
 :load-all-visits
 #_ coffee-table-interceptors
 (fn [db [_ response]]
   (-> db
       (assoc :visits/loading? false)
       (assoc :app/mode :list)
       (assoc :visits/all (->> response
                              js->clj
                              (mapv m/JSON-Visit))))))

(rf/reg-event-db
 :select-visit
 (fn [db [_ visit]]
   (-> db
       (assoc :buffer/visit visit)
       (assoc :app/mode :view))))

(rf/reg-event-db
 :add-visit
 (fn [db _]
   (-> db
       (assoc :buffer/visit (m/new-visit "New Visit" (time/today) "New drink" 3))
       (assoc :app/mode :view))))

(rf/reg-event-db
 :cancel-edit
 (fn [db _]
   (-> db
       (assoc :app/mode :list))))

(rf/reg-event-fx
 :submit-visit
 #_ coffee-table-interceptors
 (fn [{:keys [db]} _]
   (let [visit-id (-> db :buffer/visit :id)
         new-visit (nil? visit-id)
         base-url "http://localhost:8080/visits"]
     {:http-xhrio {:method (if new-visit :post :put)
                   :uri (if new-visit
                          base-url
                          (gstring/format "http://localhost:8080/visits/%d" visit-id))
                   :headers {:Authorization (str "Bearer " (get-in db [:app/user :token]))}
                   :params (-> db
                               :buffer/visit
                               m/Visit-JSON)
                   :format (ajax/json-request-format)
                   :response-format (ajax/raw-response-format)
                   :on-success [:fetch-all-visits]
                   :on-failure [:bad-response]}})))

(rf/reg-event-fx
 :login-attempt
 #_ coffee-table-interceptors
 (fn [_ [_ username password]]
   {:http-xhrio {:method :post
                 :uri "http://localhost:8080/login"
                 :params {:username username :password password}
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-failure [:bad-response]
                 :on-success [:login-successful]}}))

(rf/reg-event-fx
 :login-successful
 #_ coffee-table-interceptors
 (fn [{:keys [db]} [_ response]]
   {:db (-> db
            (assoc :app/user response))
    :dispatch [:fetch-all-visits]}))

(rf/reg-event-db
 :bad-response
 #_ coffee-table-interceptors
 (fn [db [_ response]]
   (assoc db :app/error (get-in response [:response :error]))))

(rf/reg-event-db
 :update-buffer
 (fn [db [_ key value]]
   (assoc-in db [:buffer/visit key] value)))
