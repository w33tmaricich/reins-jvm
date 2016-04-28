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
(def debug-mode false)

(def id (comp :id :config))
;(def retrieve-socket-input (comp DataInputStream. BufferedInputStream. .getInputStream .accept))
(def initialize-briefcase (comp execute-list string->list :code))
(def run-next (comp string->fn :do-next))

(def PORT 1612)
(def EXCLUDE #{}) ; A list of agent id's that are not allowed to run.
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
               :test
               :transport}) ; A list of agent id's that are allowed to run.

(defn open-briefcase
  "Initializes all briefcase code and then runs a function within the agent
   if specified."
  [briefcase]
  (msg/debug debug-mode "open-briefcase")
  ; Initialize all functions and variables
  (if (initialize-briefcase briefcase)
  ; If the briefcase specifies a function to run
    (if (:do-next briefcase)
      (let [do-next# (-> briefcase
                        :do-next
                        string->fn)]
        (msg/data do-next# (id briefcase))
        ; Run the specified function
        (do-next# briefcase))
    (msg/err "Unable to read in agent."))))

(defn allowed-entry?
  "Checks the config of an agent's briefcase to make sure that it is allowed to be run."
  [briefcase include-list exclude-list]
  (msg/debug debug-mode "allowed-entry?")
  (if (empty? include-list)
    (if (empty? exclude-list)
      true
      (not (contains? exclude-list (id briefcase))))
    (contains? include-list (id briefcase))))

(defn logic
  "Holds the sequence of events that will take place in the executor."
  [& args]

  (when using-port
    (msg/suc "Waiting for code over port " PORT)
    (let [socket (ServerSocket. PORT)]
      (loop [incoming-connection (-> socket
                                     .accept
                                     .getInputStream
                                     BufferedInputStream.
                                     DataInputStream.)]
        (future
          (try
            ; Get the briefcase
            (msg/debug debug-mode "Get the briefcase")
            (let [briefcase (hand-briefcase (.readUTF incoming-connection))]
              ; Close the connection. We have everything we need.
              (msg/debug debug-mode "Close the connection. we have everything we need.")
              (.close incoming-connection)
              ; If the briefcase is allowed to run
              (msg/debug debug-mode "If the briefcase is allowed to run")
              (if (allowed-entry? briefcase INCLUDE EXCLUDE)
                ; Initialize and deploy agent on the system.
                (open-briefcase briefcase)
                (msg/debug debug-mode "Rejecting agent with id of" (id briefcase))))
            (catch Exception e (do (println "End of file: " e)))))
        (recur (-> socket
                   .accept
                   .getInputStream
                   BufferedInputStream.
                   DataInputStream.))))))

(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
