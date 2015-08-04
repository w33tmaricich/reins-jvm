{:config {}
 :data {:greeting "Hello, World!"}
 :do-next "do-next"}

(defn do-next
  "This function runs as soon as the agent arrives at its destination."
  [briefcase]
  (println (:greeting (:data briefcase))))
