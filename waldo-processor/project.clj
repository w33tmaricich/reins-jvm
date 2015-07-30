(defproject waldo-processor "0.1.0-SNAPSHOT"
  :description "Code pre-processor. This takes a clojure file as a parameter, packages the agent, and sends it off to the executor to be run."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot waldo-processor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
