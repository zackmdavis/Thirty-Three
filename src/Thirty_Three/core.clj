(ns Thirty-Three.core)

(def arena-size 4)

(defmacro clean-n-arena [n]
  (if (= n 0)
    :_
    `(vec (for [_# (range arena-size)]
            (clean-n-arena ~(dec n))))))

(def one-arena (clean-n-arena 1))
(def two-arena (clean-n-arena 2))
(def the-arena (clean-n-arena 3))

