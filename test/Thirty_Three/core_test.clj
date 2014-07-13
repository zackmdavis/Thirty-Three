(ns Thirty-Three.core-test
  (:require [clojure.test :refer :all]
            [Thirty-Three.core :refer :all]))

(deftest can-slide-line
  (is (= (slide-line [2 nil 2 nil] :back) [3 nil nil nil]))
  (is (= (slide-line [3 nil 2 2] :forward) [nil nil 3 3])))

(deftest can-write
  (is (= (write (clean-n-arena 2 2) [1 1] :a)
         [[nil nil] [nil :a]]))
  (is (= (lookup (write (clean-n-arena 3 4) [1 2 3] :x)
                 [1 2 3])
         :x)))
