(ns flappy-bird.core
  (:require [meowser.core :as m.core]
            [meowser.scene :as m.scene]
            [meowser.sprite :as m.sprite]))


(defn wrap-scene [phaser-scene]
(reify m.core/MeowserBase
  (scene [_] phaser-scene)
  (sprite [_] nil)))


(def game-state (atom :title))

(def boot-scene
  (reify m.core/MeowserBase
    (scene [_]
      (clj->js
       {:key "boot"
        :preload (fn []
        (let [this (js* "this") wrapped (wrap-scene this)]
          (js/console.log "preload this:" this)
          (m.scene/load-image wrapped "bg" "assets/background.png")
          (m.scene/load-image wrapped "getready" "assets/getready.png")
          (m.scene/load-image wrapped "ground-1" "assets/ground-1.png")
          (m.scene/load-image wrapped "ground-2" "assets/ground-2.png")
          (m.scene/load-image wrapped "bird-1" "assets/bird-1.png")
          (m.scene/load-image wrapped "bird-2" "assets/bird-2.png")
          (m.scene/load-image wrapped "bird-3" "assets/bird-3.png")
          (m.scene/load-image wrapped "tap-1" "assets/tap-1.png") 
          (m.scene/load-image wrapped "tap-2" "assets/tap-2.png")))

        :create (fn []
                  (let [this (js* "this") wrapped (wrap-scene this)
                        width 275
                        height 375]
                    (m.scene/add-image! wrapped "bg" :position [(/ width 2) (* height 0.76)]) 
                    ;; (m.scene/add-image! wrapped "ground-1" :position [(/ width 2) (* height 0.92)])  
                    (m.sprite/create-anim wrapped
                                          {:key "tap-blink"
                                            :frames (clj->js [{:key "tap-1"}
                                                              {:key "tap-2"}])
                                            :frameRate 4
                                            :repeat -1})
                    (m.sprite/create-anim wrapped
                                          {:key "ground-move"
                                            :frames (clj->js [{:key "ground-1"}
                                                              {:key "ground-2"}])
                                            :frameRate 10
                                            :repeat -1})
                    (m.sprite/create-anim wrapped
                                          {:key "bird-fly"
                                            :frames (clj->js [{:key "bird-1"}
                                                              {:key "bird-2"}
                                                              {:key "bird-3"}])
                                            :frameRate 4
                                            :repeat -1})
                    (let [this (js* "this")
                          wrapped (wrap-scene this)
                          tap (m.sprite/gen-ui wrapped :key "tap-1" :x (/ width 2) :y (* height 0.68))
                          ground (m.sprite/gen-sprite wrapped :key "ground-1" :x (/ width 2) :y (* height 0.92))
                          bird (m.sprite/gen-sprite wrapped :key "bird-1" :x (/ width 4) :y (* height 0.2))
                          getready (m.sprite/gen-ui wrapped :key "getready" :x (/ width 2) :y (* height 0.51))
                          ]
                      
                      (m.sprite/play-anim tap "tap-blink")
                      ;; (m.sprite/play-anim ground "ground-move")
                      (m.sprite/play-anim bird "bird-fly")
                      (m.sprite/set-allow-gravity! bird false)
                      (m.sprite/set-allow-gravity! ground false)
                      (m.sprite/set-immovable! ground true)
                      (m.sprite/collider-with-sprite bird ground
                      (fn []
                        (print "game over.")
                        (reset! game-state :gameover)
                        ))

                      (set! (.-tapSprite this) tap)
                      (set! (.-groundSprite this) ground)
                      (set! (.-bird this) bird)

                      (.on (.-input (m.core/scene wrapped)) "pointerdown"
                            (fn []
                              (case @game-state
                                :title
                                (do
                                  (m.sprite/set-visible! tap false)
                                  (m.sprite/set-visible! getready false)
                                  (reset! game-state :playing)
                                  (m.sprite/set-allow-gravity! bird true))
                                :playing
                                (do
                                  (m.sprite/set-velocity! bird {:y -150})
                                  (m.sprite/set-rotation! bird (/ js/Math.PI -4)))
                                :gameover
                          ))))
                    ))
        :update (fn [] (case @game-state
                          :title
                          (let [this (js* "this")
                                t (/ (.. this -time -now) 1000)
                                bird (.-bird this)
                                width 275
                                height 375
                                y (+ (* height 0.22) (* 5 (Math/sin t)))]
                            (m.sprite/set-position! bird {:x (/ width 6) :y y})
                            )
                          :playing (let [this (js* "this") bird (.-bird this)]
                                 (when (< (m.sprite/rotation bird) (/ js/Math.PI 2))
                                   (m.sprite/rotate! bird 0.01))
                          )
                          :gameover
                          ))
}))
    (sprite [_] nil)))

(defn init []
  (m.core/gen-game
   {:debug? false
    :width 275
    :height 375
    :gravity 300
    :bg "#30c0df"
    :boot :boot 
    :pixelArt false
    :scenes [boot-scene]}))
