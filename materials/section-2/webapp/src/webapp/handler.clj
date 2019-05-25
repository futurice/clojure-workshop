(ns webapp.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.coercions :refer [as-int]]
            [ring.util.response :as r]
            [ring.middleware.params :as request-middleware]
            [ring.middleware.json :as json-middleware]
            [ring.middleware.cors :refer [wrap-cors]]))

(def todos (atom []))

(def id-counter (atom 0))

(def conj-sort (comp (partial sort-by :id) conj))

(defn- add-todo! [todo]
  (swap! todos conj-sort todo))

(defn- remove-todo! [id]
  (reset! todos (remove (fn [todo] (= (:id todo) id)) @todos)))

(defn- toggle-done! [id]
  (when-let [todo (first (filter #(= (:id %) id) @todos))]
    (remove-todo! id)
    (add-todo! (assoc todo :done (-> todo :done not)))))

(defroutes app-routes
  (context "/api" []
    (GET "/todos" [sortby]
      (let [todos (sort-by :id @todos)]
        (if (= sortby "desc")
          (r/response (reverse todos))
          (r/response todos))))
    (POST "/todos" [name]
      (add-todo! {:id (swap! id-counter inc) :name name :done false})
      (r/response @todos))
    (PATCH "/todos/:id" [id :<< as-int]
      (toggle-done! id)
      (r/response @todos))
    (DELETE "/todos/:id" [id :<< as-int]
      (let [current-length (count @todos)]
        (remove-todo! id)
        ;; If lenghts are the same, nothing has been deleted
        (if (= current-length (count @todos))
          (r/status nil 400)
          (r/response @todos))))
    (DELETE "/todos/empty" []
      (reset! todos [])
      (r/response @todos))
    (DELETE "/todos/special" []
      (letfn [(power-of-2? [id] (true? (and
                                         (not (= id 0))
                                         (= (bit-and id (- id 1)) 0))))]
        (reset! todos (filterv (fn [todo] (not (power-of-2? (:id todo)))) @todos))
        (r/response @todos))))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      request-middleware/wrap-params
      (wrap-cors :access-control-allow-origin [#"http://localhost:3449"]
                 :access-control-allow-methods [:get :patch :post :delete])
      json-middleware/wrap-json-params
      json-middleware/wrap-json-response))
