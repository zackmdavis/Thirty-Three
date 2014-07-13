(ns Thirty-Three.core-test
  (:require [clojure.test :refer :all]
            [Thirty-Three.core :refer :all]))

(deftest can-slide-line
  (is (= (slide-line [2 :_ 2 :_] :back) [3 :_ :_ :_]))
  (is (= (slide-line [3 :_ 2 2] :forward) [:_ :_ 3 3])))
