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

(def dimensionality 3)

(def game-state
  (atom (macros/clean-n-arena 3 4)))
(seed! game-state 4)

(def previous-game-states
  (atom []))

(defn tile-element [value]
  (let [value-maybe (or value "_")]
    (dom/div #js {:className "tile" :data-value value-maybe}
             value-maybe)))

(defn row-element [j row-state]
  (apply dom/div #js {:id (str "row" j) :className "row"} 
         (map tile-element row-state)))

(defn level-element [i level-state]
  (apply dom/div #js {:id (str "level" i) :className "level"}
         (map-indexed row-element level-state)))

(defn arena-view [arena-state owner]
  (condp = dimensionality
    2 (om/component
       (apply dom/div #js {:id "two-arena" :className "level"}
              (map-indexed row-element
                           arena-state)))
    3 (om/component
       (apply dom/div #js {:id "three-arena"}
              (map-indexed level-element
                           arena-state)))))

(om/root
 arena-view
 game-state
 {:target (. js/document (getElementById "downtown"))})

(defn slide! [state previous-states sliding-dimension direction]
  (swap! previous-states #(conj % @state))
  (swap! state #(fdn/slide-arena % sliding-dimension direction))
  (when (seq (fdn/vacancies @state))
    (swap! state fdn/fill-vacancy)))

(defn undo! [state previous-states]
  (when (peek @previous-states)
    (reset! state (peek @previous-states))
    (reset! previous-states (pop @previous-states))))

(defn resize! [state previous-states delta]
  (condp = dimensionality
    ;; XXX TODO FIXME: investigate possible brokenness of clean-n-arena
    2 (when-let [arena-size (count @state)]
        (reset! state (macros/clean-n-arena 2 (+ arena-size delta)))
        (seed! state 2))
    3 (when-let [arena-size (count @state)]
        (reset! state (macros/clean-n-arena 3 (+ arena-size delta)))
        (seed! state 4)))
    (reset! previous-states []))

(defn alter-dimensionality! [state previous-states n]
  (condp = n
    2 (do
        ;; XXX TODO FIXME: investigate whether and how clean-n-arena
        ;; is broken so that this can be a function
        (def dimensionality 2)
        (reset! state (macros/clean-n-arena 2 4))
        (seed! state 2)
        (reset! previous-states []))
    3 (do
        (def dimensionality 3)
        (reset! state (macros/clean-n-arena 3 4))
        (seed! state 4)
        (reset! previous-states []))))

(def two-actions
  {:left  #(slide! %1 %2 1 :back)     
   :right #(slide! %1 %2 1 :forward)  
   :up    #(slide! %1 %2 0 :back)     
   :down  #(slide! %1 %2 0 :forward)
   :undo  undo!
   :expand   #(resize! %1 %2 +1)
   :contract #(resize! %1 %2 -1)
   :three-alter #(alter-dimensionality! %1 %2 3)})

(def three-actions
  {:left  #(slide! %1 %2 2 :back)     
   :right #(slide! %1 %2 2 :forward)  
   :up    #(slide! %1 %2 1 :back)     
   :down  #(slide! %1 %2 1 :forward)
   :west  #(slide! %1 %2 0 :back)
   :east  #(slide! %1 %2 0 :forward)
   :undo  undo!
   :expand   #(resize! %1 %2 +1)
   :contract #(resize! %1 %2 -1)
   :two-alter #(alter-dimensionality! %1 %2 2)})

(def keycodes
  (merge (zipmap (range 37 41) [:left :up :right :down])
         {65 :left, 87 :up, 68 :right, 83 :down, 81 :west, 69 :east}
         {85 :undo}
         {109 :contract, 189 :contract, 107 :expand, 187 :expand}
         {219 :two-alter, 221 :three-alter}))

(defn set-keypress-listener! []
  (.addEventListener 
   js/document "keydown"
   (fn [event]
     (condp = dimensionality
       2 (when-let [action! (two-actions (keycodes (.-keyCode event)))]
           (action! game-state previous-game-states))
       3 (when-let [action! (three-actions (keycodes (.-keyCode event)))]
           (action! game-state previous-game-states))))))

(set-keypress-listener!)
(prn "Hello ClojureScript World from ui.cljs")
(prn @game-state)
