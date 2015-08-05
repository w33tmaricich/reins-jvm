;;;; Carrier Agent
;;;;
;;;; Carries agents from one location to another. An agent can request to be picked
;;;; up by a carrier agent. If the carrier accepts the request, it will slurp up the
;;;; agent, transport it somewhere, and deploy it once it reaches its destination.
;;;;
;;;; Carrier request format:
;;;;  {:access-code "Somecode that doesnt have to be a string"
;;;;   :agent "(agent)"}

{:config {:communication-method "TCP"
          :move-port 1612         ; The port the carrier uses to move.
          :listen-port 1613}      ; The port the carrier uses to grab agents.
 :data {:home "172.28.12.42"         ; Where the carrier sits and waits for requests.
        :destination "172.28.16.67"  ; Where the carrier will deploy the agent it carries.
        :full false
        :passenger ""}
 :do-next "listen"}

(import java.net.Socket)
(import java.net.ServerSocket)
(import java.io.DataOutputStream)
(import java.io.DataInputStream)
(import java.io.BufferedOutputStream)
(import java.io.BufferedInputStream)

;; Globals
(def ACCESS-CODE "piggyback") ; This is a simple example of an access code. It could be a
                              ; server/username password combination etc.

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
      (println " [ car ] --> Carrier move to" ip ":" port "was successful."))
    (catch Exception e (println "Error: " e))))

;; Data Manipulation Functions

(defn string->data
  "Converts the string into a list of executable code."
  [s]
  (read-string s))

;; Carrier Functions

(defn valid-carry-request?
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

(defn absorb
  "Absorbs an agent for carrying."
  [briefcase message]
  (let [msg (eval (string->data message))]
    {:config (:config briefcase)
     :data {:home (:home (:data briefcase))
            :destination (:destination (:data briefcase))
            :full true
            :passenger (:agent msg)}
     :do-next "((declare deploy-and-return))"
     :code (:code briefcase)}))

(defn remove-passengers
  "Removes passengers from the carrier"
  [briefcase]
  {:config (:config briefcase)
   :data {:home (:home (:data briefcase))
          :destination (:destination (:data briefcase))
          :full false
          :passenger ""}
   :do-next "((declare listen))"
   :code (:code briefcase)})

(defn deploy-and-return
  "Deploys the passenger and returns to home."
  [briefcase]
  ; Deploys the agent onto the executor.
  (move
    (:destination (:data briefcase))
    (:move-port (:config briefcase))
    (:passenger (:data briefcase)))
  (println " [ cry ] --> Carrier deployed agent.")
  (move
    (:home (:data briefcase))
    (:move-port (:config briefcase))
    (remove-passengers briefcase)))

(defn listen
  "Waits and listens for any agents that request access. If the agent has the proper credentials, it is absorbed and moved."
  [briefcase]
  (let [socket (ServerSocket. (:listen-port (:config briefcase)))]
    (println " [ cry ] --> Carrier waiting for movement request.")
    (loop [incoming-connection (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))]
      (let [message (.readUTF incoming-connection)]
        (if (valid-carry-request? message)
          (do
            (println " [ cry ] --> Valid agent found. Moving with agent to executor.")
            (.close incoming-connection)
            (.close socket)
            ; Absorb the agent and move to its deployment location
            (move (:destination (:data briefcase))
                  (:move-port (:config briefcase))
                  (absorb briefcase message)))
          (recur (DataInputStream. (BufferedInputStream. (.getInputStream (.accept socket))))))))))
