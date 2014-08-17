(ns Thirty-Three.ui
  (:require-macros [Thirty-Three.macro-library :as macros])
  (:require [clojure.string :as string]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [Thirty-Three.foundation :as fdn]))

(enable-console-print!)

(defn seed! [state j]
  (doseq [_ (range j)]
    (swap! state fdn/fill-vacancy)))

(def game-state
  (atom (macros/clean-n-arena 2 4)))
(seed! game-state 2)

(def previous-game-states
  (atom []))

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

(defn slide! [state previous-states sliding-dimension direction]
  (swap! previous-states #(conj % @game-state))
  (swap! game-state #(fdn/slide-arena % sliding-dimension direction))
  (swap! game-state fdn/fill-vacancy))

(defn undo! [state previous-states]
  (reset! state (peek @previous-states))
  (reset! previous-states
          (pop @previous-states)))

(defn resize! [state previous-states delta]
  (when-let [arena-size (count @state)]
    (reset! state (macros/clean-n-arena 2 (+ arena-size delta)))
    (seed! state 2))
  (reset! previous-states []))


(def two-actions ; [direction]
  {:left  #(slide! %1 %2 1 :back)     
   :right #(slide! %1 %2 1 :forward)  
   :up    #(slide! %1 %2 0 :back)     
   :down  #(slide! %1 %2 0 :forward)
   :undo  undo!
   :expand   #(resize! %1 %2 +1)
   :contract #(resize! %1 %2 -1)})

(def keycodes
  (merge (zipmap (range 37 41) [:left :up :right :down])
         {65 :left, 87 :up, 68 :right, 83 :down}
         {85 :undo}
         {109 :contract 189 :contract 107 :expand 187 :expand}))

(defn set-keypress-listener! []
  (.addEventListener 
   js/document "keydown"
   (fn [event]
     (when-let [action! (two-actions (keycodes (.-keyCode event)))]
       (action! game-state previous-game-states)))))

(set-keypress-listener!)
(prn "Hello ClojureScript World from ui.cljs")
(prn @game-state)
