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


