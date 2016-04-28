{:config {:id :test}
 :data {:greeting "Hello, World!"}
 :do-next "hello-world"}

(defn hello-world
  "This function runs as soon as the agent arrives at its destination."
  [briefcase]
  (println (:greeting (:data briefcase))))
