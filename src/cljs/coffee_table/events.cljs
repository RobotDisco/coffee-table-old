(ns coffee-table.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]))

(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:visits/all []
    :visits/loading? true}))

(rf/reg-event-fx
 :fetch-all-visits
 (fn [{:keys [db]} _]
   {:db (assoc db :visits/loading? true)
    :http-xhrio {:method :get
                 :uri "http://localhost:8080/visits"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:load-all-visits]
                 :on-failure [:bad-response]}}))

(rf/reg-event-db
 :load-all-visits
 (fn [db [_ response]]
   (-> db
       (assoc :visits/loading? false)
       (assoc :visits/all (js->clj response)))))

(rf/reg-event-db
 :bad-response
 (fn [_ _]))
