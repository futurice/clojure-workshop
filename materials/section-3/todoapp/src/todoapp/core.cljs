(ns todoapp.core
    (:require
      [reagent.core :as r]
      [todoapp.todo :refer [todos add-todo! remove-todo! toggle-done! fetch-todos!]]))

;; -------------------------
;; Views

(defn- render-todo [todo]
  [:li {:class "todo-item"}
   [:label
    [:input {:type "checkbox"
             :checked (if (:done todo) "checked" "")
             :on-change #(toggle-done! (:id todo))}]
    (:name todo)]
   [:button
    {:on-click #(remove-todo! (:id todo))}
    "❌"]])

(defn- render-todo-adder []
  (let [todo-text (r/atom "")]
    (fn []
      [:li
       [:input {:type "text"
                :value @todo-text
                :on-change #(reset! todo-text (-> % .-target .-value))}]
       [:button
        {:on-click
         (fn [] (add-todo! @todo-text)
                (reset! todo-text ""))}
        "Add"]])))

(defn home-page []
  [:ul
   (for [todo @todos]
     ^{:key (:id todo)} [render-todo todo])
   [render-todo-adder]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (fetch-todos!)
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
