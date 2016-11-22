(ns coffee-table.bullshit-database-test
  (:require [coffee-table.bullshit-database :as bsd]
            [coffee-table.system :refer [test-system]]
            [clojure.test :refer :all]
            [schema.test]
            [com.stuartsierra.component :as component]))

(use-fixtures :once schema.test/validate-schemas)

(deftest add-visit-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        old-count (count (bsd/visits db))
        _ (bsd/add-visit db visit)
        new-count (count (bsd/visits db))
        new-visits (bsd/visits db)]
    (is (some #{visit} new-visits))
    (is (= new-count (inc old-count)))))

(deftest add-visit-twice-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        first-count (bsd/add-visit db visit)
        second-count (bsd/add-visit db visit)]
    (is (= second-count (inc first-count)))))

(deftest remove-visit-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        new-id (bsd/add-visit db visit)
        _ (bsd/delete-visit db new-id)
        new-visits (bsd/visits db)]
    (is (not (some #{visit} new-visits)))))

(deftest update-visit-test
  (let [system (component/start test-system)
        db (:db system)
        visit {:name "Test Cafe"
               :beverage-ordered "Espresso"
               :beverage-rating 5
               :date (java.util.Date.)}
        new-visit (assoc visit :name "Test Cafe1")
        new-id (bsd/add-visit db visit)
        _ (bsd/update-visit db new-id new-visit)
        new-visits (bsd/visits db)]
    (is (not (some #{visit} new-visits)))
    (is (some #{new-visit} new-visits))))
