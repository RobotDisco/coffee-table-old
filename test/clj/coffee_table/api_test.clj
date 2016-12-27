(ns coffee-table.api-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :as mock]
             [yada.yada :as yada]))

(deftest visits-api
  (testing "POST /visits (invalid data)"
    (let [handler (yada/handler "Totally fake handler")
          response @(handler (mock/request :post "/visits"))]
      (is (= 400 (:status response)))))
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
