(ns sc3.utilities.utilitiesv2
  (:require [clojure.java.io :as io])
  (:require (hiccup
             [core :refer :all]
             [form :refer :all]
             [util :refer :all]
             [element :refer :all]
             [page :refer :all]))
  (:require [clojure.java.classpath :as cp])
  (:require [clojure.string :as str])
  (:require [clojure.java.shell :refer [sh] :as shell])
  ;;(:require 
  ;; [dommy.core :as dommy])
  )

(defn help
  "Print a description of useful functions in this namespace."
  []
  (println "Utilities.clj help :::")
  (println "util-parse-int [string] ::\n\tConvert a string to an integer.")
  (println "util-ns-regex-search [string] ::\n\tPrint any namespaces that contain the given tag pattern. i.e. #\"(abc)\".")
  (println "util-ns-publics [string] ::\n\tPrint the public symbols for the given namespace.")
  (println "--warning WIP-- util-find-objects-in-directory [{:directory <string> :re-tag<#\"regex-pattern\">...}] :: \n\t Find objects in a directory matching a search pattern."))

(defn classpath
  []
  (cp/classpath))


(defn view 
  "Expand the sequence returned by the given function, and wrap println around it."
  [f]
  (doseq [x f ] (println x)))

(defn util-parse-int
  "Convert a string to an integer."
  [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))

(defn util-ns-regex-search
  "Print any namespaces that contain the given tag pattern."
  [pattern]
  (doseq [x (all-ns)] (if (re-find pattern (str x)) (println x))))

