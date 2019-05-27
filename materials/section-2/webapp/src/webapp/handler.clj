(ns webapp.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.coercions :refer [as-int]]
            [ring.util.response :as r]
            [hugsql.core :refer [def-db-fns]]
            [ring.middleware.params :as request-middleware]
            [ring.middleware.json :as json-middleware]
            [ring.middleware.cors :refer [wrap-cors]]))

(def db-url "postgresql://postgres@localhost:5432/clojure_workshop_db")

(def-db-fns "webapp/queries.sql")

(defroutes app-routes
  (context "/api" []
    (GET "/todos" [sortby]
      (let [todos (sort-by :id (fetch-todos! db-url))]
        (if (= sortby "desc")
          (r/response (reverse todos))
          (r/response todos))))
    (POST "/todos" [name]
      (insert-todo! db-url {:name name})
      (r/response (fetch-todos! db-url)))
    (PATCH "/todos/:id" [id :<< as-int]
      (update-todo! db-url {:id id})
      (r/response (fetch-todos! db-url)))
    (DELETE "/todos/:id" [id :<< as-int]
      (let [deleted-count (delete-todo! db-url {:id id})]
        ;; If lenghts are the same, nothing has been deleted
        (if (= deleted-count 0)
          (r/status nil 400)
          (r/response (fetch-todos! db-url)))))
    (DELETE "/todos/empty" []
      (clear-todos! db-url)
      (r/response (fetch-todos! db-url)))
    (DELETE "/todos/special" []
      (let [power-of-2? (fn [id] (and
                                   (not (= id 0))
                                   (= (bit-and id (- id 1)) 0)))
            idx (->> db-url
                    (fetch-todos!)
                    (map :id)
                    (filter power-of-2?))]
        (delete-todo-special! db-url {:idx idx})
        (r/response (fetch-todos! db-url)))))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      request-middleware/wrap-params
      (wrap-cors :access-control-allow-origin [#"http://localhost:3449"]
                 :access-control-allow-methods [:get :patch :post :delete])
      json-middleware/wrap-json-params
      json-middleware/wrap-json-response))
