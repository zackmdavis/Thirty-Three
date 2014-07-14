(ns Thirty-Three.terminal-demo
  (:require [Thirty-Three.core :refer :all]))

(def demo-two-arena-atom (atom (clean-n-arena 2 4)))

(swap! demo-two-arena-atom #(write % [0 0] 1))
(swap! demo-two-arena-atom #(write % [2 3] 1))

(defn display-square [square]
  (if square
    (format "%2d" square)
    "  "))

(defn display-two-arena [arena]
  (doseq [row arena]
    (println (vec (map display-square row))))) 

(def actions
  {:A #(slide-arena % 1 2 :back)
   :D #(slide-arena % 1 2 :forward)
   :W #(slide-arena % 0 2 :back)
   :S #(slide-arena % 0 2 :forward)})

(defn game-loop []
  (let [action (actions (keyword (clojure.string/upper-case (read-line))))]
    (when action
      (swap! demo-two-arena-atom action)
      (swap! demo-two-arena-atom #(fill-vacancy % 2)) 
      (display-two-arena @demo-two-arena-atom))
    (recur)))

(defn -main []
  (game-loop))

