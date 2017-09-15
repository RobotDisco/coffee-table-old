-- src/coffee_table/db/sql/users.sql
-- Coffee Table Users

-- :name user-by-username :? :1
-- :doc Get coffee table user by username string
select * FROM users where username = :username
