(ns coffee-table.api-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :as mock]
             [bidi.ring :refer [make-handler]]
             [yada.yada :as yada]
             [coffee-table.model :refer [Visit]]
             [coffee-table.system :refer [test-system]]
             [cheshire.core :refer [generate-string parse-string]]
             [byte-streams :as bs :refer [convert]]
             [com.stuartsierra.component :as component]))

(def example-visit {:name "Minumum Data"
                    :beverage-rating 5
                    :beverage-ordered "Cortado"
                    :date (java.util.Date.)})

(defn make-json-request [request body]
  (-> request
      (mock/body (generate-string body))
      (mock/header "Content-Type" "application/json")
      (mock/header "Accept" "application/json")))

(deftest visits-api
  (testing "POST /visits (invalid data)"
    (let [handler (make-handler (:routes (:web (component/start (test-system)))))
          request (make-json-request (mock/request :post "/visits") {})
          response @(handler request)]
      (is (= 400 (-> response :status)))))
  (testing "POST /visits (valid data)"
    (let [handler (make-handler (:routes (:web (component/start (test-system)))))
          request (make-json-request (mock/request :post "/visits") example-visit)
          response @(handler request)]
      (is (= 201 (-> response :status)))
      (is (contains? (:headers response) "location"))))
  (testing "GET /visits/<someid> (existing entry)"
    (let [handler (make-handler (:routes (:web (component/start (test-system)))))
          create-request (make-json-request (mock/request :post "/visits") example-visit)
          create-response @(handler create-request)
          location (get-in create-response [:headers "location"])
          get-request (mock/request :get location)
          get-response @(handler get-request)]
      (is (= (assoc example-visit :id 0) (update (parse-string (convert (:body get-response) String) true) :date clojure.instant/read-instant-date)))))
  (testing "GET /visits/<someid> (nonexistant entity)"
    (let [handler (make-handler (:routes (:web (component/start (test-system)))))
          get-request (mock/request :get "/visits/0")
          get-response @(handler get-request)]
      (is (= 404 (:status get-response)))))
  (testing "GET /visits (no entries yet)"
    (let [handler (make-handler (:routes (:web (component/start (test-system)))))
          response @(handler (mock/request :get "/visits"))]
      (is (= 200 (:status response)))
      (is (= [] (parse-string (convert (:body response) String))))))
  (testing "Get /visits (a couple of entries)"
    (let [numtimes 2
          handler (make-handler (:routes (:web (component/start (test-system)))))
          _ (dotimes [_ numtimes] @(handler (make-json-request (mock/request :post "/visits") example-visit)))
          list-request (mock/request :get "/visits")
          list-response @(handler list-request)]
      (is (= 200 (:status list-response)))
      (is (= numtimes (count (parse-string (convert (:body list-response) String)))))))
  #_ (testing "PUT /visits"
    (let [response @((yada/handler "/") (mock/request :put "/visits"))]
      (is (= 201 (:status response)))))
  #_ (testing "POST /visits"
    (let [response @((yada/handler "/") (mock/request :post "/visits"))]
      (is (= 204 (:status response)))))
  #_ (testing "DELETE /visits"
    (let [response @((yada/handler "/") (mock/request :delete "/visits"))]
      (is (= 204 (:status response))))))
