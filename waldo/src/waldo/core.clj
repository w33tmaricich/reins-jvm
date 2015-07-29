(ns waldo.core
  (:require [waldo.waldo :refer :all]
            [waldo.communications.spread :as spread])
  (:gen-class))

(defn -main
  "A simple test of the waldo library"
  [& args]
  ; Create the briefcase
  (def briefcase (new-briefcase))
  ; send it over as a spread message
  (send-briefcase-spread briefcase "waldo-execute"))
