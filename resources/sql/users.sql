-- src/coffee_table/db/sql/users.sql
-- Coffee Table Users

-- :name private-user-by-username :? :1
-- :doc Get coffee table user by username string. INCLUDES PASSWORD. BE CAREFUL
SELECT * FROM users WHERE username = :username

-- :name all-users :? :*
-- :doc Get all app users
SELECT id, username, is_admin FROM users
