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

(if (= __name__ "__main__")
  (do
   (cljx_of_destitution "src/Thirty_Three/foundation.clj")
   (cljx_of_destitution "src/Thirty_Three/combinatorics_library.clj")
   (let [[cljsbuild_option (nth sys.argv 1)]]
     (try
      (print (apply subprocess.check_output
                    [["lein" "cljsbuild" cljsbuild_option]]
                    {"stderr" subprocess.STDOUT}))
      (catch [e subprocess.CalledProcessError]
        (print "ERROR" (or e.output e)))))))
