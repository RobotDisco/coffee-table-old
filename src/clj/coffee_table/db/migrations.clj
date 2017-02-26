(ns coffee-table.db.migrations
  (:require [coffee-table.component.database :refer [DEFAULT-DB-SPEC]]
            [migratus.core :as migratus]))

(def config {:store :database
             :migration-dir "migrations"
             :migration-table-name "schema_migrations"
             :db DEFAULT-DB-SPEC})
