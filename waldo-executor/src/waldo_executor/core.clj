(ns waldo-executor.core
  (:require [waldo-executor.utils.messages :as msg])
  (:gen-class))

(defn logic
  "Holds the sequence of events that will take place in the executor."
  [& args]
  (msg/message "nope" "Hello, World!" args))

(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
