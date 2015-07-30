(ns waldo.waldo
  (:require [waldo.communications.spread :as spread]
            [waldo.utils.messages :as msg]))

(defn new-briefcase
  "Creates a new briefcase."
  []
  {:config {}
   :trail ["127.0.0.1"]
   :fnmap {:do-next "(declare runme)"}
   :data {}
   :code "(defn runme [] (println \"it worked!\"))"})

(defn send-briefcase-spread
  "Sends the briefcase via spread to a specified group"
  [briefcase group-name]
  (let [connection (spread/connect (spread/connection-information "127.0.0.1" 4803 "waldo-test" false false))
        send-group (spread/join-group group-name connection)
        str-briefcase (str briefcase)]
    (spread/push connection send-group str-briefcase)
    (msg/suc "Message sent via spread")
    (spread/disconnect connection)))

(defn do-next
  "Sets the :do-next keyword to the function that is specified"
  [quoted-function briefcase]
  {:config (:config briefcase)
   :trail (:trail briefcase)
   :fnmap {:do-next (str "(declare " quoted-function ")")}
   :data (:data briefcase)
   :code (:code briefcase)})

