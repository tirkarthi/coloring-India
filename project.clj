(defproject coloring-india "0.1.0-SNAPSHOT"
  :description "A simple graph coloring problem solver"
  :license {:name "MIT License"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.specs.alpha "0.1.10"]
                 [org.clojure/core.logic "0.8.11"]
                 [cheshire "5.7.1"]]
  :main ^:skip-aot coloring-india.core
  :target-path "target/%s"
  :jvm-opts ^replace []
  :resource-paths ["resources"]
  :profiles {:profiles {:dev {:dependencies [[alembic "0.3.2"]
                                             [fipp "0.6.10"]]}}
             :uberjar {:aot :all}})
