(ns koans.13-threading
  (:require [koan-engine.core :refer :all]))

(meditations
 "Calling many functions on a single piece of data can be hard to digest"
 (= __ (first (.split (.replace (.toUpperCase "f u t u") "F" "T") " ")))


 "But we can make them more consumable by threading the needle"
 (= __ (-> "a i t o"
           (.replace "o" "i")
           (.replace "a" "ä")
           .toUpperCase))

 "But sometimes we want to thread the needle in a different order"
 (= __ (->> (iterate inc 0)
            (map #(mod % 10))
            (take 10)))
 )
