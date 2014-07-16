(ns Thirty-Three.ui
  (:require-macros [Thirty-Three.macro-library :as macros])
  (:require [clojure.string :as string]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [Thirty-Three.foundation :as fdn]))

(enable-console-print!)

(def game-state
  (atom (macros/clean-n-arena 2 4)))
(swap! game-state #(fdn/write % [0 0] 1))
(swap! game-state #(fdn/write % [2 2] 1))

(defn row-component [i row-state]
  (dom/div #js {:id (str "row" i)} 
           (string/join " " (map (fn [value] (if value value "_"))
                                 row-state))))

(defn arena-view [arena-state owner]
  (om/component
   (apply dom/div #js {:id "arena-view"}
          (map-indexed row-component
                       arena-state))))

(om/root
 arena-view
 game-state
 {:target (. js/document (getElementById "downtown"))})

(def two-actions ; [direction]
  {:left  #(fdn/slide-arena % 1 :back)     
   :right #(fdn/slide-arena % 1 :forward)  
   :up    #(fdn/slide-arena % 0 :back)     
   :down  #(fdn/slide-arena % 0 :forward)})

(defn set-click-handlers []
  (doseq [direction ["up" "down" "left" "right"]]
    (let [button (.getElementById js/document direction)]
         (set! (.-onclick button)
               (fn [e] (do
                         (prn direction " button clicked")
                         (swap! game-state
                                (two-actions (keyword direction)))
                         (swap! game-state fdn/fill-vacancy)))))))

(set-click-handlers)
(.log js/console "Hello ClojureScript World from ui.cljs")
(prn @game-state)
