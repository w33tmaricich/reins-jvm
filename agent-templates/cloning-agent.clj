;;;; Cloning Agent
;;;;
;;;; A cloning agent deploys many identical agents to a list of computers on
;;;; a network. An agent can request to be duplicated by a cloning agent. If
;;;; the cloning agent accepts the request, it will absorb the agent, and
;;;; broadcast it out to all ips that it is aware of.
;;;;
;;;; Cloning request format:
;;;;  {:access-code "Some code that doesnt have to be a string"
;;;;   :agent "(agent)"}

{:config {:id :cloner
          :communication-method "TCP"
          :deploy-loc "172.28.12.42"
          :move-port 1612
          :listen-port 1614}      ; The port the carrier uses to grab agents.
 :data {:destination ["172.28.12.42" "172.28.16.67"]}  ; Where the cloner will deploy the agent.
 :do-next "deploy"}

(import java.net.Socket)
(import java.net.ServerSocket)
(import java.io.DataOutputStream)
(import java.io.DataInputStream)
(import java.io.BufferedOutputStream)
(import java.io.BufferedInputStream)

;; Globals
(def ACCESS-CODE "multicast")

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

;; Cloning Functions

(defn valid-cloning-request?
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

(defn duplicate
  "Duplicates an agent to every ip that the cloning agent knows of"
  [briefcase mobile-agent]
  (doseq [ip (:destination (:data briefcase))]
    (move
      ip
      (:move-port (:config briefcase))
      (:agent mobile-agent))))

(defn listen
  "Waits and listens for any agents that request access. If the agent has the proper credentials, it is absorbed and duplicated."
  [briefcase]
  (let [socket (ServerSocket. (:listen-port (:config briefcase)))]
    (println " [ cln ] --> Cloner waiting for duplication request.")
    (loop [incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))]
      (let [message (.readUTF incoming-connection)]
        (if (valid-cloning-request? message)
          (do
            (println " [ cln ] --> Valid agent found. Deploying agent across the network.")
            (.close incoming-connection)
            ; Duplicate the message across machines.
            (let [mobile-agent-request (eval (string->data message))]
              (duplicate briefcase mobile-agent-request))
            (recur (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))))
          (recur (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))))))))
