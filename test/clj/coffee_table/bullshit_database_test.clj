(ns coffee-table.bullshit-database-test
  (:require [coffee-table.bullshit-database :as bsd]
            [coffee-table.system :refer [test-system]]
            [clojure.test :refer :all]
            [schema.test]
            [com.stuartsierra.component :as component]
            [coffee-table.model :as m]))

(use-fixtures :once schema.test/validate-schemas)

(deftest add-visit-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        old-count (count (bsd/visits db))
        result (bsd/add-visit db visit)
        new-count (count (bsd/visits db))
        new-visit (bsd/visit db (m/visit-id result))]
    (is (= new-visit result))
    (is (= new-count (inc old-count)))))

(deftest add-visit-twice-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        visit1 (bsd/add-visit db visit)
        visit2 (bsd/add-visit db visit)]
    (is (= (m/visit-id visit2) (inc (m/visit-id visit1))))))

(deftest remove-visit-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        new-visit (bsd/add-visit db visit)
        _ (bsd/delete-visit db (m/visit-id new-visit))
        new-visits (bsd/visits db)]
    (is (not (some #{new-visit} new-visits)))))

(deftest remove-visit-twice-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        new-visit (bsd/add-visit db visit)
        _ (bsd/delete-visit db (m/visit-id new-visit))
        _ (bsd/delete-visit db (m/visit-id new-visit))
        new-visits (bsd/visits db)]
    (is (not (some #{new-visit} new-visits)))))

(deftest update-visit-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        added-visit (bsd/add-visit db visit)
        updated-visit (assoc added-visit :name "Test Cafe1")
        _ (bsd/update-visit db updated-visit)
        new-visits (bsd/visits db)]
    (is (not (some #{added-visit} new-visits)))
    (is (some #{updated-visit} new-visits))))

(deftest update-visit-with-invalid-id-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        new-visit (bsd/add-visit db visit)
        pending-visit (update new-visit :id inc)
        updated-visit (bsd/update-visit db pending-visit)]
    (is (nil? updated-visit))))
