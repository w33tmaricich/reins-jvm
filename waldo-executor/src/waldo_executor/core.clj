(ns waldo-executor.core
  (:require [waldo-executor.utils.messages :as msg])
  (:gen-class))

(defn file->list
  "Converts the contents of a file into a list of information."
  [file]
    (read-string (str \( (slurp file) \))))

(defn execute-list
  "Takes a list of specially formatted waldo code and executes each segment"
  [code-list]
  (if (empty? code-list)
    true
    (do
      (eval (first code-list))
      (recur (rest code-list)))))

(defn hand-briefcase
  "Takes a list of specically formatted waldo code and returns the briefcase"
  [code-list]
  (eval (first code-list)))

(defn logic
  "Holds the sequence of events that will take place in the executor."
  [& args]
  (let [code-list (file->list "/tmp/test.clj")
        info (hand-briefcase code-list)
        config (:config info)
        data (:data info)
        fmap (:fmap info)
        trail (:trail info)]

    (msg/data 'info info)
    (msg/data 'config config)
    (msg/data 'data data)
    (msg/data 'fmap fmap)
    (msg/data 'trail trail)

    (execute-list code-list)
    ((:do-next fmap))))


(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
