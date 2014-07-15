#!/usr/local/bin/hy
(import sys)
(import subprocess)
(import shutil)

(if (= __name__ "__main__")
  (do
   (shutil.copy "src/Thirty_Three/core.clj" "src/Thirty_Three/core.cljs")
   (shutil.copy "src/Thirty_Three/combinatorics_library.clj"
                "src/Thirty_Three/combinatorics_library.cljs")
   (let [[cljsbuild_option (nth sys.argv 1)]]
     (try
      (print (apply subprocess.check_output
                    [["lein" "cljsbuild" cljsbuild_option]]
                    {"stderr" subprocess.STDOUT}))
      (catch [e subprocess.CalledProcessError]
        (print "ERROR" e))))))
