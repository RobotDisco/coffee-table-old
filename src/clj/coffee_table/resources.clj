(ns coffee-table.resources
  (:require [buddy.sign.jwt :as jwt]
            [buddy.hashers :as bhash]
            [coffee-table.component.database :as dbc]
            [coffee-table.model :refer [Visit] :as m]
            [yada.yada :as yada]
            [schema.core :as s]
            [hiccup.core :refer [html]]
            [clj-time.core :as time])
  (:import [java.net URI]
           [clojure.lang ExceptionInfo]))

(defn valid-user [db username password]
  (let [unauthenticated [false {:error "Invalid username or password"}]]
    (if-let [user (dbc/private-user db username)]
      (if (bhash/check password (:password user))
        [true (-> user
                  (dissoc :password))]
        unauthenticated)
      unauthenticated)))

(defmethod yada.security/verify :jwt [ctx scheme]
  (try
    (let [auth (get-in ctx [:request :headers "Authorization"])
          cred (jwt/unsign (last (re-find #"^Bearer (.*)$" auth)) "lp0fTc2JMtx8")]
      (m/JSON-User cred))
    (catch ExceptionInfo e
      (if-not (= (ex-data e)
                 {:type :validation :cause :signature})
        (throw e)))))

(defn new-login-resource [db]
  (yada/resource
   {:access-control {:allow-origin "http://localhost:3449"
                     :allow-methods [:head :options :post]
                     :allow-headers ["Content-Type"]}
    :logger (fn [& rest]
              (print rest))
    :methods
    {:post
     {:consumes "application/json"
      :produces "application/json"
      :parameters {:body {:username s/Str :password s/Str}}
      :response (fn [ctx]
                  (let [{:keys [username password]} (get-in ctx [:parameters :body])
                        [success payload] (valid-user db username password)
                        response (:response ctx)]
                    (if success
                      (merge payload
                             {:token (jwt/sign (assoc payload :exp (time/plus (time/now) (time/minutes 10)))
                                               "lp0fTc2JMtx8")})
                      (-> response
                          (assoc :status 401)
                          (assoc :body payload)))))}}}))

(defn new-visit-index-resource [db]
  (yada/resource
   {:access-control {:allow-origin "http://localhost:3449"
                     :allow-methods [:options :head :get :post]
                     :allow-headers ["Content-Type" "Authorization"]
                     :scheme :jwt
                     :authorization {:methods {:get :user
                                               :post :user}}}
    :description "Café Visit index"
    :consumes #{"application/json"}
    :produces #{"application/json"}
    :methods {:get {:response (fn [ctx]
                                (dbc/visits db))}
              :post {:parameters {:body Visit}
                     :response (fn [ctx]
                                 (let [visit (dbc/add-visit db (get-in ctx [:parameters :body]))
                                       id (:id visit)]
                                   (URI. (str "/visits/" id))))}}}))

(defn new-visit-node-resource [db]
  (yada/resource
   {:access-control {:allow-origin "http://localhost:3449"
                     :allow-methods [:options :head :get :put :delete]
                     :allow-headers ["Content-Type" "Authorization"]
                     :scheme :jwt
                     :authorization {:methods {:get :user
                                               :put :user
                                               :delete :user}}}
    :description "Café Visit entries"
    :consumes #{"application/json"}
    :produces #{"application/json"}
    :parameters {:path {:id Long}}
    :properties (fn [ctx]
                  (let [id (get-in ctx [:parameters :path :id])]
                    {:exists? (not (nil? (dbc/visit db id)))}))
    :methods {:delete {:response (fn [ctx]
                                   (let [id (get-in ctx [:parameters :path :id])]
                                     (dbc/delete-visit db id)))}
              :get {:response (fn [ctx]
                                (let [id (get-in ctx [:parameters :path :id])]
                                  (dbc/visit db id)))}
              :put {:parameters {:body Visit}
                    :response (fn [ctx]
                                (let [id (get-in ctx [:parameters :path :id])
                                      updated-visit (get-in ctx [:parameters :body])
                                      updated-visit1 (assoc updated-visit :id id)
                                      res (dbc/update-visit db updated-visit1)]
                                  (if-not (nil? res)
                                    nil
                                    (assoc-in ctx [:response :status] 404))))}}}))
