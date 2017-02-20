(defproject net.robot-disco/coffee-table "0.0.1"
  :description "Review and log café visits"
  :url "http://robot-disco.net/coffee-table"
  :license {:name "GNU General Public License 3.0"
            :url "http://www.gnu.org/licenses/gpl-3.0.txt"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [prismatic/schema "1.1.3"]
                 [com.stuartsierra/component "0.3.1"]
                 [aleph "0.4.1"]
                 [yada "1.1.46"]
                 [ring/ring-mock "0.3.0"]
                 [cheshire "5.6.3"]
                 [com.layerware/hugsql "0.4.7"]
                 [org.postgresql/postgresql "9.4.1212"]]
  :profiles {:dev {:dependencies [[reloaded.repl "0.2.3"]]
                   :source-paths ["dev"]}}
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"])
