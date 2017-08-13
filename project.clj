(defproject net.robot-disco/coffee-table "0.0.1"
  :description "Review and log café visits"
  :url "http://robot-disco.net/coffee-table"
  :license {:name "GNU General Public License 3.0"
            :url "http://www.gnu.org/licenses/gpl-3.0.txt"
            :distribution :repo}
  :local-repo ".m2"
  :dependencies [;; Core Language(s)
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.854"]

                 ;; Model schema/verification
                 [prismatic/schema "1.1.6"]

                 ;; Backend state management
                 [com.stuartsierra/component "0.3.2"]

                 ;; Backend HTTP server
                 [aleph "0.4.3"]

                 ;; API: REST framework
                 [yada "1.2.6"]

                 ;; JSON parsing
                 [cheshire "5.7.1"]

                 ;; DB: SQL, migrations
                 [com.layerware/hugsql "0.4.7"]
                 [org.postgresql/postgresql "42.1.4"]
                 [migratus "0.9.8"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]

                 ;; Semantic UI (CSS Framework)
                 [cljsjs/semantic-ui-react "0.71.0-0"]

                 ;; Re-frame (Redux for Clojurescript)
                 [re-frame "0.9.4"]
                 ;; Re-frame AJAX effect handlers
                 [cljs-ajax "0.6.0"]
                 [day8.re-frame/http-fx "0.1.4"]

                 ;; Reagent (React Framework)
                 [reagent "0.7.0"]

                 ;; Reagent/Re-frame state inspector
                 [re-frisk "0.4.5"]

                 ;; Time Processing libraries
                 [com.andrewmcveigh/cljs-time "0.5.1"]]
  :plugins [[lein-figwheel "0.5.9"]
            [lein-cljsbuild "1.1.5"]
            [cider/cider-nrepl "0.14.0"]
            [refactor-nrepl "2.2.0"]
            [migratus-lein "0.5.0"]]
  :profiles {:dev {:dependencies [;; Component/namespace mgmt
                                  [reloaded.repl "0.2.3"]
                                  ;; Testing mocks
                                  [ring/ring-mock "0.3.1"]
                                  ;; CLJS interactive prototype visualizer
                                  [devcards "0.2.3" :exclusions [cljsjs/react cljsjs/react-dom]]
                                  ;; CLJS REPL
                                  [com.cemerick/piggieback "0.2.2"]
                                  [figwheel-sidecar "0.5.12"]
                                  ;; Chrome DevTools extensions for CLJS
                                  [binaryage/devtools "0.9.4"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :source-paths ["dev"]}}
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs" "src/cljc"]
                        :figwheel {:on-jsload coffee-table.core/main}
                        :compiler {:preloads [devtools.preload]
                                   :main coffee-table.core
                                   :output-to "resources/public/js/compiled/coffee_table.js"
                                   :output-dir "resources/public/js/compiled/out_dev"
                                   :asset-path "js/compiled/out_dev"
                                   :optimizations :none
                                   :pretty-print true}}
                       {:id "devcards"
                        :source-paths ["src/cljs" "src/cljc"]
                        :figwheel {:devcards true}
                        :compiler {:preloads [devtools.preload]
                                   :main coffee-table.devcards.core
                                   :asset-path "js/compiled/out_devcards"
                                   :output-to "resources/public/js/compiled/coffee_table_devcards.js"
                                   :output-dir "resources/public/js/compiled/out_devcards"
                                   :source-map-timestamp true}}]}
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj" "test/cljc"]
  :migratus {:store :database
             :migration-dir "migrations"
             :migration-table-name "schema_migrations"
             :db {:dbtype   "postgresql"
                  :dbname   "postgres"
                  :user     "postgres"
                  :password "password"
                  :host     "localhost"
                  :port     5432}})
