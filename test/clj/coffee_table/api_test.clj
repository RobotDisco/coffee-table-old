(ns coffee-table.api-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :as mock]))

(deftest visits-api
  (testing "GET /visits"
    (let [response (mock/request :get "/visits")]))
  (testing "PUT /visits"
    (let [response (mock/request :put "/visits")]))
  (testing "POST /visits"
    (let [response (mock/request :post "/visits")]))
  (testing "DELETE /visits"
    (let [response (mock/request :delete "/visits")])))
