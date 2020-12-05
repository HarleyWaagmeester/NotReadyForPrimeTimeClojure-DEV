(ns sc3.app2
  (:require (clojure
             [pprint :refer :all]))
  (:require [ring.adapter.jetty :as jetty])
  (:require [ring.util.response :as r])
  (:require [ring.util.request :as rr])
  (:require (hiccup
             [core :refer :all]
             [form :refer :all]
             [util :refer :all]
             [element :refer :all]
             [page :refer :all]))
;;  (:require (hickory
;;             [core :refer :all]))
;;  (:require [hickory.core :as hickory])
  (:require
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :refer [files resources not-found]])
  ;;             [core :only (GET POST defroutes)]))
  (:require [clojure.java.shell :refer [sh] :as shell])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [sc3.utilities.utilities :refer :all])
  (:require [sc3.proc :as process])
  (:gen-class))

;; Dis is a vewwy teenee beta.
;; This is a web app providing an API to access a homepage and it's supporting resources.
;; This is deployable as a standalone jar file to other hosts.(not really)

;; When working with this code in the cider nrepl interface,
;; you can run the eclipse.org jetty webserver in the repl in the sc3.app2 namespace with this:
;; "(def server (jetty/run-jetty #'app {:port 7777 :join? false}))".
;; Also, you will find that the function (-main) calls (start-server), and that will also work in the repl.


;;;;;;;;;;;;;;;;;;;; Runtime Environment Deterministics ;;;;;;;;;;;;;
(defn- keywordize [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))



(defn- read-system-environment
"Use java to get the linux environment settings."
  []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

;; Functions defining the Clocss language hosted on HTML and CSS.
;;(def clojure-css-language 1)


(defn div
  "Clocss definition"
  []
  (println "<div>"))

(defn div_off
  "Clocss definition"
  []
  (println "</div>"))
  
;; use flex for floats
(defn flexbox
  "Clocss definition"
  []
  (println "<div class='flexbox';>"))

(defn flexbox_off
  "Clocss definition"
  []
  (println "</div>"))

;; span colors
(defn span_color
  "Clocss definition"
  [str]
  (if (= str "green")
  (println "<span class=span_green>")))

(defn span_color_off
  [str]
  "Clocss definition"
  (if (= str "off")
    (println"</span>")))

(defn div33
  "Clocss definition"
  []
  (println"<div class='div33'>"))

(defn div66
  "Clocss definition"
  []
  (println"<div class='div66'>"))

(defn div33_float_right
  "Clocss definition"
  []
  (println "<div class='div33_float_right'>"))

(defn div33_float_left
  "Clocss definition"
  []
  (println"<div class='div33_float_left'>"))

(defn div66_float_left
  "Clocss definition"
  []
  (println"<div class='div66_float_left'>"))

(defn div66_float_right
  "Clocss definition"
  []
  (println"<div class='div66_float_right'>"))

;; Shorthand for span_float_...
(defn span_float_left
  "Clocss definition"
  []
  (println"<span class='span_float_left'>"))

(defn span_float_right
  "Clocss definition"
  []
  (println"<span class='span_float_right'>"))

(defn span_off
  "Clocss definition"
  []
  (println"</span>"))

(defn float_off
  "Clocss definition"
  []
  (println"</div><br>"))

(defn br
  "Clocss definition"
  []
  (println"<br>"))

(defn div_off
  "Clocss definition"
  []
  (println"</div>"))



(defn clocss
  "clocss experiment"
  [& args]
  (->
   (r/response
    (with-out-str
      (println "<head><link rel='stylesheet' href='/css/ip.css'></head>")
      (div)
      (div33_float_left)
      (println "&nbsp")
      (div_off)
      (div33_float_left)
      (println "&nbsp")
      (div_off)
      (div33_float_left)
      (span_float_right)
      (span_color "green") 
      (println "33% span float right")
      (span_off)
      (span_off)
      (div_off)
      )
    )
   (r/header "Content-Type" "text/html; charset=utf-8")))



;;;;;;;;;;;; html formatting using hiccup ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- html-doc 
  [body] 
  (html 
   (doctype :html4) 
   body))

