(ns sc3.proc
  (:import [java.lang ProcessBuilder])
  (:use [clojure.java.io :only [reader writer]]))

(defn spawn
  "Start a process and leave it running, i.e. a non-blocking spawn v4"
   [& args]
  (let [process (-> (ProcessBuilder. args)
                  (.start))]
    {:out (-> process
            (.getInputStream)
            (reader))
     :err (-> process
            (.getErrorStream)
            (reader))
     :in (-> process
           (.getOutputStream)
           (writer))
     :process process}))
