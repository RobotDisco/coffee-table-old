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

(def system (atom nil))
(def handler (atom nil))


(defn with-test-system [f]
  (reset! system (component/start (test-system)))
  (reset! handler (make-handler (:routes (:web @system))))
  (f)
  (component/stop @system))

(use-fixtures :each with-test-system)


(def example-visit {:name "Minumum Data"
                    :beverage-rating 5
                    :beverage-ordered "Cortado"
                    :date (java.util.Date.)})

(defn make-json-request [request body]
  (-> request
      (mock/body (generate-string body))
      (mock/header "Content-Type" "application/json")
      (mock/header "Accept" "application/json")))

(deftest create-visits-invalid-data
  (testing "POST /visits (invalid data)"
    (let [request (make-json-request (mock/request :post "/visits") {})
          response @(@handler request)]
      (is (= 400 (-> response :status))))))

(deftest create-visits-valid-data
  (testing "POST /visits (valid data)"
    (let [request (make-json-request (mock/request :post "/visits") example-visit)
          response @(@handler request)]
      (is (= 201 (-> response :status)))
      (is (contains? (:headers response) "location")))))

(deftest get-visits-id-exists
  (testing "GET /visits/<someid> (existing entry)"
    (let [create-request (make-json-request (mock/request :post "/visits") example-visit)
          create-response @(@handler create-request)
          location (get-in create-response [:headers "location"])
          get-request (mock/request :get location)
          get-response @(@handler get-request)]
      (is (= (assoc example-visit :id 0) (update (parse-string (convert (:body get-response) String) true) :date clojure.instant/read-instant-date))))))

(deftest get-visits-id-does-not-exist
  (testing "GET /visits/<someid> (nonexistant entity)"
    (let [get-request (mock/request :get "/visits/0")
          get-response @(@handler get-request)]
      (is (= 404 (:status get-response))))))

(deftest list-visits-no-entries-yet
  (testing "GET /visits (no entries yet)"
    (let [response @(@handler (mock/request :get "/visits"))]
      (is (= 200 (:status response)))
      (is (= [] (parse-string (convert (:body response) String)))))))

(deftest list-visits-entries-exist
  (testing "Get /visits (a couple of entries)"
    (let [numtimes 2
          _ (dotimes [_ numtimes] @(@handler (make-json-request (mock/request :post "/visits") example-visit)))
          list-request (mock/request :get "/visits")
          list-response @(@handler list-request)]
      (is (= 200 (:status list-response)))
      (is (= numtimes (count (parse-string (convert (:body list-response) String))))))))

(deftest update-visits-entry-exists)
(deftest update-visits-entry-does-not-exist)
(deftest update-visits-incomplete-data)
(deftest delete-visits-id-exists)
(deftest delete-visits-id-does-not-exist)
