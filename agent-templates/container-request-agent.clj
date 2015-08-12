;;;; Container Request Agent
;;;;
;;;; The container request agent asks for access to a given container. If the container
;;;; exists at the anticipated location, then the container sends its data to the
;;;; container request agent.

{:config {:id :mobile
          :communication-method "TCP"
          :home "127.0.0.1"
          :container-port 1615
          :container-ip "192.168.1.18"}
 :data {}
 :do-next "access-container"}

(import java.net.Socket)
(import java.io.DataOutputStream)
(import java.io.DataInputStream)
(import java.io.BufferedOutputStream)
(import java.io.BufferedInputStream)

(defn send-req
  "Sends a specialized message to the carrier."
  [ip port message]
  (try
    (let [socket (Socket. ip port)
          out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
      (.writeUTF out-stream (str message))
      (.flush out-stream)
      (println " [ suc ] --> Request sent to" ip ":" port)
      socket)
    (catch Exception e (println "Error: " e))))

(defn access-container
  "Sends a message to the container in order to attempt to access the data it holds."
  [briefcase]
  (let [socket (send-req
                 (:container-ip (:config briefcase))
                 (:container-port (:config briefcase))
                 {:access-code "supersecret"})]
    (listen briefcase socket))

(defn listen
  "Listens for a response from the container."
  [briefcase socket]
  (let [sock socket]
    (println " [ suc ] --> Waiting for data access request.")
    (let [incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream sock)))
          message (.readUTF incoming-connection)]
      (println message))))
