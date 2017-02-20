(ns coffee-table.database-test
  (:require  [coffee-table.database :as dbc]
             [coffee-table.system :refer [test-system]]
             [clojure.test :refer :all]
             [schema.test]
             [com.stuartsierra.component :as component]
             [coffee-table.model :as m]))

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
