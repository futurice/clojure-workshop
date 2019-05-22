(ns todoapp.todo
  (:require
    [reagent.core :as r]
    [ajax.core :refer [GET POST PATCH DELETE]]))

(def url-base "http://localhost:3000/api/todos")

(def todos
  (r/atom []))

(defn- update-state! [data]
  (reset! todos data))

(def default-options
  {:handler update-state!
   :response-format :json
   :keywords? true})

(defn fetch-todos! []
  (GET url-base default-options))

(defn add-todo! [text]
  (POST url-base
        (merge default-options
          {:params {:name text}
           :format :json})))

(defn remove-todo! [id]
  (DELETE (str url-base "/" id) default-options))

(defn toggle-done! [id]
  (PATCH (str url-base "/" id) default-options))
