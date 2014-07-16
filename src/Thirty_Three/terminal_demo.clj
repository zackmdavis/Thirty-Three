(ns Thirty-Three.terminal-demo
  (:require [Thirty-Three.foundation :refer :all]
            [Thirty-Three.macro-library :refer :all])
  (:import jline.Terminal))

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
  {:A #(slide-arena % 1 :back)
   :D #(slide-arena % 1 :forward)
   :W #(slide-arena % 0 :back)
   :S #(slide-arena % 0 :forward)})

(def three-actions
  {:A #(slide-arena % 2 :back)
   :D #(slide-arena % 2 :forward)
   :W #(slide-arena % 1 :back)
   :S #(slide-arena % 1 :forward)
   :Q #(slide-arena % 0 :back)
   :E #(slide-arena % 0 :forward)})

(defn game-loop [arena-atom actions display]
  (display @arena-atom)
  (let [terminal (Terminal/getTerminal)
        action (actions (keyword (clojure.string/upper-case
                                  (char (.readCharacter terminal System/in)))))]
    (when action
      (swap! arena-atom action)
      (swap! arena-atom fill-vacancy))
    (recur arena-atom actions display)))

(defn -main [& args]
  (condp = (first args)
    "2" (game-loop demo-two-arena-atom two-actions display-two-arena)
    "3" (game-loop demo-three-arena-atom three-actions display-three-arena)
    (println "argument must be '2' or '3' indicating game dimensionality")))
