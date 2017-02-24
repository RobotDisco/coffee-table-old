(ns coffee-table.db.migrations
  (:require [migratus.core :as migratus]
            [coffee-table.database :refer [DEFAULT-DB-SPEC]]))

(def config {:store :database
             :migration-dir "migrations"
             :migration-table-name "schema_migrations"
             :db DEFAULT-DB-SPEC})
