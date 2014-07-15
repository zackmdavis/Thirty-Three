#!/usr/local/bin/hy
(import sys)
(import subprocess)
(import shutil)

(if (= __name__ "__main__")
  (do
   (shutil.copy "src/Thirty_Three/core.clj" "src/Thirty_Three/core.cljs")
   (let [[cljsbuild_option (nth sys.argv 1)]]
     (try
      (subprocess.check_output ["lein" "cljsbuild" cljsbuild_option])
      (catch [subprocess.CalledProcessError e]
        (print "ERROR" e))))))
