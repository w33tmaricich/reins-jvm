(ns reins-runtime.inject
  (:require [reins-runtime.utils.messages :as msg]))

(defn string->list
  "Converts the string into a list of executable code."
  [s]
  (read-string s))

(defn string->fn
  "Converts a string to a function. Usially used on a string declare."
  [s]
  (eval (first (string->list s))))

(defn file->list
  "Converts the contents of a file into a list of information."
  [file]
  (string->list (slurp file)))

(defn execute-list
  "Takes a list of specially formatted waldo code and executes each segment"
  [code-list]
  (if (empty? code-list)
    true
    (do
      (eval (first code-list))
      (recur (rest code-list)))))

(defn hand-briefcase
  "Takes a list of specically formatted waldo code and returns the briefcase"
  [message]
  (eval (string->list message)))
