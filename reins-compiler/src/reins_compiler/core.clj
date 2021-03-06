(ns reins-compiler.core
  (:require [reins-compiler.utils.messages :as msg])
  (:gen-class))

(import java.net.Socket)
(import java.io.DataOutputStream)
(import java.io.BufferedOutputStream)

(def using-port true)

(def IP "127.0.0.1")
(def PORT 1612)

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

    (when using-port
      (let [socket (Socket. IP PORT)
            out-stream (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
        (msg/message "prt" "Sending via port" PORT)
        (msg/data 'briefcase briefcase)
        (.writeUTF out-stream (str briefcase))
        (.flush out-stream)
        (.close out-stream)))))
