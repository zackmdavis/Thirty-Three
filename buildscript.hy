#!/usr/local/bin/hy
(import sys)
(import re)
(import subprocess)
(import shutil)

(defn return_group [match]
  (.group match 1))

(if (= __name__ "__main__")
  (do
   ;; improvised cljx: copy foundation.clj with .cljs extension but
   ;; uncommenting commented lines that end in "; buildscript: cljs"
   (with [[fdn_clj (open "src/Thirty_Three/foundation.clj")]
          [fdn_cljs (open "src/Thirty_Three/foundation.cljs" "w")]]
         (let [[source (.read fdn_clj)]
               [output (re.sub ";(.*)?; buildscript: cljs\n"
                              return_group
                              source)]]
           (.write fdn-cljs output)))
   ;; also the combinatorics library (doesn't actually need changes
   ;; but TODO could be included in previous context manager for
   ;; elegance
   (shutil.copy "src/Thirty_Three/combinatorics_library.clj"
                "src/Thirty_Three/combinatorics_library.cljs")

   (let [[cljsbuild_option (nth sys.argv 1)]]
     (try
      (print (apply subprocess.check_output
                    [["lein" "cljsbuild" cljsbuild_option]]
                    {"stderr" subprocess.STDOUT}))
      (catch [e subprocess.CalledProcessError]
        (print "ERROR" (or e.output e)))))))
