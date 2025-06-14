(ns flappy-bird.core
  (:require [meowser.core :as m.core]
            [meowser.scene :as m.scene]))

(defn load-image-raw [phaser-scene name path]
  (.image (.-load phaser-scene) name path))

(defn add-image-raw! [scene name & {:keys [position size]}]
(let [image (.image (.-add scene) name
                    (first position) (second position))]
  (when size
    (.setDisplaySize image (first size) (second size)))
  image))



(def boot-scene
  (reify m.core/MeowserBase
    (scene [_]
      (clj->js
       {:key "boot"
        :preload (fn []
        (let [this (js* "this")]
          (js/console.log "preload this:" this)
          (load-image-raw this "bg" "assets/background.png")))

        :create (fn []
        (let [this (js* "this")
              width (.-innerWidth js/window)
              height (.-innerHeight js/window)]
          (add-image-raw! this "bg"
                          :position [0 0]
                          :size [width height])))
}))
    (sprite [_] nil)))

(defn init []
  (m.core/gen-game
   {:debug? true
    :width 400
    :height 600
    :gravity 300
    :boot :boot
    :scenes [boot-scene]}))
