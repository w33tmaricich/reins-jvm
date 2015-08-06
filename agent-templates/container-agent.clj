;;;; Container Agent
;;;;
;;;; A container agent is nothing more than a mobile, network variable. It sits
;;;; on a network and waits for a TCP connection. When an application requests
;;;; the data it holds, if the application has the proper credentials, the
;;;; container will then connect to the requesting agent and send the data it holds.
;;;;
;;;; Container request format:
;;;;  {:access-code "Some code that doesnt have to be a string"
;;;;   :response-method {:type "TCP"
;;;;                     :ip "127.0.0.1"
;;;;                     :port 1615}}

{:config {:communication-method "TCP"
          :deploy-loc "172.28.16.67"
          :move-port 1612
          :listen-port 1615}      ; The port the container uses to listen to requests.
 :data {:info "You got this from a container!"}
 :do-next "deploy"}

(import java.net.Socket)
(import java.net.ServerSocket)
(import java.io.DataOutputStream)
(import java.io.DataInputStream)
(import java.io.BufferedOutputStream)
(import java.io.BufferedInputStream)

;; Globals
(def ACCESS-CODE "supersecret")

;; Movement Functions
(defn move
  "Jumps to another executor"
  [ip port briefcase]
  (try
    (let [socket (Socket. ip port)
          out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
      (.writeUTF out-stream (str briefcase))
      (.flush out-stream)
      (.close out-stream)
      (println " [ cln ] --> Briefcase sent to" ip ":" port))
    (catch Exception e (println "Error: " e))))

(defn deploy
  "Moves the agent to its specified deploy location"
  [briefcase]
  (move (:deploy-loc (:config briefcase))
        (:move-port (:config briefcase))
        {:config (:config briefcase)
         :data (:data briefcase)
         :do-next "((declare listen))"
         :code (:code briefcase)}))

;; Data Manipulation Functions
(defn string->data
  "Converts the string into a list of executable code."
  [s]
  (read-string s))

;; Data Access Functions
(defn valid-request?
  "Checks to see if the agent has access to be carried."
  [agent-message]
  (try
    (let [message (eval (string->data agent-message))]
      (if (= (:access-code message) ACCESS-CODE)
        true
        false))
    (catch Exception e (do
                         (println "Invalid carrier format: " e)
                         false))))

(defn send-response
  "Sends a response back to the requester."
  [briefcase request response]
  (let [socket (Socket. (:ip (:response-method request))
                        (:port (:response-method request)))
        out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
    (.writeUTF out-stream (str response))
    (.flush out-stream)
    (.close out-stream)
    (.close socket)
    (println " [ con ] --> Response sent.")))

(defn listen
  "Waits and listens for any agents that request access. If the agent has the proper credentials, the data stored within the container is transmitted."
  [briefcase]
  (let [socket (ServerSocket. (:listen-port (:config briefcase)))]
    (println " [ con ] --> Container waiting for data access request.")
    (loop [incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))]
      (let [message (.readUTF incoming-connection)]
        (if (valid-request? message)
          (do
            (println " [ con ] --> Valid request made. Sending stored information")
            (.close incoming-connection)
            ; Send requested data
            (let [mobile-agent-request (eval (string->data message))]
              (send-response briefcase mobile-agent-request (:data briefcase)))
            (recur (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))))
          (recur (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))))))))
