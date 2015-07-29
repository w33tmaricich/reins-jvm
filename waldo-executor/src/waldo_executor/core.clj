(ns waldo-executor.core
  (:require [waldo-executor.inject :refer :all]
            [waldo-executor.communications.spread :as spread]
            [waldo-executor.utils.messages :as msg])
  (:gen-class))

(defn logic
  "Holds the sequence of events that will take place in the executor."
  [& args]
  (let [connection (spread/connect (spread/connection-information "127.0.0.1" 4803 "waldo-executor" false false))
        grp-retrieve (spread/join-group "retrieve" connection)]
    (spread/push connection grp-retrieve "(+ 1 1)")
    (def code (spread/pull connection))
    (def code-list (string->list code))
    (execute-list code-list)

    (spread/disconnect connection)))



  ;(let [code-list (file->list "/tmp/test.clj")
        ;info (hand-briefcase code-list)
        ;config (:config info)
        ;data (:data info)
        ;fmap (:fmap info)
        ;trail (:trail info)]

    ;(msg/data 'info info)
    ;(msg/data 'config config)
    ;(msg/data 'data data)
    ;(msg/data 'fmap fmap)
    ;(msg/data 'trail trail)

    ;(execute-list code-list)
    ;((:do-next fmap))))


(defn -main
  "Starts when the application launches."
  [& args]
  (apply logic args))
