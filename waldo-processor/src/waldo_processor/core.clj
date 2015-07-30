(ns waldo-processor.core
  (:gen-class))

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
  [data code]
  {:config {}
   :fnmap {:do-next nil}
   :data (:data data)
   :code (apply str code)})

(defn -main
  "Create a processed agent."
  [& args]
  (let [code-list (file->list (first args))
        code (code->string (rest code-list))
        briefcase (make-briefcase (first code-list) code)]
    (println briefcase)))
