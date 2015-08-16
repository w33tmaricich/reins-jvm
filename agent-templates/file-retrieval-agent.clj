;;;; File Retrieval Agent
;;;;
;;;; This agent takes a location, retrieval file path, and destination file path. The agent jumps to the computer that has the file,
;;;; slurps up its contents, and then transports the file to another computer.
;;;;
;;;; 1. Move to computer that has file.
;;;; 2. Slurp contents of file.
;;;; 3. Move to computer that wants a copy of the file.
;;;; 4. Place file onto the file system.

{:config {:id :mobile
          :communication-method "TCP"
          :port 1612}
 :data {:from-ip "192.168.1.7"
        :to-ip "192.168.1.18"
        :from-path "/home/w33t/fix-monitors.sh"
        :to-path "/Users/alexandermaricich/Desktop/backup-monitor-fix.sh"
        :file-contents ""}
 :do-next "move-to-computer-that-has-file"}

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
      (.close out-stream)
      (println " [ suc ] --> Move to" ip ":" port "was successful."))
    (catch Exception e (println "Error: " e))))

(defn move-to-computer-that-has-file
  "um."
  [briefcase]
  (println "Traveling to computer!")
  (move (:from-ip (:data briefcase))
        (:port (:config briefcase))
        {:config (:config briefcase)
         :data (:data briefcase)
         :do-next "((declare slurp-contents-of-file))"
         :code (:code briefcase)}))

(defn slurp-contents-of-file
  "again, um."
  [briefcase]
  (println "Copying file!")
  (move (:to-ip (:data briefcase))
        (:port (:config briefcase))
        {:config (:config briefcase)
         :data {:from-ip (:from-ip (:data briefcase))
                :to-ip (:to-ip (:data briefcase))
                :from-path (:from-path (:data briefcase))
                :to-path (:to-path (:data briefcase))
                :file-contents (slurp (:from-path (:data briefcase)))}
         :code (:code briefcase)
         :do-next "((declare release-file))"}))

(defn release-file
  "Places the file into the file system"
  [briefcase]
  (println "Pasting file!")
  (spit (:to-path (:data briefcase)) (:file-contents (:data briefcase))))
