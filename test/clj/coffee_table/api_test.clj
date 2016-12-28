(ns coffee-table.api-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :as mock]
             [yada.yada :as yada]
             [coffee-table.model :refer [Visit]]
             [cheshire.core :refer [generate-string]]
             [byte-streams :as bs]))

(defn make-json-request [request body]
  (-> request
      (mock/body (generate-string body))
      (mock/header "Content-Type" "application/json")))

(deftest visits-api
  (testing "POST /visits (invalid data)"
    (let [resource {:methods {:post {:parameters {:body Visit}
                                     :response (java.net.URI. "/visits/1")}}
                    :consumes #{"application/json"}}
          handler (yada/handler (yada/resource resource))
          request (make-json-request (mock/request :post "/visits")
                                     {})
          response @(handler request)]
      (is (= 400 (-> response :status)))))
  (testing "POST /visits (valid data)"
    (let [resource {:properties (fn [ctx] {:exists? false})
                    :methods {:post {:parameters {:body Visit}
                                     :response (java.net.URI. "/visits/1")}}
                    :consumes #{"application/json"}}
          handler (yada/handler (yada/resource resource))
          request (make-json-request (mock/request :post "/visits")
                                     {:name "Minumum Data"
                                      :beverage-rating 5
                                      :beverage-ordered "Cortado"
                                      :date (java.util.Date.)})
          response @(handler request)]
      (is (= 201 (-> response :status)))
      (is (contains? (:headers response) "location"))))
  #_ (testing "GET /visits"
    (let [handler (yada/handler "Totally fake handler")
          response @(handler (mock/request :get "/visits"))]
      (is (= 200 (:status response)))))
  #_ (testing "PUT /visits"
    (let [response @((yada/handler "/") (mock/request :put "/visits"))]
      (is (= 201 (:status response)))))
  #_ (testing "POST /visits"
    (let [response @((yada/handler "/") (mock/request :post "/visits"))]
      (is (= 204 (:status response)))))
  #_ (testing "DELETE /visits"
    (let [response @((yada/handler "/") (mock/request :delete "/visits"))]
      (is (= 204 (:status response))))))
