;;;; Function Request Agent
;;;;
;;;; The function request agent asks for access to a given function. If the function
;;;; exists at the anticipated location, then the function sends its calcuated value to the
;;;; function request agent after all computations have taken place

{:config {:id :mobile
          :communication-method "TCP"
          :home "127.0.0.1"
          :function-port 1616
          :function-ip "172.28.16.67"}
 :data {}
 :do-next "access-function"}

(import java.net.Socket)
(import java.io.DataOutputStream)
(import java.io.DataInputStream)
(import java.io.BufferedOutputStream)
(import java.io.BufferedInputStream)

(defn send-req
  "Sends a specialized message to the function."
  [ip port message]
  (try
    (let [socket (Socket. ip port)
          out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
      (.writeUTF out-stream (str message))
      (.flush out-stream)
      (println " [ suc ] --> Request sent to" ip ":" port)
      socket)
    (catch Exception e (println "Error: " e))))

(defn retrieve-response
  "Listens for a response from the function."
  [briefcase socket]
  (let [sock socket]
    (println " [ suc ] --> Waiting for data access request.")
    (let [incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream sock)))
          message (.readUTF incoming-connection)]
      (println message))))

(defn access-function
  "Sends a message to the function."
  [briefcase]
  (let [socket (send-req
                 (:function-ip (:config briefcase))
                 (:function-port (:config briefcase))
                 {:access-code "add"
                  :parameters [1 2 3 4 5]})]
    (retrieve-response briefcase socket)))
