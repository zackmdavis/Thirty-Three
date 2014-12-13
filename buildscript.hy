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

(defn interpolate [rgb1 rgb2 weighing]
  (list (map (fn [c1 c2]
               (+ c1 (* weighing (- c2 c1))))
             rgb1 rgb2)))

(defn interpolate_stop [color_stops x]
  (let [[stops (sorted (.keys color_stops))]
        [closest_above (min (filter (fn [stop] (> (- stop x) 0)) stops))]
        [closest_below (max (filter (fn [stop] (< (- stop x) 0)) stops))]
        [diffracted_above (diffract (.get color_stops closest_above))]
        [diffracted_below (diffract (.get color_stops closest_below))]
        [weighting (/ (- x closest_below) (- closest_above closest_below))]]
    (undiffract (interpolate diffracted_below diffracted_above weighting))))

(defn populate_stops
  [color_stops]
  (let [[values (range (min (.keys color_stops)) (max (.keys color_stops)))]
        [full_stops (.copy color_stops)]]
    (for [value values]
      (if (not-in value color_stops)
        (assoc full_stops value (interpolate_stop color_stops value))))
    full_stops))

(defn stile_block ; (sic)
  [value color]
  (.join "\n"
         [(.format "[data-value=\"{}\"] {{" value)
          "  color: #ffffff;"
          (.format "  background-color: #{};" color)
          "}\n"]))

(defn stilesheet ; (sic)
  [color_stops]
  (let [[colors (populate_stops color_stops)]]
    (.join "\n" (list-comp (stile_block value color)
                           [[value color] (.items colors)]))))

(defn write_stilesheet! ; (sic)
  [color_stops]
  (with [[tiles_css (open "static/tiles.css" "w")]]
        (.write tiles_css (stilesheet color_stops))))

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

(def tile_color_parameters {1 "2020B0"  8 "B02020"
                            14 "F04590" 21 "9070F0" 31 "A0A0A0"})

(when (= __name__ "__main__")
  (if (= (nth sys.argv 1) "--only-stilesheet")
    (write_stilesheet! tile_color_parameters)
    (do
     (list (map cljx_of_destitution
                ["src/Thirty_Three/foundation.clj"
                 "src/Thirty_Three/combinatorics_library.clj"]))
     (write_stilesheet! tile_color_parameters)
     (let [[subtask (nth sys.argv 1)]]
       (cljsbuild subtask)))))
