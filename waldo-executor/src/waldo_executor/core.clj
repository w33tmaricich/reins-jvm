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
                                                                  (:private-name spread-con)
                                                                  (:priority spread-con)
                                                                  (:group-membership spread-con)))
        grp-retrieve (spread/join-group retrieve-group connection)]
    (msg/suc "Waiting for code...")
    (loop [message (spread/pull connection)] ; Retrieve code to be run from spread
      (try
        (do
          ; Get the briefcase
          (msg/suc "Retrieved agent: " message)
          (def briefcase (hand-briefcase message))
          (msg/data 'briefcase-code (:code briefcase))
          ; Execute the code
          (execute-list (string->list (:code briefcase)))
          ; Run the next relevant function
          ;(def do-next (read-string (:do-next (:fnmap briefcase))))
          (def do-next (string->fn (:do-next (:fnmap briefcase))))
          (do-next))
          ;(execute-list (string->list code))

        (catch Exception e (do
                             (msg/err (str "Unable to execute code: " (.getMessage e)))
                             (spread/disconnect connection))))
      (recur (spread/pull connection)))))

(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
