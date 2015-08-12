;;;; Function Agent
;;;;
;;;; A function aget is nothing more than a mobile, network function. It sits
;;;; on a network and waits for a TCP connection. When an application requests
;;;; to use the function it contains, if the application has the proper credentials,
;;;; the function will then connect to the requesting agent, retrieve data for
;;;; calculating, and return a result when it is finished.
;;;;
;;;; Function request format:
;;;;  {:access-code "Some code that doesnt have to be a string"
;;;;   :parameters [12 4]}

{:config {:id :function
          :communication-method "TCP"
          :deploy-loc "172.28.16.67"
          :move-port 1612
          :listen-port 1616}
 :data {:num-parameters 2}
 :do-next "deploy"}

(import java.net.Socket)
(import java.net.ServerSocket)
(import java.io.DataOutputStream)
(import java.io.DataInputStream)
(import java.io.BufferedOutputStream)
(import java.io.BufferedInputStream)

;; Globals
(def ACCESS-CODE "add")

;; Movement Functions
(defn move
  "Jumps to another executor"
  [ip port briefcase]
  (try
    (let [socket (Socket. ip port)
          out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
      (.writeUTF out-stream (str briefcase))
      (.flush out-stream)
      (.close socket)
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
(defn valid-function-request?
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
  [briefcase request out-stream response]
  (.writeUTF out-stream (str response))
  (.flush out-stream)
  (.close out-stream)
  (println " [ fun ] --> Result sent."))

(defn main-function
  "The function that the application is requesting to use."
  [args]
  (apply + args))

(defn listen
  "Waits and listens for any agents that request access. If the agent has the proper credentials, the data sent is retrieved and the function is run."
  [briefcase]
  (let [socket (ServerSocket. (:listen-port (:config briefcase)))]
    (println " [ fun ] --> Function waiting for execution request.")
    (loop [connected-socket (.accept socket)
           incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream connected-socket)))
           outgoing-connection (DataOutputStream. (BufferedOutputStream. (.getOutputStream connected-socket)))]
      (let [message (.readUTF incoming-connection)]
        (if (valid-function-request? message)
          (do
            (println " [ fun ] --> Valid request made. Executing function.")
            ; Send requested data
            (let [mobile-agent-request (eval (string->data message))]
              (send-response briefcase mobile-agent-request outgoing-connection (main-function (:parameters mobile-agent-request))))
            (let [new-socket (.accept socket)]
              (recur new-socket
                     (DataInputStream. (BufferedInputStream. (.getInputStream new-socket)))
                     (DataOutputStream. (BufferedOutputStream. (.getOutputStream new-socket))))))
          (let [new-socket (.accept socket)]
            (recur new-socket
                   (DataInputStream. (BufferedInputStream. (.getInputStream new-socket)))
                   (DataOutputStream. (BufferedOutputStream. (.getOutputStream new-socket))))))))))
