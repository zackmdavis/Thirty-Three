#!/usr/local/bin/hy
(import sys)
(import re)
(import subprocess)

(defn diffract [hex_encoding]
  (list (map
         (fn [sl] (int (sl hex_encoding) 16))
         (map (fn [b] (fn [s] (slice s (nth b 0) (nth b 1))))
              [[0 2] [2 4] [4 6]]))))

(defn undiffract [rgb]
  (.join "" (map (fn [c] (.zfill (slice (hex (int c)) 2) 2)) rgb)))

(defn average_diffracted [rgb1 rgb2]
  (list (map (fn [c1 c2] (/ (+ c1 c2) 2)) rgb1 rgb2)))

(defn color_stop_interpolate [color_stops x]
  (let [[stops (sorted (.keys color_stops))]
        [closest_above (min (filter (fn [stop] (> (- stop x) 0)) stops))]
        [closest_below (max (filter (fn [stop] (< (- stop x) 0)) stops))]]
    (undiffract
     (apply average_diffracted
            (map (fn [k] (diffract (get color_stops k))) [closest_above closest_below])))))

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
