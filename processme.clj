{:config {:waldo-executor "exe-1"}
 :data {:great "Persona 4"
        :best "Silent hill 2"}
 :do-next "check-both"}

(ns silent.hill.best.processme)

(defn bestgame?
  [game]
  (if (= "Silent hill 2" game)
    (println "Yes it is!")
    (println "Close, but not quite")))

(defn check-both
  [briefcase]
  (let [data (:data briefcase)]
    (println "Which is the best game? " (:great data) " or " (:best data) "?")
    (println "Is " (:great data) " the best?")
    (bestgame? (:great data))
    (println "Is " (:best data) " the best?")
    (bestgame? (:best data))))
