(ns todoapp.core
    (:require
      [reagent.core :as r]
      [todoapp.todo :refer [add-todo remove-todo toggle-done]]))

;; Atoms

(def id-counter (r/atom 2))

(def todos
  (r/atom
   [{:id 1 :name "Write frontend" :done false}
    {:id 2 :name "Test it" :done true}]))

;; -------------------------
;; Views

(defn- render-todo [todo]
  [:li {:class "todo-item"}
   [:label
    [:input {:type "checkbox"
             :checked (if (:done todo) "checked" "")
             :on-change #(toggle-done todos (:id todo))}]
    (:name todo)]
   [:button
    {:on-click #(remove-todo todos (:id todo))}
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
         (fn [] (add-todo todos
                    {:id (swap! id-counter inc)
                     :name @todo-text
                     :done false})
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
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
