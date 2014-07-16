(ns Thirty-Three.ui
  (:require-macros [Thirty-Three.macro-library :as macros])
  (:require [Thirty-Three.foundation :as fdn]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

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
    (dom/div nil
        (dom/div nil (str arena-state)))))

(om/root
 arena-view
 game-state
 {:target (. js/document (getElementById "downtown"))})

(defn actions [direction]
  {:left  #(fdn/slide-arena % 1 :back)     
   :right #(fdn/slide-arena % 1 :forward)  
   :up    #(fdn/slide-arena % 0 :back)     
   :down  #(fdn/slide-arena % 0 :forward)})

(defn set-click-handlers []
  (doseq [direction ["up" "down" "left" "right"]]
    (let [button (.getElementById js/document direction)]
         (set! (.-onclick button)
               (fn [e] (do
                         (prn "click")
                         (prn "before swap game state" @game-state)
                         (swap! game-state (fn [s] (str "Game State" direction)))
                         (prn "after swap game state"  @game-state)))))))

(let [button (.getElementById js/document "test-button")]
  (set! (.-onclick button)
        (fn [e] (do
                  (prn "button clicked")))))

(set-click-handlers)
(.log js/console "Hello ClojureScript World from ui.cljs")
(prn @game-state)
