(ns Thirty-Three.foundation-test
  (:require [clojure.test :refer :all]
            [Thirty-Three.foundation :refer :all]))

(def marked-two-arena
  [[:a :b :c] [:d :e :f] [:g :h :i]])

(def realist-two-arena-a
  [[nil nil 1 nil] [nil nil 1 nil] [nil nil nil nil] [2 nil 1 1]])

(deftest can-make-clean-n-arena
  (is (= (clean-n-arena 2 2)
         [[nil nil] [nil nil]]))
  (is (= (clean-n-arena 3 2)
         [[[nil nil] [nil nil]] [[nil nil] [nil nil]]])))

(deftest can-write
  (is (= (write (clean-n-arena 2 2) [1 1] :a)
         [[nil nil] [nil :a]]))
  (is (= (lookup (write (clean-n-arena 3 4) [1 2 3] :x)
                 [1 2 3])
         :x)))

(deftest can-interpret-wildcard-coordinates
  (is (= (interpret-wildcard-coordinates [0 :* 2 :*] 2)
         [[0 0 2 0] [0 0 2 1] [0 1 2 0] [0 1 2 1]])))

(deftest can-lookup-select
  (is (= (lookup-select marked-two-arena [:* 0])
         [:a :d :g])))

(deftest can-write-select
  (is (= (write-select marked-two-arena [:* 0] [:x :y :z])
         [[:x :b :c] [:y :e :f] [:z :h :i]])))

(deftest can-insert-into-seq-at
  (is (= (insert-into-seq-at :x (range 5) 2)
         [0 1 :x 2 3 4])))

(deftest can-infer-dimensionality
  (are [arena n] (= (infer-dimensionality arena) n)
       (clean-n-arena 1 4) 1
       (clean-n-arena 2 4) 2
       (clean-n-arena 3 4) 3))

(deftest can-compute-slicing-coordinates
  (is (= (slicing-coordinates (clean-n-arena 2 4) 1)
         [[0 :*] [1 :*] [2 :*] [3 :*]]))
  (is (= (slicing-coordinates (clean-n-arena 3 3) 0)
         [[:* 0 0] [:* 0 1] [:* 0 2]
          [:* 1 0] [:* 1 1] [:* 1 2]
          [:* 2 0] [:* 2 1] [:* 2 2]])))

(deftest can-report-vacancies
  (is (= [[0 0] [0 1] [0 3] [1 0] [1 1] [1 3] [2 0] [2 1] [2 2] [2 3] [3 1]]
         (vacancies realist-two-arena-a))))
