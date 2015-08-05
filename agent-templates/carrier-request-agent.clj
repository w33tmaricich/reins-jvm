;;;; movemeplox.clj
;;;;
;;;; This agent does not know the location of an executor. It makes a request to 
;;;; a carrier to pick it up. If the carrier accepts, it will capture the agent that
;;;; made the request, move to the location of the executor, and deploy it on that machine.

{:config {:communication-method "TCP"
          :port 1612
          :carrier 1613}
 :data {}
 :do-next "enter-carrier"}

(import java.net.Socket)
(import java.io.DataOutputStream)
(import java.io.BufferedOutputStream)

(defn send-req
  "Jumps to another executor"
  [ip port briefcase]
  (try
    (let [socket (Socket. ip port)
          out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
      (.writeUTF out-stream (str briefcase))
      (.flush out-stream)
      (.close out-stream)
      (println " [ suc ] --> Request sent to carrier at" ip ":" port))
    (catch Exception e (println "Error: " e))))

(defn enter-carrier
  "Sends a message to the carrier in order to attempt to be picked up."
  [briefcase]
  (send-req
    "127.0.0.1"
    (:carrier (:config briefcase))
    {:access-code "piggyback"
     :agent {:config (:config briefcase)
             :data (:data briefcase)
             :do-next "((declare do-next))"
             :code (:code briefcase)}}))

(defn do-next
  "This function will run when the agent reaches its destination."
  [briefcase]
  (println "Hello, World!"))
