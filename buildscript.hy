#!/usr/local/bin/hy
(import sys)
(import re)
(import itertools)
(import collections)
(import subprocess)

(defn diffract [hex_encoding]
  (list (map
         (fn [sl] (int (sl hex_encoding) 16))
         (map (fn [b] (fn [s] (slice s (nth b 0) (nth b 1))))
              [[0 2] [2 4] [4 6]]))))

(defn undiffract [rgb]
  (.join "" (map (fn [c] (.zfill (slice (hex (int c)) 2) 2)) rgb)))

(defn weighted_average_diffracted [rgb1 rgb2 weighing]
  (list (map (fn [c1 c2]
               (+ c1 (* weighing (- c2 c1))))
             rgb1 rgb2)))

(defn color_stop_interpolate [color_stops x]
  (let [[stops (sorted (.keys color_stops))]
        [closest_above (min (filter (fn [stop] (> (- stop x) 0)) stops))]
        [closest_below (max (filter (fn [stop] (< (- stop x) 0)) stops))]]
    (undiffract
     (apply weighted_average_diffracted
            (itertools.chain (list (map (fn [k] (diffract (get color_stops k)))
                                        [closest_below closest_above]))
                             [(/ (- x closest_below)
                                 (- closest_above closest_below))])))))

(defn stile_block [value color]; (sic)
  (.join "\n"
         [(.format "[data-value=\"{}\"] {{" value)
          "    color: #ffffff;"
          (.format "    background-color: #{};" color)
          "}\n"]))

(defn build_stilesheet ; (sic)
  [color_stops start end]
  (let [[values (range start end)]
        [colors (dict-comp value
                           (.get color_stops value
                                 (color_stop_interpolate
                                  color_stops value))
                           [value values])]]
    (.join "\n"
           (list-comp (stile_block value (get colors value)) [value values]))))

(defn write_stilesheet! ; (sic)
  [color_stops start end]
  (with [[tiles_css (open "static/tiles.css" "w")]]
        (.write tiles_css (+ (stile_block 1 "2020B0") "\n"
                             ; ^ XXX TODO FIXME this is not actually
                             ; the right way to deal with an
                             ; off-by-one error elsewhere
                             (build_stilesheet color_stops start end)))))

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

(when (= __name__ "__main__")
  (do
   (list (map cljx_of_destitution
              ["src/Thirty_Three/foundation.clj"
               "src/Thirty_Three/combinatorics_library.clj"]))
   (write_stilesheet! {1 "2020B0" 8 "B02020" 14 "D0D0D0"} 2 14)
   (let [[subtask (nth sys.argv 1)]]
     (cljsbuild subtask))))
