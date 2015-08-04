(ns waldo-processor.core
  (:require [waldo-processor.communications.spread :as spread]
            [waldo-processor.utils.messages :as msg])
  (:gen-class))
(import java.net.Socket)
(import java.io.DataOutputStream)

(def using-spread false)
(def using-port true)

(defn string->list
  "Converts the string into a list of executable code."
  [s]
  (read-string (str \( s \))))

(defn file->list
  "Converts the contents of a file into a list of information."
  [file]
  (string->list (slurp file)))

(defn code->string
  "Converts the code passed into a string"
  [code]
  (str code))

(defn make-briefcase
  "Creates a briefcase just from data"
  [header code]
  {:config (:config header)
   :do-next (str "((declare " (:do-next header) "))")
   :data (:data header)
   :code (apply str code)})

(defn -main
  "Create a processed agent."
  [& args]
  (println args)
  (let [code-list (file->list (first args))
        code (code->string (rest code-list))
        briefcase (make-briefcase (first code-list) code)]

    (while using-spread
      (let [connection (spread/connect (spread/connection-information "127.0.0.1" 4803 "waldo-processor" false false))
            grp-execute (spread/join-group "waldo-execute" connection)]
        (msg/data 'briefcase briefcase)
        (spread/push connection grp-execute (str briefcase))
        (spread/disconnect connection)))

    (while using-port
      (let [socket (Socket. "127.0.0.1" 8001)
            out-stream (DataOutputStream. (.getOutputStream socket))]
        (msg/message "prt" "Sending via port 8001")
        (msg/data 'briefcase briefcase)
        (.writeByte out-stream 1)
        (.writeUTF out-stream (str briefcase))
        (.flush out-stream)))))
