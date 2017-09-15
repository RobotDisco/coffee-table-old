(ns coffee-table.resources
  (:require [buddy.sign.jwt :as jwt]
            [buddy.hashers :as bhash]
            [coffee-table.component.database :as dbc]
            [coffee-table.model :refer [Visit]]
            [yada.yada :as yada]
            [schema.core :as s]
            [hiccup.core :refer [html]])
  (:import java.net.URI))

(defn valid-user [db username password]
  (if-let [user (dbc/user db username)]
    (bhash/check password (:password user))
    false))

(defn new-login-resource [db]
  (yada/resource
   {:methods
    {:post
     {:consumes "application/x-www-form-urlencoded"
      :parameters {:form {:user s/Str :password s/Str}}
      :response (fn [ctx]
                  (let [{:keys [user password]} (get-in ctx [:parameters :form])]
                    (if (valid-user db user password)
                      (assoc (:response ctx)
                             :cookies {"session"
                                       {:value (jwt/sign {:user user}
                                                         "lp0fTc2JMtx8")}})
                      "Login failed!!!")))}
     :get
     {:produces "text/html"
      :response (html
                 [:form {:method :post}
                  [:input {:name "user" :type :text}]
                  [:input {:name "password" :type :password}]
                  [:input {:type :submit}]])}}}))

(defn new-visit-index-resource [db]
  (yada/resource
   {:access-control {:allow-origin "http://localhost:3449"
                     :allow-methods [:options :head :get :post]
                     :allow-headers ["Content-Type"]}
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
                     :allow-headers ["Content-Type"]}
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
