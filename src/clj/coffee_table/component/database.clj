(ns coffee-table.component.database
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s]
            [coffee-table.model :as m]
            [coffee-table.db.visits :as dbv]
            [coffee-table.db.users :as dbu]
            [clojure.java.jdbc :as jdbc])
  (:import [java.sql PreparedStatement]))

(def DEFAULT-DB-SPEC
  {:dbtype   "postgresql"
   :dbname   "postgres"
   :user     "postgres"
   :password "password"
   :host     "localhost"
   :port     5432})

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [value metadata index]
    (to-date value)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [value ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime value)))))

(def DBVisit
  "Schema for visit that came from a DB"
  (s/conditional #(contains? % :id) m/Visit))

(def DBVisitResult
  (s/maybe DBVisit))

(s/defn db->visit :- DBVisitResult
  [visit]
  (when-not (nil? visit)
    (into {} (remove (comp nil? second)
                     (clojure.set/rename-keys visit
                                              {:cafe_name :name,
                                               :date_visited :date
                                               :beverage_rating :beverage-rating
                                               :beverage_ordered :beverage-ordered
                                               :beverage_notes :beverage-notes
                                               :service_rating :service-rating
                                               :service_notes :service-notes
                                               :ambience_notes :ambience-notes
                                               :ambience_rating :ambience-rating
                                               :other_notes :other-notes})))))

(s/defrecord Database []
  component/Lifecycle
  (start [this]
    (assoc this :spec DEFAULT-DB-SPEC))
  (stop [this]
    (dissoc this :spec)))

(s/defn new-database
  []
  (map->Database {}))

(s/defn private-user :- (s/maybe m/PrivateUser)
  [component
   username :- s/Str]
  (if-let [puser (dbu/private-user-by-username
                  (:spec component)
                  {:username username})]
    (-> puser
        (update :username str)
        (assoc :roles (if (:is_admin puser) #{:user :admin} #{:user})))))

(s/defn user :- (s/maybe m/PublicUser)
  [component
   username :- s/Str]
  (if-let [puser (private-user component username)]
    (dissoc puser :password)))

(s/defn users :- [m/PublicUser]
  [component]
  (dbu/all-users (:spec component)))

(s/defn visits :- [DBVisit]
  [component]
  (mapv db->visit (dbv/all-visits (:spec component))))

(s/defn visit :- DBVisitResult
  [component
   id :- (s/maybe s/Int)]
  (when-not (nil? id)
    (db->visit (dbv/visit-by-id (:spec component) {:id id}))))

(s/defn add-visit :- DBVisitResult
  [component
   v :- m/Visit]
  (let [{:keys [id]} (first (dbv/insert-visit (:spec component) v))
        res (visit component id)]
    res))

(s/defn delete-visit :- s/Bool
  [component
   visit-id :- s/Int]
  (dbv/delete-visit-by-id (:spec component) {:id visit-id})
  true)

(s/defn update-visit :- DBVisitResult
  [component
   update-visit :- m/Visit]
  (let [update-idx (m/visit-id update-visit)]
    (when-not (nil? (visit component update-idx))
      (dbv/update-visit-by-id (:spec component) update-visit)
      (visit component (m/visit-id update-visit)))))
