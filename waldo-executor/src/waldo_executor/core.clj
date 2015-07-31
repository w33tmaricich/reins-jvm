(ns waldo-executor.core
  (:require [waldo-executor.inject :refer :all]
            [waldo-executor.communications.spread :as spread]
            [waldo-executor.utils.messages :as msg])
  (:gen-class))

(def retrieve-group "waldo-execute")
(def spread-con {:ip "127.0.0.1"
                 :port 4803
                 :private-name "waldo-executor"
                 :priority false
                 :group-membership false})

(defn logic
  "Holds the sequence of events that will take place in the executor."
  [& args]
  (let [connection (spread/connect (spread/connection-information (:ip spread-con)
                                                                  (:port spread-con)
                                                                  (first args)
                                                                  (:priority spread-con)
                                                                  (:group-membership spread-con)))
        grp-retrieve (spread/join-group retrieve-group connection)]
    (msg/suc "Waiting for code...")
    (loop [message (spread/pull connection)] ; Retrieve code to be run from spread
      (try
        (do
          ; Get the briefcase
          (msg/suc "Retrieved agent.")
          (def briefcase (hand-briefcase message))
          (msg/suc "Initialized agent.")
          (if (:do-next briefcase)
            (do
              ; Execute the code
              (execute-list (string->list (:code briefcase)))
              (msg/suc "Initialized functions.")
              ; Run the next relevant function
              (println)
              (println "Starting Execution:")
              (println "-------------------")
              (println)
              (def do-next (string->fn (:do-next briefcase)))
              (do-next briefcase))
            (msg/err ":do-next not specified. Will not run code.")))
        (catch Exception e (do
                             (msg/err (str "Unable to execute code: " (.getMessage e))))))
      (recur (spread/pull connection)))))

(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
