(ns Thirty-Three.core)

(defmacro clean-n-arena [n arena-size]
  "Create an n-dimensional vector-of-vectors-of-&c. with side length
   arena-size"
  (if (= n 0)
    :_
    `(vec (for [_# (range ~arena-size)]
            (clean-n-arena ~(dec n) ~arena-size)))))

(def one-arena (clean-n-arena 1 4))
(def two-arena (clean-n-arena 2 4))
(def the-arena (clean-n-arena 3 4))

(defn lookup [arena & coordinates]
  (reduce (fn [arena-slice coordinate] (arena-slice coordinate))
          arena coordinates))

(defn squash [done todo]
  (if (<= (count todo) 1)
    (concat done todo)
    (let [[this-collision still-todo] (map vec (split-at 2 todo))
          collision-outcome (if (= (first this-collision)
                                   (second this-collision))
                              [(inc (first this-collision))]
                              this-collision)]
      (squash (concat done collision-outcome) still-todo))))

(defn slide-line [line direction]
  (let [blocks (filter #(not= % :_) line)
        squashed (condp = direction
                   :back (squash [] blocks)
                   :forward (squash [] (reverse blocks)))
        size (count line)
        padding (repeat (- size (count squashed)) :_)]
    (condp = direction
      :back (vec (concat squashed padding))
      :forward (vec (concat padding squashed)))))

