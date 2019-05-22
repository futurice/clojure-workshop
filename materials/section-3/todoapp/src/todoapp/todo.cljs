(ns todoapp.todo
  (:require
    [reagent.core :as r]
    [ajax.core :refer [GET POST PATCH DELETE]]))

(def url-base "http://localhost:3000/api/todos")

(def todos
  (r/atom []))

(defn- update-state! [data]
  (println "update-state!" data)
  (reset! todos data))

(defn fetch-todos! []
  (GET url-base
       {:handler update-state!}))

(defn add-todo! [text]
  (POST url-base
        {:params {:name text}
         :format :json
         :response-format :json
         :keywords? true
         :handler update-state!}))

(defn remove-todo! [id]
  (println "remove-done!" id)
  (DELETE (str url-base "/" id)
         {:handler update-state!
          :response-format :json
          :keywords? true}))

(defn toggle-done! [id]
  (println "toggle-done!" id)
  (PATCH (str url-base "/" id)
         {:handler update-state!
          :response-format :json
          :keywords? true}))
