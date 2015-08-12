;;;; reins-runtime
;;;;
;;;; The runtime is a constantly executing application that waits for code to
;;;; be sent to it. Once code is recieved, if it has the proper credentials,
;;;; the agent is executed.

(ns reins-runtime.core
  (:require [reins-runtime.inject :refer :all]
            [reins-runtime.utils.messages :as msg])
  (:gen-class))
(import java.net.ServerSocket)
(import java.io.DataInputStream)
(import java.io.BufferedInputStream)

(def using-port true)

(def PORT 1612)
(def EXCLUDE #{""}) ; A list of agent id's that are not allowed to run.
(def INCLUDE #{:carrier
               :cloner
               :container
               :duckling
               :echo
               :function
               :generic
               :master
               :mobile
               :mother
               :transport}) ; A list of agent id's that are allowed to run.

(defn allowed-entry?
  "Checks the config of an agent's briefcase to make sure that it is allowed to be run."
  [briefcase]
  (contains? INCLUDE (:id (:config briefcase))))

(defn logic
  "Holds the sequence of events that will take place in the executor."
  [& args]

  (when using-port
    (msg/suc "Waiting for code over port " PORT)
    (def socket (ServerSocket. PORT))

    (loop [incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))]
      (future
        (try
          ; Get the briefcase
          (let [briefcase (hand-briefcase (.readUTF incoming-connection))]
            ; Close the connection. We have everything we need.
            (.close incoming-connection)
            ; If the briefcase is allowed to run
            (when (allowed-entry? briefcase)
              ; If the briefcase specifies a function to run
              (when (:do-next briefcase)
                ; Initialize all functions and variables
                (execute-list (string->list (:code briefcase)))
                ; Run the relvant function
                (def do-next (string->fn (:do-next briefcase)))
                (do-next briefcase))))
          (catch Exception e (do (println "End of file: " e)))))
      (recur (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))))))


(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
