#!/usr/local/bin/hy
(import sys)
(import re)
(import subprocess)

(defn return_group [match]
  (.group match 1))

(defn cljx_of_destitution [filename]
  (with [[cljf (open filename)]
         [cljsf (open (+ filename "s") "w")]]
        (let [[source (.read cljf)]
              [output (re.sub ";(.*)?; buildscript: cljs\n"
                              return_group
                              source)]]
          (.write cljsf output))))

(defn cljsbuild [subtask]
  (subprocess.call ["lein" "cljsbuild" subtask]))

(if (= __name__ "__main__")
  (do
   (list (map cljx_of_destitution
              ["src/Thirty_Three/foundation.clj"
               "src/Thirty_Three/combinatorics_library.clj"]))
   (let [[subtask (nth sys.argv 1)]]
     (cljsbuild subtask))))
