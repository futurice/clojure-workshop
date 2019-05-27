-- :name fetch-todos! :? :*
SELECT * FROM todo;

-- :name insert-todo! :? :<!
INSERT INTO todo (name) VALUES (:name) RETURNING*;

-- :name update-todo! :! :1
UPDATE todo SET done = NOT done WHERE id = :id;

-- :name delete-todo! :! :1
DELETE FROM todo WHERE id = :id;

-- :name clear-todos! :! :*
DELETE FROM todo;

-- :name delete-todo-special! :! :*
DELETE FROM todo WHERE id IN (:v*:idx);
