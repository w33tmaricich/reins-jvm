(ns waldo-processor.utils.messages)

;;; Create Output Strings
(defn box->
  "[ box ] --> messages"
  [box & messages]
  (apply str " [" (.substring (str box) 0 3) "] -->" messages))

;;; Send Concole Messages
(defn message
  "Send output to the terminal that is formmatted all pretty"
  [status & messages]
  (apply println " [" (.substring (str status) 0 3) "] -->" messages))

(defn debug
  "Displays a debug message only if a debug flag is set"
  [debug-mode & messages]
  (when debug-mode
    (apply message "dbg" messages)))

(defn data
  "Displays a piece of data"
  [label-name label]
  (message "dta" label-name label))

(defn err
  "Displays a standard error message"
  [& messages]
  (apply message "err" messages))

(defn fix
  "Displays a fixme message"
  [& messages]
  (apply message "$$$" "FIXME:" messages))

(defn suc
  "Displays a success message"
  [& messages]
  (apply message "suc" messages))
