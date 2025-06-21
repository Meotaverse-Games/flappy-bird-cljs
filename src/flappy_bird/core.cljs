(ns flappy-bird.core
  (:require [meowser.core :as m.core]
            [meowser.scene :as m.scene]))

(defn load-image-raw [phaser-scene name path]
  (.image (.-load phaser-scene) name path))

(defn add-image-raw! [scene name & {:keys [position size]}]
(let [[x y] position
      image (.image (.-add scene) x y name)] ; ← 順序修正
  (when size
    (.setDisplaySize image (first size) (second size)))
  image))


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
          (m.scene/load-image wrapped "ground" "assets/ground.png")
          (m.scene/load-image wrapped "getready" "assets/getready.png")
          (m.scene/load-image wrapped "tap" "assets/tap.png")
          ))

        :create (fn []
        (let [this (js* "this") wrapped (wrap-scene this)
              width 275
              height 375]
          (m.scene/add-image! wrapped "bg" :position [(/ width 2) (* height 0.76)]) 
          (m.scene/add-image! wrapped "ground" :position [(/ width 2) (* height 0.92)]) 
          (m.scene/add-image! wrapped "getready" :position [(/ width 2) (* height 0.51)]) 
          (m.scene/add-image! wrapped "tap" :position [(/ width 2) (* height 0.68)]) 
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
