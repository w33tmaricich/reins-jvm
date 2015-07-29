(defproject waldo "0.1.0-SNAPSHOT"
  :description "The waldo library. The core is a small example of its usage."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot waldo.core
  :target-path "target/%s"
  :resource-paths ["resources/spread.jar"]
  :profiles {:uberjar {:aot :all}})
