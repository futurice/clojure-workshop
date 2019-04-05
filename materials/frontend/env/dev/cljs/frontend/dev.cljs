(ns ^:figwheel-no-load frontend.dev
  (:require
    [frontend.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
