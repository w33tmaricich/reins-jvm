;;;; jumpingnumbers.clj
;;;; 
;;;; This agent simpley increments a number and pushes it to the terminal. Once it has printed the number
;;;; and waited for two seconds, it then uses spread to jump to another executor.

{:config {:id :test
          :exe-name-spread "exe-1"}
 :data {:number 1}
 :do-next "increment-counter"}

;(ns count.jumpingnumbers)

;;;; Spread jumping library

(import spread.SpreadConnection)
(import spread.SpreadGroup)
(import spread.SpreadMessage)
(import java.net.InetAddress)

;;; Connection Information
(def connection-keys #{:inet-address :port :private-name :priority :group-membership})

;;; Connection Functions
(defn create-inet-address
  "Creates a java.net.InetAddress"
  [ip]
  (InetAddress/getByName (str ip)))

(defn connection-information
  "Creates a map of connection information based upon the given values."
  [ip port private-name priority group-membership]
  {:inet-address (create-inet-address ip)
   :port port
   :private-name private-name
   :priority priority
   :group-membership group-membership})

(defn valid-connection-information?
  "Checks the connection map to see if we have all required information"
  [connection-information valid-keys]
  (loop [info connection-information
         keywords valid-keys]
    (if (empty? keywords)
      true
      (if (contains? info (first keywords))
        (recur info (rest keywords))
        false))))

(defn connect
  "Connect to a spread daemon. Takes a map of :inet-address :port :private-name :priority and :group-memebership"
  [connection-information]
  (let [connection (SpreadConnection.)]
    (when (empty? connection-information)
        (println 'connection-information "was empty. Please pass in a valid map")
        false)
    (when (not (valid-connection-information? connection-information connection-keys))
      (println 'connection-information "does not have the proper fields. Please check keywords.")
      false)

    (try
      (do
        (.connect connection
                  (:inet-address connection-information)
                  (:port connection-information)
                  (:private-name connection-information)
                  (:priority connection-information)
                  (:group-membership connection-information))
        (println "Connection established")
        connection)
      (catch Exception e (println (str "Connection failure: " (.getMessage e)))))))


(defn disconnect
  "Disconnect from a spread daemon"
  [connection]
  (try
    (do
      (.disconnect connection))
    (catch Exception e (println (str "Could not disconnect from spread: " (.getMessage e))))))

(defn join-group
  "Joins a specific group using the specified connection"
  [group-name connection]
  (let [group (SpreadGroup.)]
    (.join group connection group-name)
    group))

(defn leave
  "Leaves the specified group"
  [group]
  (.leave group))

(defn push
  "Send a message using spread. Takes a connection object, group object, reliability setting, and map data to be sent"
  [connection group data]
  (let [message (SpreadMessage.)]
    (.addGroup message group)
    (.setData message (.getBytes data))
    (.multicast connection message)))

(defn pull
  "Retrieves a message on the current connection and returns it as a map"
  [connection]
  (let [message (.receive connection)]
    (if message
      (String. (.getData message) "UTF-8"))))

;;;; Agent logic
(defn increment-counter
  "Increments the data counter, displays it, and then sends the data to another machine"
  [briefcase]
  (let [number (inc (:number (:data briefcase)))
        connection (connect (connection-information "127.0.0.1" 4803 (str "bobby" (rand-int 10000)) false false))
        exe-grp (join-group "waldo-execute" connection)]
    (println number)
    (Thread/sleep 2000)
    (if (< number 20)
      (push connection exe-grp (str {:config {:exe-name-spread (if (= (:exe-name-spread (:config briefcase)) "exe-1")
                                                          "exe-2"
                                                          "exe-1")}
                              :data {:number number}
                              :do-next (:do-next briefcase)
                              :code (:code briefcase)})))
    (disconnect connection)))
