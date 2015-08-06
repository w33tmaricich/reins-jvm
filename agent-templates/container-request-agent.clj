;;;; Container Request Agent
;;;;
;;;; The container request agent asks for access to a given container. If the container
;;;; exists at the anticipated location, then the container sends its data to the
;;;; container request agent.

{:config {:communication-method "TCP"
          :listen-port 8023 ; The port the container request agent will listen on.
          :home "127.28.12.42"
          :container-port 1615
          :container-ip "172.28.16.67"}
 :data {}
 :do-next "access-container"}

(import java.net.Socket)
(import java.net.ServerSocket)
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
      (.close out-stream)
      (println " [ suc ] --> Request sent to" ip ":" port))
    (catch Exception e (println "Error: " e))))

(defn access-container
  "Sends a message to the container in order to attempt to access the data it holds."
  [briefcase]
  (send-req
    (:container-ip (:config briefcase))
    (:container-port (:config briefcase))
    {:access-code "supersecret"
     :response-method {:type "TCP"
                       :ip (:home (:config briefcase))
                       :port (:listen-port (:config briefcase))}})
  (listen briefcase))

(defn listen
  "Listens for a response from the container."
  [briefcase]
  (let [socket (ServerSocket. (:listen-port (:config briefcase)))]
    (println " [ suc ] --> Waiting for data access request.")
    (let [incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))
          message (.readUTF incoming-connection)]
      (.close incoming-connection)
      (.close socket)
      (println message))))
