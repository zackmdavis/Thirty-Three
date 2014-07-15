(ns Thirty-Three.terminal-demo
  (:require [Thirty-Three.core :refer :all]))

(def demo-two-arena-atom (atom (clean-n-arena 2 4)))
(swap! demo-two-arena-atom #(write % [0 0] 1))
(swap! demo-two-arena-atom #(write % [2 3] 1))

(def demo-three-arena-atom (atom (clean-n-arena 3 4)))
(swap! demo-three-arena-atom #(write % [0 0 0] 1))
(swap! demo-three-arena-atom #(write % [2 3 1] 1))

(defn display-square [square]
  (if square
    (format "%2d" square)
    "  "))

(defn display-two-arena [arena]
  (doseq [row arena]
    (println (vec (map display-square row))))) 

(defn display-three-arena [arena]
  (doseq [level arena]
    (display-two-arena level)
    (println "_____")))

(def two-actions
  {:A #(slide-arena % 1 2 :back)
   :D #(slide-arena % 1 2 :forward)
   :W #(slide-arena % 0 2 :back)
   :S #(slide-arena % 0 2 :forward)})

(def three-actions
  {:A #(slide-arena % 2 3 :back)
   :D #(slide-arena % 2 3 :forward)
   :W #(slide-arena % 1 3 :back)
   :S #(slide-arena % 1 3 :forward)
   :Q #(slide-arena % 0 3 :back)
   :E #(slide-arena % 0 3 :forward)})

(defn two-game-loop []
  (display-two-arena @demo-two-arena-atom)
  (let [action (two-actions (keyword (clojure.string/upper-case (read-line))))]
    (when action
      (swap! demo-two-arena-atom action)
      (swap! demo-two-arena-atom #(fill-vacancy % 2)))
    (recur)))

(defn three-game-loop []
  (display-three-arena @demo-three-arena-atom)
  (let [action (three-actions (keyword (clojure.string/upper-case (read-line))))]
    (when action
      (swap! demo-three-arena-atom action)
      (swap! demo-three-arena-atom #(fill-vacancy % 3)))
    (recur)))

(defn -main [& args]
  (condp = (first args)
    "2" (two-game-loop)
    "3" (three-game-loop)
    (println "argument must be '2' or '3' indicating game dimensionality")))
