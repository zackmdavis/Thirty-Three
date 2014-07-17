(ns Thirty-Three.ui
  (:require-macros [Thirty-Three.macro-library :as macros])
  (:require [clojure.string :as string]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [Thirty-Three.foundation :as fdn]))

(enable-console-print!)

(def game-state
  (atom (macros/clean-n-arena 2 4)))
(doseq [_ (range 2)]
  (swap! game-state fdn/fill-vacancy))

(defn tile-element [value]
  (let [value-maybe (or value "_")]
    (dom/div #js {:className "tile" :data-value value-maybe}
             value-maybe)))

(defn row-element [i row-state]
  (apply dom/div #js {:id (str "row" i) :className "row"} 
         (map tile-element row-state)))

(defn arena-view [arena-state owner]
  (om/component
   (apply dom/div #js {:id "arena"}
          (map-indexed row-element
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

(def keycodes
  (merge (zipmap (range 37 41) [:left :up :right :down])
         {65 :left, 87 :up, 68 :right, 83 :down}))

(defn set-keypress-listener! []
  (.addEventListener js/document "keydown"
                     (fn [event]
                       (let [action (two-actions (keycodes (.-keyCode event)))]
                         (swap! game-state action)
                         (swap! game-state fdn/fill-vacancy)))))

(set-keypress-listener!)
(prn "Hello ClojureScript World from ui.cljs")
(prn @game-state)
