(ns webapp.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as r]
            [ring.middleware.params :as request-middleware]
            [ring.middleware.json :as json-middleware]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

;; step 1 - lein new compojure webapp
;; step 2 - show how compojure works
;; step 3 - create a fake db atom : Attention! Missing middleware!
;; step 4 - remove unused middleware (defaults)
;; step 5 - add ring.middleware.json
;; step 6 - add ring.middleware.params

(def todos (atom []))

(def id-counter (atom 0))

(defroutes app-routes
  (context "/api" []
    (GET "/todos" []
      (r/response @todos))
    (POST "/todos" [name]
      (swap! todos conj {:id (swap! id-counter inc) :name name :done false})
      (r/response @todos))
    (DELETE "/todos/:id" [id]
      (reset! todos (remove (fn [todo] (= (:id todo) (Integer/parseInt id))) @todos))
      (r/response @todos)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      request-middleware/wrap-params
      json-middleware/wrap-json-params
      json-middleware/wrap-json-response))
