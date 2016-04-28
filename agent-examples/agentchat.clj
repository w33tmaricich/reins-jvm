;;;; Agent Chat
;;;;
;;;; This is a super simple chat client implemented in a mobile agent.

{:config {:communication-method "TCP"
          :port 8002}
 :data {:current-location "172.28.12.45"
        :partner-location "192.168.1.18"
        :message "Welcome to agent chat!\n======================\nStart typing and press enter to send your message!\n"}
 :do-next "chat"}

(import java.net.Socket)
(import java.io.DataOutputStream)
(import java.io.BufferedOutputStream)

(defn move
  "Jumps to another executor"
  [ip port briefcase]
  (try
    (let [socket (Socket. ip port)
          out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
      (.writeUTF out-stream (str briefcase))
      (.flush out-stream)
      (.close out-stream))
      ;(println " [ suc ] --> Move to" ip ":" port "was successful."))
    (catch Exception e (println "Error: " e))))

(defn chat
  "This function will run when the agent reaches its destination."
  [briefcase]
  (println (:message (:data briefcase)) "\n[you]---v ")
  (move (:partner-location (:data briefcase))
        (:port (:config briefcase))
        {:config (:config briefcase)
         :do-next "((declare chat))"
         :data {:current-location (:partner-location (:data briefcase))
                :partner-location (:current-location (:data briefcase))
                :message (str \[ (:current-location (:data briefcase)) "]---v\n" (read-line))}
         :code (:code briefcase)}))
