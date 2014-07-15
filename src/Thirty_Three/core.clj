(ns Thirty-Three.core
  (:require [clojure.math.combinatorics :refer [cartesian-product]]))

(defmacro clean-n-arena [n arena-size]
  "Create an n-dimensional vector-of-vectors-of-&c. with side length
   arena-size"
  (if (= n 0)
    nil
    `(vec (for [_# (range ~arena-size)]
            (clean-n-arena ~(dec n) ~arena-size)))))

(def one-arena (clean-n-arena 1 4))
(def two-arena (clean-n-arena 2 4))
(def the-arena (clean-n-arena 3 4))

(defn lookup [arena coordinates]
  (reduce (fn [arena-slice coordinate] (arena-slice coordinate))
          arena coordinates))

;; XXX retarded
(defn one-write [arena [x] value]
  (assoc arena x value))
(defn two-write [arena [x y] value]
  (assoc arena x (assoc (arena x) y value)))
(defn three-write [arena [x y z] value]
  (assoc arena x (assoc (arena x) y (assoc ((arena x) y) z value))))

;; TODO: generalize
(defn write [arena coordinates value]
  (condp = (count coordinates)
    1 (one-write arena coordinates value)
    2 (two-write arena coordinates value)
    3 (three-write arena coordinates value)))

(defn interpret-wildcard-coordinates [coordinates arena-size]
  (let [wild-indices (filter identity (map-indexed (fn [i c] (if (#{:*} c) i))
                                                   coordinates))
        substitutions (apply cartesian-product
                            (repeat (count wild-indices) (range arena-size)))]
    (map (fn [subs] (apply assoc coordinates (interleave wild-indices subs)))
         substitutions)))

(defn lookup-select [arena coordinates]
  (map #(lookup arena %)
       (interpret-wildcard-coordinates coordinates (count arena))))

(defn write-select [arena coordinates values]
  (let [locations (interpret-wildcard-coordinates coordinates (count arena))]
    (reduce (fn [arena-state [location value]]
              (write arena-state location value))
            arena
            (map vector locations values))))

(defn squash [done todo]
  (if (empty? todo)
    done
    (if (= (last done) (first todo))
      (recur (assoc done (dec (count done)) (inc (first todo)))
             (rest todo))
      (recur (conj done (first todo)) (rest todo)))))

(defn accumulate [blocks]
  (reduce (fn [done incoming]
            (if (= (last done) incoming)
              (assoc done (dec (count done)) (inc incoming))
              (conj done incoming)))
          []
          blocks))

(defn nullspace [blocks arena-size side]
  (let [space (repeat (- arena-size (count blocks)) nil)]
    (condp = side
      :start (concat space blocks)
      :end (concat blocks space)))) 

(defn slide-line [line direction]
  (let [blocks (filter identity line)]
    (condp = direction
      :forward (nullspace (accumulate blocks) (count line) :start)
      :back (nullspace (reverse (accumulate (reverse blocks))) (count line) :end))))
  
(defn insert-into-seq-at [insertion sequence index]
  (let [[start finish] (split-at index sequence)]
    (concat start [insertion] finish)))

(defn slicing-coordinates [slicing-dimension n arena-size]
  (let [bound-coordinates (apply cartesian-product 
                                 (repeat (dec n) (range arena-size)))]
    (map #(vec (insert-into-seq-at :* % slicing-dimension)) bound-coordinates)))

(defn slide-arena [arena sliding-dimension n direction]
  (let [line-coordinates (slicing-coordinates sliding-dimension n (count arena))
        lines (map #(lookup-select arena %) line-coordinates)
        slid-lines (map #(slide-line % direction) lines)
        coordinates-and-new (map vector line-coordinates slid-lines)]
    (reduce (fn [arena-state [line-coordinate slid-line]]
              (write-select arena-state line-coordinate slid-line))
            arena
            coordinates-and-new)))

(defn vacancies [arena n]
 (filter #(not (lookup arena %))
         (apply cartesian-product (repeat n (range (count arena))))))
  
(defn fill-vacancy [arena n]
  (let [positions (vacancies arena n)]
    (write arena (rand-nth positions) 1)))
