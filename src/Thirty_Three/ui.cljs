(ns Thirty-Three.ui
  (:use [jayq.core :only [$ html add-class remove-class]])
  (:require [clojure.string :as string]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [Thirty-Three.foundation :as fdn]))

(enable-console-print!)

(defn seed-size [n arena-size]
  (* 2 (Math/pow 4 (- n 2))))

(defn seed! [state j]
  (doseq [_ (range j)]
    (swap! state fdn/fill-vacancy)))

(def dimensionality 3)

(def game-state
  (atom (fdn/clean-n-arena 3 4)))
(seed! game-state (seed-size 3 4))

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
  (when-let [arena-size (count @state)]
    (let [n (fdn/infer-dimensionality @state)]
      (reset! state (fdn/clean-n-arena n (+ arena-size delta)))
      (seed! state (seed-size n arena-size))))
  (reset! previous-states []))

(defn alter-dimensionality! [state previous-states n]
  (def dimensionality n)
  (let [arena-size (count @state)]
    (reset! state (fdn/clean-n-arena n arena-size))
    (seed! state (seed-size n arena-size)))
  (reset! previous-states [])
 (label-availability!))

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

(def dimensionality-to-actions
  {2 two-actions
   3 three-actions})

(def all-actions
  (set (apply concat (map keys [two-actions three-actions]))))

(def ancillary-availability-ps
  {:undo (fn [] (not (empty? @previous-game-states)))})

(defn label-availability! []
  (doseq [action all-actions]
    (let [$action ($ (str "." (name action)))
          active (not (nil? ((dimensionality-to-actions dimensionality) action)))
          [class-to-add class-to-remove] (if active
                                           ["available" "unavailable"]
                                           ["unavailable" "available"])]
      (prn action active class-to-add class-to-remove)
      (add-class $action class-to-add)
      (remove-class $action class-to-remove))))

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
