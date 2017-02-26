(ns coffee-table.database-test
  (:require [clojure.test :refer :all]
            [coffee-table
             [model :as m]
             [system :refer [test-system]]]
            [coffee-table.component.database :as dbc]
            [com.stuartsierra.component :as component]
            schema.test))

(use-fixtures :once schema.test/validate-schemas)

(deftest add-visit-test
  (let [system (component/start (test-system))
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        old-count (count (dbc/visits db))
        result (dbc/add-visit db visit)
        new-count (count (dbc/visits db))
        new-visit (dbc/visit db (m/visit-id result))]
    (is (= new-visit result))
    (is (= new-count (inc old-count)))))

(deftest add-visit-twice-test
  (let [system (component/start (test-system))
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        visit1 (dbc/add-visit db visit)
        visit2 (dbc/add-visit db visit)]
    (is (= (m/visit-id visit2) (inc (m/visit-id visit1))))))

(deftest remove-visit-test
  (let [system (component/start (test-system))
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        old-count (count (dbc/visits db))
        new-visit (dbc/add-visit db visit)
        mid-count (count (dbc/visits db))
        _ (dbc/delete-visit db (m/visit-id new-visit))
        new-visits (dbc/visits db)
        new-count (count new-visits)]
    (is (not (some #{new-visit} new-visits)))
    (is (= old-count (dec mid-count) new-count))))

(deftest remove-visit-twice-test
  (let [system (component/start (test-system))
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        old-visits (dbc/visits db)
        new-visit (dbc/add-visit db visit)
        _ (dbc/delete-visit db (m/visit-id new-visit))
        _ (dbc/delete-visit db (m/visit-id new-visit))
        new-visits (dbc/visits db)]
    (is (not (some #{new-visit} new-visits)))
    (is (= (count old-visits) (count new-visits)))))

(deftest update-visit-test
  (let [system (component/start (test-system))
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        added-visit (dbc/add-visit db visit)
        updated-visit (assoc added-visit :name "Test Cafe1")
        _ (dbc/update-visit db updated-visit)
        new-visits (dbc/visits db)]
    (is (not (some #{added-visit} new-visits)))
    (is (some #{updated-visit} new-visits))))

(deftest update-visit-with-invalid-id-test
  (let [system (component/start (test-system))
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        new-visit (dbc/add-visit db visit)
        pending-visit (update new-visit :id inc)
        updated-visit (dbc/update-visit db pending-visit)]
    (is (nil? updated-visit))))

(deftest get-existing-visit
  (let [system (component/start (test-system))
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        new-visit (dbc/add-visit db visit)
        fetched-visit (dbc/visit db (m/visit-id new-visit))]
    (is (= fetched-visit new-visit))))

(deftest get-nonexisting-visit
  (let [system (component/start (test-system))
        db (:db system)
        visits-count (count (dbc/visits db))
        fetched-visit (dbc/visit db (inc visits-count))]
    (is (= fetched-visit nil))))
