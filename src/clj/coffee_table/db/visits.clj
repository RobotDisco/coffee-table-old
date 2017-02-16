(ns coffee-table.db.visits
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "coffee_table/db/sql/visits.sql")
(hugsql/def-sqlvec-fns "coffee_table/db/sql/visits.sql")
