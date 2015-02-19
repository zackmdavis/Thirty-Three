(ns Thirty-Three.foundation
  (:require [Thirty-Three.combinatorics-library :refer [cartesian-product]]))

(defn clean-n-arena [n arena-size]
  (if (= n 1)
    (vec (for [_ (range arena-size)] nil))
    (let [level-below (clean-n-arena (dec n) arena-size)]
      (vec (for [_ (range arena-size)] level-below)))))

(defn lookup [arena coordinates]
  (get-in arena coordinates))

(defn write [arena coordinates value]
  (assoc-in arena coordinates value))

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

(defn infer-dimensionality [arena]
  (loop [i 0 level arena]
    (if (= (type level) (type []))
      (recur (inc i) (level 0))
      i)))

(defn slicing-coordinates [arena slicing-dimension]
  (let [n (infer-dimensionality arena)
        arena-size (count arena)
        bound-coordinates (apply cartesian-product
                                 (repeat (dec n) (range arena-size)))]
    (map #(vec (insert-into-seq-at :* % slicing-dimension)) bound-coordinates)))

(defn slide-arena [arena sliding-dimension direction]
  (let [line-coordinates (slicing-coordinates arena sliding-dimension)
        lines (map #(lookup-select arena %) line-coordinates)
        slid-lines (map #(slide-line % direction) lines)
        coordinates-and-new (map vector line-coordinates slid-lines)]
    (reduce (fn [arena-state [line-coordinate slid-line]]
              (write-select arena-state line-coordinate slid-line))
            arena
            coordinates-and-new)))

(defn vacancies [arena]
  (let [n (infer-dimensionality arena)]
    (filter #(not (lookup arena %))
            (apply cartesian-product (repeat n (range (count arena)))))))

(defn fill-vacancy [arena]
  (let [positions (vacancies arena)]
    (write arena (rand-nth positions) 1)))

;(.log js/console "Hello ClojureScript World from foundation.clj[s]!") ; buildscript: cljs
