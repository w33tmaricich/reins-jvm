(defproject reins-runtime "0.1.0-SNAPSHOT"
  :description "The reins runtime is an application that allows programs using the reins library to begin execution after migrating from another system."
  :url "FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot reins-runtime.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
