(ns Thirty-Three.ui
  (:require-macros [Thirty-Three.macro-library :as macros])
  (:require [Thirty-Three.foundation :as fdn])
  ;; (:require [om.core :as om :include-macros true]
  ;;           [om.dom :as dom :include-macros true]))
)

(def game-state
  (atom (macros/clean-n-arena 2 4)))

(swap! game-state #(fdn/write % [0 0] 1))
(swap! game-state #(fdn/write % [2 3] 1))

(.log js/console "Hello ClojureScript World from ui.cljs")
(.log js/console (println-str @game-state))
