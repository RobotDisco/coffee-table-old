-- src/coffee_table/db/sql/visits.sql
-- Café Visits

-- :name visit-by-id :? :1
-- :doc Get café visit by id
select * from visits where id = :id

-- :name all-visits :? :*
-- :doc Get all café visits
select * from visits

-- :name insert-visit :<!
-- :doc Create a café visit
insert into visits (
cafe_name
,date_visited
--~ (when (contains? params :machine) ",machine")
--~ (when (contains? params :grinder) ",grinder")
--~ (when (contains? params :roast) ",roast")
,beverage_ordered
,beverage_rating
--~ (when (contains? params :beverage-notes) ",beverage_notes")
--~ (when (contains? params :service-rating) ",service_rating")
--~ (when (contains? params :service-notes) ",service_notes")
--~ (when (contains? params :ambience-rating) ",ambience_rating")
--~ (when (contains? params :ambience-notes) ",ambience_notes")
--~ (when (contains? params :other-notes) ",other_notes")
) VALUES (
:name
,:date
--~ (when (contains? params :machine) ",:machine")
--~ (when (contains? params :grinder) ",:grinder")
--~ (when (contains? params :roast) ",:roast")
,:beverage-ordered
,:beverage-rating
--~ (when (contains? params :beverage-notes) ",:beverage-notes")
--~ (when (contains? params :service-rating) ",:service-rating")
--~ (when (contains? params :service-notes) ",:service-notes")
--~ (when (contains? params :ambience-rating) ",ambience-rating")
--~ (when (contains? params :ambience-notes) ",:ambience-notes")
--~ (when (contains? params :other-notes) ",:other-notes")
) returning id

-- :name update-visit-by-id :! :n
-- :doc Update a café visit with the following values
update visits SET
cafe_name = :name
,date_visited = :date
--~ (when (contains? params :machine) ",machine = :machine")
--~ (when (contains? params :grinder) ",grinder = :grinder")
--~ (when (contains? params :roast) ",roast = :roast")
,beverage_ordered = :beverage-ordered
,beverage_rating = :beverage-rating
--~ (when (contains? params :beverage-notes) ",beverage_notes = :beverage-notes")
--~ (when (contains? params :service-rating) ",service_rating = :service-rating")
--~ (when (contains? params :service-notes) ",service_notes = :service-notes")
--~ (when (contains? params :ambience-rating) ",ambience_rating = :ambience-rating")
--~ (when (contains? params :ambience-notes) ",ambience_notes = :ambience-notes")
--~ (when (contains? params :other-notes) ",other_notes = :other-notes")
where id = :id

-- :name delete-visit-by-id :! :n
-- :doc Delete a café visit with the given ID
delete from visits where id = :id

-- :name delete-all-visits :! :n
-- :doc Delete all visits
delete from visits
