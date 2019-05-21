(ns todoapp.todo)

(def conj-sort (comp (partial sort-by :id) conj))

(defn add-todo [todos todo]
  (swap! todos conj-sort todo))

(defn remove-todo [todos id]
  (reset! todos (remove (fn [todo] (= (:id todo) id)) @todos)))

(defn toggle-done [todos id]
  (when-let [todo (first (filter #(= (:id %) id) @todos))]
    (remove-todo todos id)
    (add-todo todos (assoc todo :done (-> todo :done not)))))