(defn util-ns-publics-sorted
  "Print the sorted public symbols for the given namespace."
  [ns]
  (map #(take 1 %) (sort(ns-publics ns))))

(defn split-file-on-newline
  [filename]
;;  (filter string?
          (for [s (str/split (slurp filename) #"\n")]s))

(defn split-coll-on-newline
  [coll]
;;  (filter string?
          (for [s (str/split (str coll) #"\n")]s))

(defn split-file-on-close-paren
  [filename]
;;  (filter string?
  (for [s (str/split (slurp filename)  #"\)")]s))

(defn split-coll-on-close-paren
  [coll]
;;  (filter string?
          (for [s (str/split coll  #"\n")]s))


(defn test001
  []
  (let [s  (slurp "src/sc3/utilities/test.clj")]
    (loop [open 0 index 0] 
      (if (compare (nth s index) \()
        (println (inc open)))
        (recur (inc open) (inc index)))))

(defn disect001
  " Disects a clojure source file into a collection of beginning and ending positions of the forms. "
  ;; start position, stack atom as integer accumulator, collection as vector accumulator, fstring as slurped file, stop as flag
  [start stack collection fstring stop]
  (println (str "==========\n" "start=" start))
  (view collection)
  (println (type start))
  (reset! stack 0)
  (if (= stop 0)
    (do
      (doseq [i (range start (count fstring))]
        (do
          (if (= 0 (compare (nth fstring i) \())
            (do
              (swap! stack inc)
              (println (str "inc stack=" @stack)))) 
        (if (= 0 (compare (nth fstring i) \)))
            (do
              (swap! stack dec)
              (println (str "dec stack=" @stack))))
        (if (= @stack 0)
          (do
            (println i)
            ;;            (if (= i (count s))
            (disect001 (inc i) stack (conj collection [start i]) fstring (if (= i (count fstring)) (inc stop))))))
        )))collection)

(defn disect002
  " Disects a clojure source file into a collection of beginning and ending positions of the forms. "
  ;; start position, stack accumulator, collection as vector accumulator, fstring as slurped file
  [start stack collection fstring depth]
  (println (str "==========\n" "start=" start))
;;  (view collection)
  (println "depth=" depth)
  (doseq [i (range start (count fstring))]
    (do
      (if (= 0 (compare (nth fstring i) \())
        (do
          (println (str "inc'd stack=" (inc stack)))
          (disect002 (inc i) (inc stack) (conj collection [start i]) fstring (inc depth))))
      
      (if (= 0 (compare (nth fstring i) \)))
        (do
          (println (str "dec'd stack=" (dec stack)))
          (disect002 (inc i) (dec stack) (conj collection [start i]) fstring (dec depth))))
      (if (= stack 0)
        collection))))

(defn comp1
  [c]
  (let [r 0]
    (if (= 0 (compare c \())
    (inc r)
  0)))

(defn comp2
  [c]
  (let [r 0]
    (if (= 0 (compare c \)))
    (inc r)
  0)))



(defn disect003
  " Disects a clojure source file into a collection of beginning and ending positions of the forms. "
  ;; start position, stack accumulator, collection as vector accumulator, fstring as slurped file
  [file]
;;  (view collection)
  (loop [ start 0 i 0  stack 0 collection [] fstring (slurp file)]
    (view collection)
    (recur (inc i) (inc i) (+ stack (comp1 (nth fstring i)) (if (= 0 (comp2 (nth fstring i))) (conj collection [start i])) fstring))))

(defn disect004
  [file]
  (let fstring (slurp file)
       (loop [x 1]
         (println "x= " x)
         (cond
           (> x 10) (println "ending at " x )
           (even? x) (recur (* 2 x))
           :else (recur (+ x 1))
           
           
           ))))



    ;; (do
    ;;   (if (= 0 (compare (nth fstring i) \())
    ;;     (do
    ;;       (println (str "inc'd stack=" (inc stack)))
    ;;       (disect002 (inc i) (inc stack) (conj collection [start i]) fstring (inc depth))))
      
    ;;   (if (= 0 (compare (nth fstring i) \)))
    ;;     (do
    ;;       (println (str "dec'd stack=" (dec stack)))
    ;;       (disect002 (inc i) (dec stack) (conj collection [start i]) fstring (dec depth))))
    ;;   (if (= stack 0)
    ;;     collection))))
                   
  

;; (defn get-requires
;;   "Look through a file for :require forms."
;;   [filename]
;;   (filter string?
;;           (for [s (str/split (str (split-file-on-newline filename)) #"\)")]
;;             (if-not (re-find (re-pattern "^\\s*;")s)
;;               (if (re-find (re-pattern ":require") s)s)))))

(defn get-requires
  "Look through a file for :require forms."
  [filename]
;;  (filter string?
           (for [s (for [x (split-coll-on-close-paren (split-file-on-newline "src/sc3/utilities/test.clj"))]x)]
            (if-not (re-find (re-pattern "^\\s*;")s)
              (if (re-find (re-pattern ":require") s)s))))

(defn get-require-filenames-single-vector
            [filename]
            (filter string?
                    (for [s (get-requires filename)]
                      (if (re-find (re-pattern "^\\s*\\(\\s*:require\\s*\\[")s)
                        (let [start (str/index-of s "[") end (str/index-of s " " start)]
                          (subs s ( inc start) end))))))

(defn get-require-filenames-multiple-vector
            [filename]
            (filter string?
                    (for [s (get-requires filename)]
                      (if (re-find (re-pattern "^\\s*\\(\\s*:require\\s*\\(\\s*[a-z A-Z]\\s*\\n")s)
                        (let [start (str/index-of s "\\s*\\(") end (str/index-of s "\\n" start)]
                          (subs s ( inc start) end))))))

(defn pwd
  []
  (shell/sh "pwd"))  


;; (defn get-requires
;;   "Look through a file for :require forms."
;;   [filename]
;;   (filter string?
;;           (for [s (str/split (slurp "src/sc3/app2.clj") #"\n")]
;;             (if-not (re-find (re-pattern "^\\s*;")s)
;;               (if (re-find (re-pattern ":require") s)s)))))

;; (defn util-find-files-in-directory
;;   "Find the files in a directory matching the given re-tag. :: (util-find-files-in-directory directory re-tag)"
;;   [directory re-tag]
;;   (let [ files (filter some? (for [x(.list (io/file directory))]  (if (re-matches re-tag x) x)))]
;;     (for [x files] (if (.isDirectory x)x))))

(defn util-find-objects-in-directory
  "Find objects in a directory.
  usage:
  (util-find-objects-in-directory
  {:directory <string>
  :re-tag <#\"regex-pattern\">
  :list-directories <boolean>
  :list-files <boolean>
  :headers <boolean>})"
  [m]
  (let [ directory (m :directory) objects (for [x(.listFiles (io/file directory))] x)]
    (when (m :list-directories)
      (if (m :headers) (println "Directories::"))
      (for [y(filter some? (for [x objects] (if (.isDirectory x)(.getPath x))))]y))
    (when (m :list-files)
      (if (m :headers) (println "Files::"))
      (for [y(filter some? (for [x objects] (if (.isFile x)(.getPath x))))] y))))


;; This paradigm utilizes a global value that is operated on, and the result of the operations is obtained by threadin' some functions with '->'.
;; No wildlife is harmed in this process... However... this *is* an evolving process....

;; (->
;;  (get-directory-objects-into-global-directory-objects-var ".")
;;  (filter-dirs)
;;  (filter-files))



;; create data store
(def global-directory-objects (atom nil))

(defn get-directory-objects-into-global-directory-objects-var
  "Reads directory entries into the 'global-directory-objects' variable.
  Returns nil.
  (get-directory-objects-into-global-directory-objects-var 'directory')."
  [directory]
  (reset! global-directory-objects (.listFiles (io/file directory))))

(defn get-directory-objects
  "Reads directory entries."
  [directory]
  (.listFiles (io/file directory)))

(defn filter-dirs 
  [directory]
  (filter some? (map #(if (.isDirectory %)(.getPath %)) (get-directory-objects directory))))

(defn filter-files
  [directory]
  (filter some? (map #(if (.isFile %)(.getPath %)) (get-directory-objects directory))))

(defn filter-filesize
  [directory]
  (filter some? (map #(if (.isFile %)(.length %)) (get-directory-objects directory))))

;; (defn filter-dirs 
;;   [z]
;;   (conj (filter some? (map #(if (.isDirectory %)(.getPath %)) global-directory-objects))z))

;; (defn filter-files
;;   [z]
;;   (into () (conj (filter some? (map #(if (.isFile %)(.getPath %)) global-directory-objects))z)))

;; (defn filter-filesize
;;   [z]
;;   (conj (filter some? (map #(if (.isFile %)(.length %)) global-directory-objects))z))

;;;;;;;;;;;;;;;;; using the above 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;(defn create-display-page [[p]  [q] &[]]
;; (spit "/home/sy/out.html"

;; [title & body] 
;; (html 
;;  (doctype :html4) 
;;  [:html 
;;   [:head 
;;    [:title title]
;;    [:meta {:http-equiv "refresh" :content "30"}]]
;;   [:body 
;;    [:div 
;;     [:h2 
;;      ;; Pass a map as the first argument to be set as attributes of the element
;;      [:a {:href "/"} "kseti"]]]
;;    body
;;    ]])))

;;        ([:html (with-out-str(print(map (fn [a b](format "%-25s %-20s bytes<br>\n" a b))p q)))]

;; temporary settings
(def css-path "/home/sy/GIT/CLOJURE-PROJECTS/sc3/src/sc3/utilities/")
(def js-path "/home/sy/GIT/CLOJURE-PROJECTS/sc3/src/sc3/utilities/")
;;(get-directory-objects-into-global-directory-objects-var "/home/sy/GIT/CLOJURE-PROJECTS/sc3/src/sc3")

;;;; Oh my this is so ancient. Modern browsers use a css solution for alternating row colors via something like this:
;;;;                                       .row:nth-child(even) {
;;;;                                           background: #e0e0e0;}
;;;;
;;;;                                       .row:nth-child(odd) {
;;;;                                           background: #bebebe;}
;;;;


;;(defn click-handler [e]
;;    (.log js/console "You clicked my button! Congratulations"))

;;(dommy/listen! (sel1 :#names-container) :click click-handler)

(defn html-formatted-output
  []
  (spit "/home/sy/out.html"
        (html [:html
               [:head
                [:title "v1 Viso-Mag TronoLogic Dev Manipulator"]
                (include-css (str css-path "/out-html.css"))]
               [:body
                [:div { :class "page-container"}
                                 
                 (let [color? (atom 0) row_num (atom 0)]
                   [:div {:id "names_container" :class "names-container"}
                    (with-out-str
                      (doseq
                          [x (filter-files)]
                        (case @color?
                          0 (do 
                              (swap! row_num inc)
                              (str(print (format "<div class='names-div-color-1'; id='names_container-%d';>" @row_num))
                              (print(format "%-25s</div>" x)))
                          (reset! color? 1))
                          1 (do 
                              (swap! row_num inc)
                              (str(print (format "<div class='names-div-color-2'; id='names_container-%d';>" @row_num))
                                  (print(format "%-25s</div>" x)))
                              (reset! color? 0)))))])

                 [:div {:class "size-container"}
                  (let [color? (atom 0) row_num (atom 0)]
                    [:div {:class "size"}
                     (with-out-str
                       (doseq
                           [x (filter-filesize)]
                         (case @color?
                           0 (do 
                               (swap! row_num inc)
                               (str(print (format "<div class='size-div-color-1' id='size_container-size-%d'>" @row_num))
                                   (print(format "%-25s</div>" x)))
                               (reset! color? 1))
                           1 (do 
                               (swap! row_num inc)
                               (str(print (format "<div class='size-div-color-2'; id='size_container-size-%d';>" @row_num))
                                   (print(format "%-25s</div>" x)))
                               (reset! color? 0)))))])

                  (let [color? (atom 0) row_num (atom 0)]
                    [:div {:class "unit"}
                     (with-out-str
                       (doseq
                           [x (filter-filesize)]
                         (case @color?
                           0 (do 
                               (swap! row_num inc)
                               (str(print (format "<div class='unit-div-color-1'; id='size_container-unit-%d';>" @row_num))
                                   (print(format "%-25s</div>" "&nbsp;&nbsp;bytes")))
                               (reset! color? 1))
                           1 (do 
                               (swap! row_num inc)
                               (str(print (format "<div class='size-div-color-2'; id='size_container-unit-%d';>" @row_num))
                                   (print(format "%-25s</div>" "&nbsp;&nbsp;bytes")))
                               (reset! color? 0)))))])
                  ]
                 [:div {:class "inline-box"}
                  [:div {:class "text_filename_box" :id "text_filename_lock_box"}"filename"]
                  [:div {:class "text_filename_box" :id "text_filename_visual_scanner_box"}"filename"]
                  [:div {:class "clearer"}]
                  [:div {:class "filename_content_display_box" :id "text_filename_content_display"}]]
                 
                 ];; page-container, vast and enveloping div that encloses all the other div's in the neighborhood
                ];; body
               ];; html, hiccup notation
              (include-js (str js-path "/out-html.js"))

              );; html, hiccup conversion function
        );; spit, create a file
  
  );; defn


;;;; Syntho-medicato-trans


;; (defn html-formatted-output
;;   []
;;   (spit "/home/sy/out.html"
;;         (html [:html
;;                [:head
;;                 [:title "Amalgamated Mixer Machine"]
;;                 (include-css (str css-path "/out-html.css"))]
;;                [:body
;;                 [:div {:id "names-container"}
;;                  (with-out-str
;;                    (print
;;                     (map
;;                      (fn 
;;                        [a]
;;                        (print "<span id=name-span-color-1>")
;;                        (format "&nbsp;&nbsp;%-25s&nbsp;&nbsp;</span><br>\n" a)) (filter-files[])) ))]
;;                 [:div {:id "size-container"}
;;                  [:div {:id "size"}
;;                   (with-out-str(print(map (fn [a] (format "<span id=size-span-color-1>%s</span><br>\n" a)) (filter-filesize[]) )))]
;;                  [:div {:id "unit"}
;;                   (with-out-str(print(map (fn [a] (format "<span id=unit-span-color-1>bytes</span><br>\n")) (filter-filesize[]) )))]
;;                  ]]])))

(defn print-create-display-page [[p]  [q] &[]]
  (with-out-str(print(map (fn [a b](format "%-25s %-20s bytes\n" a b))p q))))

(defn html-page [[p]  [q] &[]]
  (spit "/home/sy/out.html"  (map (fn [a b](format "%-25s %-20s bytes\n" a b))p q)))

(defn write-file
  [ofile data]
  (with-open [w (clojure.java.io/writer ofile :append true)]
    (doall (map #(.write w (str %)) data))))

(defn read-file
  [ifile]
  (with-open [r (clojure.java.io/reader ifile)]
    (doall(map prn (line-seq r)))))

(defn read-file2 [filename]
  (with-open [r (java.io.PushbackReader.
                 (clojure.java.io/reader filename))]
    (let [eof (gensym)]
      (doall(take-while #(not= % eof) (repeatedly #(.read r false eof))) ))))

;; :example:
;; (get-directory-objects-into-global-directory-objects-var ".")
;; (write-file "aaa" (create-display-page (filter-files[]) (filter-filesize[])))
;; :end:


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; This paradigm passes a map with a data key/value and an accumulator  key/value through the '->>' threading macro and accumulates a result in the
;;;; accumulator value.

;;Given a map of {:objects xxx :acc xxx},
;;and a function that will operate on {:objects},
;;this will accumulate the results of threaded '->>' operations into :acc.


(defn operate-on-map-with-fn
  [fn m]
  {:objects (m :objects)
   :acc (conj (fn m) (m :acc) )})


(defn get-directory-objects-into-map
  [directory]
  {:objects (for [x(.listFiles (io/file directory))] x)
   :acc '()})


(defn filter-directories
  [m]
  (for [y(filter some? (for [x (m :objects)] (if (.isDirectory x)(.getPath x))))]y))

(defn filter-files--
  [m]
  (for [y(filter some? (for [x (m :objects)] (if (.isFile x)(.getPath x))))]y))

;;;; End of paradigm. ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;; These functions are for formatting text to be used in html div's or whateva' ;;;;;;;

;;;; Find the longest element in a sequence. Used with the global-directory-objects variable and it's functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn position-of-longest-text-item-in-sequence
  []
  (let [a (atom 0) b (atom 0) c (atom 0)]
    (doseq
        [x (filter-files[])]
      (swap! b inc)
      (when (> (count x) @a)
        (reset! a (count x))
        (reset! c @b))
      )(dec @c))) ;;;; Returns a zero based index or -1 if the sequence had nothing that the (count ...) function could count.











;;
;; (defn operate-on-map-with-fn
;;   [f omaplist]
;;   {:olist (omaplist :olist)
;;    :acc (conj (f omaplist) (omaplist :acc) )})
;;   :acc (conj (omaplist :acc) '('abcdef))}) 


;; (defn opaths
;;   [olist]
;;   {:objects olist
;;    :acc '()})

;; (defn dpaths
;;   [omaplist]
;;   {:olist (omaplist :olist)
;;    :acc (conj (omaplist :acc) 
;;          (for [y(filter some? (for [x (omaplist :olist)] (if (.isDirectory x)(.getPath x))))]y))})

;; (defn fpaths
;;   [omaplist]
;;   {:olist (omaplist :olist)
;;    :acc (conj (omaplist :acc) 
;;          (for [y(filter some? (for [x (omaplist :olist)] (if (.isFile x)(.getPath x))))]y))})


;; Working with Files and Directories in Clojure

;; This cookbook covers working with files and directories from Clojure, using functions in the clojure.java.io namespace as well as parts of the JDKn   via interoperability.

;; This work is licensed under a Creative Commons Attribution 3.0 Unported License (including images & stylesheets). The source is available on Github.
;; Preliminaries

;; Note that for the examples below, "io" is an alias for clojure.java.io. That is, it's assumed your ns macro contains:

;; (:require [clojure.java.io :as io])

;; or else in the repl you've loaded it:

;; (require '[clojure.java.io :as io])

;; Recipes
;; Read a file into one long string

;; (def a-long-string (slurp "foo.txt"))

;; Note, you can pass urls to slurp as well. See also slurp at Clojuredocs.
;; Read a file one line at a time

;; Suppose you'd like to call my-func on every line in a file, and return the resulting sequence:

;; (with-open [rdr (io/reader "foo.txt")]
;;   (doall (map my-func (line-seq rdr))))

;; The doall is needed because the map call is lazy. The lines that line-seq gives you have no trailing newlines (and empty lines in the file will yield empty strings ("")).
;; Write a long string out to a new file

;; (spit "foo.txt"
;;       "A long
;; multi-line string.
;; Bye.")

;; Overwrites the file if it already exists. To append, use

;; (spit "foo.txt" "file content" :append true)

;; Write a file one line at a time

;; Suppose you'd like to write out every item in a vector, one item per line:

;; (with-open [wrtr (io/writer "foo.txt")]
;;   (doseq [i my-vec]
;;     (.write wrtr (str i "\n"))))

;; Check if a file exists

;; (.exists (io/file "filename.txt"))

;; Is it a directory? :

;; (.isDirectory (io/file "path/to/something"))

;; An io/file is a java.io.File object (a file or a directory). You can call a number of functions on it, including:

;; exists        Does the file exist?
;; isDirectory   Is the File object a directory?
;; getName       The basename of the file.
;; getParent     The dirname of the file.
;; getPath       Filename with directory.
;; mkdir         Create this directory on disk.

;; To read about more available methods, `see the java.io.File docs.
;; Get a list of the files and dirs in a given directory

;; As File objects:

;; (.listFiles (io/file "path/to/some-dir"))

;; Same, but just the names (strings), not File objects:

;; (.list (io/file "path/to/some-dir"))

;; The results of those calls are seqable.
;; See also

;;     https://github.com/Raynes/fs
;;     the I/O section of the cheatsheet



;; ternery

;;(def foo (if (not (nil? bar)) bar baz))
