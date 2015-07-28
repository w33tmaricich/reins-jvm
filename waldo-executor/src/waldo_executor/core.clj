(ns waldo-executor.core
  (:require [waldo-executor.utils.messages :as msg])
  (:gen-class))

(defn file->vector
  "Converts the contents of a file into a vector of information."
  [file]
  (into []
    (read-string (str \( (slurp file) \)))))

(defn import-functions
  "Takes a list of specially formatted waldo code and imports all functions."
  [waldo-list]
  (loop [number-functions (count waldo-list)
         index 4]
    (when (< index number-functions)
      (eval (waldo-list index))
      (recur number-functions (inc index)))))


(defn import-information
  "Takes a list of specically formatted waldo code and returns all data stored."
  [waldo-list]
  (let [info [(eval (waldo-list 0))   ; config
              (eval (waldo-list 1))   ; data
              (eval (waldo-list 2))   ; function map
              (eval (waldo-list 3))]] ; trail
    (import-functions waldo-list)
    info))

(defn logic
  "Holds the sequence of events that will take place in the executor."
  [& args]
  (let [waldo-list (file->vector "/tmp/test.clj")
        info (import-information waldo-list)
        config (info 0)
        data (info 1)
        fmap (info 2)
        trail (info 3)]

    (msg/data 'info info)
    (msg/data 'config config)
    (msg/data 'data data)
    (msg/data 'fmap fmap)
    (msg/data 'trail trail)

    ((:main fmap))))


(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
