{:config {:exe-name-spread "exe-1"}
 :data {}
 :do-next "do-next"}

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
(defn do-next
  "This function runs as soon as the agent arrives at its destination."
  [briefcase]
  (println "Hello, World!"))
