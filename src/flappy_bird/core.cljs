(ns flappy-bird.core
  (:require [meowser.core :as m.core]
            [meowser.scene :as m.scene]
            [meowser.sprite :as m.sprite]))


(defn wrap-scene [phaser-scene]
(reify m.core/MeowserBase
  (scene [_] phaser-scene)
  (sprite [_] nil)))


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
                    (m.scene/add-image! wrapped "getready" :position [(/ width 2) (* height 0.51)]) 
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
                    (let [tap-sprite (m.sprite/gen-ui wrapped :key "tap-1" :x (/ width 2) :y (* height 0.68))] 
                      (m.sprite/play-anim tap-sprite "tap-blink")
                      ) 
                    (let [ground-sprite (m.sprite/gen-ui wrapped :key "ground-1" :x (/ width 2) :y (* height 0.92))] 
                      ;; (m.sprite/play-anim ground-sprite "ground-move")
                    ) 
                    (let [bird-sprite (m.sprite/gen-ui wrapped :key "bird-1" :x (/ width 4) :y (* height 0.2))] 
                      (m.sprite/play-anim bird-sprite "bird-fly")
                    ) 
                    ))
}))
    (sprite [_] nil)))

(defn init []
  (m.core/gen-game
   {:debug? true
    :width 275
    :height 375
    :gravity 300
    :bg "#30c0df"
    :boot :boot
    :scenes [boot-scene]}))