;;;; read a hiccup file and convert it to html
(defn- make-html-page-from-hiccup-file [filename]
  (html (with-open [r (java.io.PushbackReader.
                       (clojure.java.io/reader filename))]
          (let [eof (gensym)]
            (doall(take-while #(not= % eof) (repeatedly #(read r false eof))) )))))

(defn- make-html-document-with-hiccup-file [filename]
  (html-doc (make-html-page-from-hiccup-file filename)))


(defn- make-auto-refresh-html-doc-from-hiccup-file
  [title seconds & body] 
  (html 
   (doctype :html4) 
   [:html 
    [:head 
     [:title title]
     [:meta {:http-equiv "refresh" :content seconds}]]
    [:body 
     [:div 
      [:h2 
       ;; Pass a map as the first argument to be set as attributes of the element
       [:a {:href "/"} "kseti"]]]
     body]]))

;;;;;;;;;;;; end of html formatting using hiccup ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;; functions that transmute the resource files into responses ;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- html-static-docs
  [request]
  (spit "app2.log" "\n===========================\n" :append true)
  (spit "app2.log" (with-out-str(println request)) :append true)
  (if-let [ x (io/resource (apply str ["html" (get request :uri)])) ]
    (r/response (io/file x))
    (-> (r/response (io/file (io/resource "images/404.jpg"))) (r/content-type "image/jpg")  (r/status 404))))



;;;;; This will get resource files.
(defn- fetch-data [url]
  (let  [con    (-> url java.net.URL. .openConnection)
         fields (reduce (fn [h v] 
                          (assoc h (.getKey v) (into [] (.getValue v))))
                        {} (.getHeaderFields con))
         size   (first (fields "Content-Length"))
         in     (java.io.BufferedInputStream. (.getInputStream con))
         out    (java.io.BufferedOutputStream. 
                 (java.io.FileOutputStream. "out.file"))
         buffer (make-array Byte/TYPE (util-parse-int size))]
    (loop [g (.read in buffer)
           r 0]
      (if-not (= g -1)
        (do
;;          (println r "/" size)
          (.write out buffer 0 g)
          (recur (.read in buffer) (+ r g)))))
    (.close in)
    (.close out)
    (.disconnect con)
    (apply str (map char buffer))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;; TODO: This is using a static buffer size which is bad. The buffer needs dynamic allocation.
(defn- images
  "Read a binary image file and return it as a ring response."
  [request]
  (if-let [ x (io/resource (subs (str(get request :uri))1))]
    (with-open [in (io/input-stream (io/file x))]
      (let [buf (byte-array 2000)
            n (.read in buf)]
        (println "Read" n "bytes.")
;;        (take-while #(not= 0 %) (seq buf))))))
        (-> (r/response (take n buf)) (r/content-type "image/jpeg") )))   ))

(defn- css
  "Read a text file and return it as a ring response."
  [request]
  (if-let [ x (io/resource (subs (str(get request :uri))1))]
    (r/response  (slurp x))
    ;;  (r/response (with-out-str (println x)))
    (-> (r/response "") (r/content-type "text/html")  (r/status 404))))

(defn- js
  "Read a text file and return it as a ring response."
  [request]
  (if-let [ x (io/resource (subs (str(get request :uri))1))]
    (r/response (slurp x))
    (-> (r/response "") (r/content-type "text/html")  (r/status 404))))





;;;;;;;;; end of functions that transmute the resource files into responses ;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;                         API                                ;;;;;;;;;;;;;;;;;
;;;;;;;;;; These function definitions are the only API documentation. ;;;;;;;;;;;;;;;;;
;;;;;;;;;; Enjoy.                                                     ;;;;;;;;;;;;;;;;;


(defn- wireless-network-activate
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "/etc/init.d/network-manager" "start")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- programming-clojure-2nd-edition
  [request]
  (list "<pre>"
        (let [x (shell/sh "evince" "/home/sy/Azureus Downloads/Clojure/Programming Clojure 2nd Edition/Programming Clojure 2nd Edition.pdf")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- openvpn-tunnel-start
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "openvpn" "--config" "/home/sy/vpn1.config" "/sbin/ifconfig")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- openvpn-tunnel-stop
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "killall" "openvpn")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- wireless-network-deactivate
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "/etc/init.d/network-manager" "stop")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- remove-ath9k-module
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "modprobe" "-v" "-r" "ath9k")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- install-ath9k-module
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "modprobe" "-v" "ath9k" )]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- remove-rtl8187-module
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "modprobe" "-v" "-r" "rtl8187")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- install-rtl8187-module
  [request]
  (list "<pre>"
        (let [x (shell/sh "sudo" "-A" "modprobe" "-v" "rtl8187" )]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- pwd
  [request]
  (list "<pre>"
        (let [x (shell/sh "pwd")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- w
  [request]
  (list "<pre>"
        (let [x (shell/sh "w")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- reload_w
  "Provides an automatic page reloading html formatted output of the system command 'w'."
  [request]
  (make-auto-refresh-html-doc-from-hiccup-file     "/usr/bin/w"
                                                   30
                                                   (w[])))



(defn- iwconfig
  [request]
  ;; (list "<pre>"
  ;;       (let [x (shell/sh "/sbin/iwconfig")]
  ;;         (str (x :out)(x :err)))
  ;;       "</pre>"))
  (list "<pre><h1>"
        (try
          (let [x (shell/sh "/sbin/iwconfig")]
            (str (x :out)(x :err)))
          (catch Exception e (str "caught" e))
          (finally (prn " iwconfig routine announces: finally " ))
          )

        "</pre>"))

(defn- reload_iwconfig
  "Provides an automatic page reloading html formatted output of the system command 'iwconfig'."
  [request]
  (make-auto-refresh-html-doc-from-hiccup-file     "/sbin/iwconfig"
                                                   3
                                                   (iwconfig[])))



(defn- iwconfig-zenity
  [request]
  (list "<pre><h1>"
        (try
          (let [x (shell/sh "/bin/bash" "/home/nsa/bin/ws")]
            (str (x :out)(x :err)))
          (catch Exception e (str "caught" e))
          (finally (prn " iwconfig routine announces: finally " ))
          )

        "</pre>"))

(defn- ifconfig
  [request]
  (list "<pre>"
        (let [x (shell/sh "/sbin/ifconfig")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- netstat
  [request]
  (list "<pre>"
        (let [x (shell/sh "/bin/netstat" "-nr")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- ip-route
  [request]
  (list "<pre>"
        (let [x (shell/sh "/usr/sbin/ip" "route" "show" )]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- iwlist-scan-wlan0
  [request]
  (list "<pre>"
        (let [x (shell/sh "/sbin/iwlist" "wlan0" "scan")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- iwlist-scan-wlan1
  [request]
  (list "<pre>"
        (let [x (shell/sh "/sbin/iwlist" "wlan1" "scan")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- html-viewer
  [request]
  (if-let [x (str(get request :uri))]
    (def html-viewer-process-output (process/spawn "html-viewer" x ))))

;;;;;;;; Notes about def'ing var's ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; good
;def thing 1) ; value of thing is now 1
; do some stuff with thing
;alter-var-root #'thing (constantly nil)) ; value of thing is now nil
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- echo-my-request
  "Simply echos the http request sent to the server."
  [request]
  { :body (with-out-str  (pprint request))})

(defn- four-oh-four
  [id]
  (list ((shell/sh "cat" "src/sc3/404.html"):out)))



(defn- resource-test
  [request]
  (print (io/resource "dhtmlwindow.css")))   

(defn- print-resource-file
  [request]
  (list
   (println (io/resource "css/dhtmlwindow.css"))
   (str "<pre>" (slurp (io/resource "css/dhtmlwindow.css"))"<pre>")))

(defn- simple
  [request]
  (list
   (println (io/resource "html/simple.html"))
   (str (slurp (io/resource "css/dhtmlwindow.css")))))

(defn- print-classpath
  "Provides html formatted output of the java classpath."
  []
  (-> (r/response (seq (map #(str % "<br>") (seq(str/split (java.lang.System/getProperty "java.class.path")#":"))))) (r/content-type "text/html") (r/status 200)))

(defn- visualize-system-environment
  "Provides html formatted output of the system environment."
  [request]
  (list "<pre>" "<h1>"
        (with-out-str (doseq [x(read-system-environment)](println x)))
        "</h1>" "</pre>")) 

(defn- ls_l
  [request]
  (list "<pre>"
        (let [x (shell/sh "/bin/ls" "-l" "/")]
          (str (x :out)(x :err)))
        "</pre>"))

(defn- port2
  [request]
  ("<script>
port22=_create_window('divbox', 'div', 'menu-data', 'port #2', 'width=450px,height=300px,left=300px,top=150px,resize=1,scrolling=1');</script>")
  )

;; (defn- homepage_port1
;;   [request]
;;   (make-html-document-with-hiccup-file "resources/html/page1.hiccup"))

(defn- homepage_port1
  [request]
  (make-html-document-with-hiccup-file (io/resource "html/page1.hiccup")))

(defn- homepage
  [request]
  ;;(homepage_port1[])
  (homepage_port1[])
  ;;  (port2 [])
  ;;  (port2)
  ;;  (port3)
  )


(defn- port3
  [request]
  )

(defn- gen-diff
  "  "
  [request]
  (-> (r/response (str "gen-diff<br>"  (rr/body-string))) (r/content-type "text/html") (r/status 200)))

;;;;;;;;;;;;;;;;;; for accessing binary files, and other static files, e.g. css, js. -- ring/resource-response.

(defn- awkward404
  [& args]
;;  (->
;;   (slurp (object-retrieval {:uri "html/awkward404.html", :method :get}))))
;;   (r/resource-response )))
   (slurp (:body(r/resource-response "/html/awkward404.html"))))

(defn- simple-four-oh-four
  [id]
  {:body "(defroutes app ...) route doesn't exist\n404 error. File not found"})

(defn- object-retrieval
  "Given a map of :uri 'filename', return a ring response map."
  [request]
  (doseq [keyval request] (prn keyval))
  (->
   (subs (str(get request :uri))1)
   (r/resource-response )))

(defn- file-retrieval
  "Given a map of :uri 'filename', return the file."
  [request]
  (doseq [keyval request] (prn keyval))
  (->
   (subs (str(get request :uri))1)
   (slurp (:body(r/file-response {:uri request})))))

(defn page
  "build a html page"
  []
  (css "ip.css"))
;;;;;;;;; end of API ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;;;;;;;;;; The paradigm is to put the html anchors in a resources/html/foo.hiccup file, and
;;;;;;;;;; then put corresponding routes to functions in this compojure/defroutes macro.

;;;;;;;;;; These routes are refered to in the 'html/page1.hiccup' file.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;; (defoutes app...) ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defroutes app
  (GET "/" request (homepage request))
  (GET "/clocss" request (clocss request))
  (GET "/html/*.html" request (object-retrieval request))
  (GET "/css/*.css" request (object-retrieval request))
;;  (GET "/js/*.js" request (js request))
  (GET "/js/*.js" request (object-retrieval request))
  (POST "/echo-my-request" request (echo-my-request request))
  (GET "/print-resource-file" request (print-resource-file request))
;;  (GET "/simple" request (simple request))
  (GET "/print-classpath" request (print-classpath))
  (POST "/gen-diff" request (gen-diff request))
  (GET "/ip-route" request (ip-route request))
  (GET "/resource-test" request (resource-test request))
  (GET "/images/*.jpg" request (object-retrieval request))
  (GET "/images/*.png" request (object-retrieval request))
  (GET "/wireless-network-activate" request (wireless-network-activate request))
  (GET "/wireless-network-deactivate" request (wireless-network-deactivate request))
  (GET "/openvpn-tunnel-start" request (openvpn-tunnel-start request))
  (GET "/openvpn-tunnel-stop" request (openvpn-tunnel-stop request))
  (GET "/iwconfig" request (reload_iwconfig request))
  (GET "/iwconfig-zenity" request (iwconfig-zenity request))
  (GET "/ifconfig" request (ifconfig request))
  (GET "/netstat" request (netstat request))
  (GET "/iwlist-scan-wlan0" request (iwlist-scan-wlan0 request))
  (GET "/iwlist-scan-wlan1" request (iwlist-scan-wlan1 request))
  (GET "/pwd" request (pwd request))
  (GET "/reload_w" request (reload_w request))
  (GET "/demo.htm" request (html-static-docs request))
  (GET "/install-ath9k-module" request (install-ath9k-module request))
  (GET "/remove-ath9k-module" request (remove-ath9k-module request))
  (GET "/install-rtl8187-module" request (install-rtl8187-module request))
  (GET "/remove-rtl8187-module" request (remove-rtl8187-module request))
  (GET "/programming-clojure-2nd-edition" request (programming-clojure-2nd-edition request))
  (GET "/visualize-system-environment" request (visualize-system-environment request))

  ;; these routes are not refered to in the 'html/page1.hiccup' file
  (GET "/ls-l" request (ls_l request))

  (resources "resources")
  ;; route not found
  ;;  (not-found simple-four-oh-four))
  (not-found awkward404))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;; end of (defoutes app...) ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; mutable
(defn- start-server
  "Attempting, in a manner which might not be discernable from feeble, to start the ring server.\n/
  The server can be stopped in the repl with (.stop server)"
  []
  (println "Attempting, in a manner which might not be discernable from feeble, to start the jetty server.\nThe server can be stopped in the repl with (.stop server)")
  (def server (jetty/run-jetty #'app {:port 7777 :join? false})))

(defn -main
  []
  (start-server)
  (println "..the server might be running at localhost:7777..")
  )


;;;;;;;;;;;;;;;;;;;;;;;;;; test vehicles ;;;;;;;;;;;;;;;;;;;;;

(defn- t-hash-key-vals []
  (doseq [keyval (sort(ns-publics 'sc3.app2))] (println (key keyval)))
  )


;; ===========================Code snippet====================================
;; Really, I think Hiccup is the best solution in this space.

;; Prefer logic-less functions to output html strings? Hiccup has this use case covered. This is all stock Clojure[script], not a templating language:

;;     (def foo [:h2 "something cool"])
;;     (hiccup.core/html foo) ;;=> "<h2>something cool</h2>"

;; Plus there's plenty of ways to quickly build up a bootstrap (, etc) page.

;;     (defn bootstrap-page [hiccup-form]
;;      (hiccup.page/html5 
;;       (hiccup.page/include-js "link-to-jquery.js" 
;;                               "link-to-bootstrap.js")
;;       (hiccup.page/include-css "link-to-bootstrap.css")
;;       hiccup-form))

;; BTW, there are some interactive examples at http://hiccup.space


;; sc3.app2> (defn- iwconfig-zenity
;;   []
;;   ;; (list "<pre>"
;;   ;;       (let [x (shell/sh "/sbin/iwconfig")]
;;   ;;         (str (x :out)(x :err)))
;;   ;;       "</pre>"))
;;   (list "<pre><h1>"
;;         (try
;;           (let [x (shell/sh "OUTPUT=\"<span foreground=red>$(/sbin/iwconfig)</span>\";"
;; "echo \"$OUTPUT\"""|""zenity --title=\"/sbin/iwconfig\" --text-info --width=600 --height=400""&")]
;;             (str (x :out)(x :err)))
;;           (catch Exception e (str "caught" e))
;;           (finally (prn " iwconfig routine announces: finally " ))
;;           )

;;         "</pre>"))


