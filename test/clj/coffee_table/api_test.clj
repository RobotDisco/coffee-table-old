(ns coffee-table.api-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :as mock]
             [bidi.ring :refer [make-handler]]
             [yada.yada :as yada]
             [coffee-table.model :refer [Visit]]
             [coffee-table.system :refer [test-system]]
             [cheshire.core :refer [generate-string parse-string]]
             [cheshire.generate :refer [add-encoder]]
             [byte-streams :as bs :refer [convert]]
             [com.stuartsierra.component :as component]
             [coffee-table.db.visits]
             [schema.test])
  (:import [java.time LocalDate]))

(def system (atom nil))
(def handler (atom nil))

(add-encoder java.time.LocalDate
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (.toString c))))

(defn with-test-system [f]
  (reset! system (component/start (test-system)))
  (reset! handler (make-handler (:routes (:web @system))))
  (f)
  (component/stop @system))

(defn clean-visits-table [f]
  (coffee-table.db.visits/delete-all-visits (get-in @system [:db :spec]))
  (f))

(use-fixtures :each with-test-system clean-visits-table)
(use-fixtures :once schema.test/validate-schemas)


(def example-visit {:name "Minumum Data"
                    :beverage-rating 5
                    :beverage-ordered "Cortado"
                    :date (-> (LocalDate/now) java.sql.Date/valueOf)})

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
      (is (= (assoc example-visit :id (Integer. (re-find #"\d+" location))) (update (-> get-response :body bs/to-string (parse-string true)) :date clojure.instant/read-instant-date))))))

(deftest get-visits-id-does-not-exist
  (testing "GET /visits/<someid> (nonexistant entity)"
    (let [get-request (mock/request :get "/visits/0")
          get-response @(@handler get-request)]
      (is (= 404 (:status get-response))))))

(deftest list-visits-no-entries-yet
  (testing "GET /visits (no entries yet)"
    (let [response @(@handler (mock/request :get "/visits"))]
      (is (= 200 (:status response)))
      (is (= [] (parse-string (bs/to-string (:body response))))))))

(deftest list-visits-entries-exist
  (testing "Get /visits (a couple of entries)"
    (let [numtimes 2
          _ (dotimes [_ numtimes] @(@handler (make-json-request (mock/request :post "/visits") example-visit)))
          list-request (mock/request :get "/visits")
          list-response @(@handler list-request)]
      (is (= 200 (:status list-response)))
      (is (= numtimes (count (parse-string (bs/to-string (:body list-response)))))))))

(deftest update-visits-entry-exists
  (testing "PUT /visits/<id> (<id> existed already)"
    (let [create-request (make-json-request (mock/request :post "/visits") example-visit)
          location (get-in @(@handler create-request) [:headers "location"])
          put-body (assoc example-visit :name "Updated Café")
          put-request (make-json-request (mock/request :put location) put-body)
          put-response @(@handler put-request)]
      (is (= 204 (:status put-response))))))

(deftest update-visits-entry-does-not-exist
  (testing "PUT /visits/<id> (<id> doesn't exist)"
    (let [put-body (-> example-visit (assoc :id 0) (assoc :name "Updated Café"))
          put-request (make-json-request (mock/request :put "/visits/0") put-body)
          put-response @(@handler put-request)]
      (is (= 404 (:status put-response))))))

(deftest update-visits-incomplete-data
  (testing "PUT /visits/<id> (<id> doesn't exist)"
    (let [put-body (-> example-visit (dissoc :name))
          put-request (make-json-request (mock/request :put "/visits/0") put-body)
          put-response @(@handler put-request)]
      (is (= 400 (:status put-response))))))

(deftest delete-visits-id-exists
    (testing "DELETE /visits/<someid> (existing entry)"
    (let [create-request (make-json-request (mock/request :post "/visits") example-visit)
          create-response @(@handler create-request)
          location (get-in create-response [:headers "location"])
          delete-request (mock/request :delete location)
          delete-response @(@handler delete-request)
          get-request (mock/request :get location)
          get-response @(@handler get-request)]
      (is (= 204 (:status delete-response)))
      (is (= 404 (:status get-response))))))

(deftest delete-visits-id-does-not-exist
  (testing "DELETE /visits/<id> (<id> doesn't exist)"
    (let [delete-request (mock/request :delete "/visits/0")
          delete-response @(@handler delete-request)]
      (is (= 204 (:status delete-response))))))
