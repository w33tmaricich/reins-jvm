(defproject waldo-executor "0.1.0-SNAPSHOT"
  :description "The waldo executor is an application that allows programs using the waldo library to begin execution after migrating from another system."
  :url "FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot waldo-executor.core
  :target-path "target/%s"
  :resource-paths ["resources/spread.jar"]
  :profiles {:uberjar {:aot :all}})
