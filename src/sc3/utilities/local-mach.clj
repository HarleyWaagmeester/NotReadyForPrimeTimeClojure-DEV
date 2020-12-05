



(totally-expressive-all-encompasing-function
 []

 (let [joe (global-local-database)]
   (work with joe)
   (work on joe)
   ))




(defn caliente
  [directory]
  (let
      [ directory-objects (fn [dir] (.listFiles (io/file dir))dir) ]
    (letfn
        [
         (filter-dirs 
           [z]
           (conj z (filter some? (map #(if (.isDirectory %)(.getPath %)) directory-objects))))
         
         (filter-files
           [z]
           (conj z (filter some? (map #(if (.isFile %)(.getPath %)) directory-objects))))
         
         (filter-filesize
           [z]
           (conj z (filter some? (map #(if (.isFile %)(.length %)) directory-objects)))) ]

      (filter-dirs[])

    )))



