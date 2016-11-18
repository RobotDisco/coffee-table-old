(defproject net.robot-disco/coffee-table "0.0.1"
  :description "Review and log café visits"
  :url "http://robot-disco.net/coffee-table"
  :license {:name "GNU General Public License 3.0"
            :url "http://www.gnu.org/licenses/gpl-3.0.txt"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [prismatic/schema "1.1.3"]]
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"])
