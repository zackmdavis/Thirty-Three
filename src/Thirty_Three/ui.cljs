(ns Thirty-Three.ui
  (:require-macros [Thirty-Three.macro-library :as macros])
  (:require [Thirty-Three.foundation :as fdn]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def game-state
  (atom (macros/clean-n-arena 2 4)))
(swap! game-state #(fdn/write % [0 0] 1))
(swap! game-state #(fdn/write % [2 2] 1))

;; XXX desperately hacking to get something, anything to "work" in
;; some sense
(defn line-to-text [i line]
  (apply dom/div #js {:id (str "row" i) :className "row"}
         (map (fn [j]
                (let [thing (line j)]
                  (if thing
                    (str "" thing "|")
                    "_|")))
              (range 4))))

(defn arena-view [arena-state owner]
  (om/component
   (apply dom/div #js {:className "arena"}
          (map-indexed line-to-text
                       arena-state))))

(om/root
 arena-view
 game-state
 {:target (. js/document (getElementById "downtown"))})

(.log js/console "Hello ClojureScript World from ui.cljs")
(.log js/console (println-str @game-state))
