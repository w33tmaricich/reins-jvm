;;;; Gensym test
;;;;
;;;; I need to test symbol colisions within this type of archetecture.

{:config {:id :test
          :communication-method :tcp
          :port 1612}
 :data {:string "This is a string"}
 :do-next "start-here"}

(defmacro defgensym
  "Same as def but generates a random symbol."
  [symbol-definition]
  `(def dgen# ~symbol-definition))

(defn start-here
  [briefcase]
  (println `start-here)
  (println (defgensym (println :awesome))))
