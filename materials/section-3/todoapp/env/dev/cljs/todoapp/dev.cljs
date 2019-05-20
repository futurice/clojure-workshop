(ns ^:figwheel-no-load todoapp.dev
  (:require
    [todoapp.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
