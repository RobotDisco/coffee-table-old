(ns coffee-table.login
  (:require [ajax.core :as ajax]))

(defn login [username password]
  (ajax/POST "http://localhost:8080/login"
             {:format :json
              :params {:user username
                       :password password}
              :handler (fn [])}))

