(ns waldo.core
  (:require [clojure.repl :refer [source source-fn]]
            [waldo.waldo :refer :all]
            [waldo.communications.spread :as spread])
  (:gen-class))

(defn printme
  "This is a function that prints a sentance"
  []
  (println "This sentance was printed from within the printme function."))

(defn print-src [func]
  (println (with-out-str (clojure.repl/source func))))

(defn -main
  "A simple test of the waldo library"
  [& args]
  ; Create the briefcase
  (def briefcase (new-briefcase))
  ; send it over as a spread message
  (send-briefcase-spread briefcase "waldo-execute"))
  ;(printme)
  ;(print-src printme))
