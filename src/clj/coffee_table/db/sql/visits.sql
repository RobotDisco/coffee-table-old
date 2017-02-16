-- src/coffee_table/db/sql/visits.sql
-- Café Visits

-- :name create-rating-type
-- :command :execute
-- :result :raw
-- :doc Create rating type
CREATE TYPE rating AS ENUM (1, 2, 3, 4, 5);

-- :name create-visits-table :!
-- :doc Create visits table
create table visits (
  id integer PRIMARY KEY,
  cafe_name text NOT NULL,
  date_visited date NOT NULL,
  -- address,
  machine text,
  grinder text,
  roast text,
  beverage_ordered text NOT NULL,
  beverage_rating rating NOT NULL,
  beverate_notes text,
  service_rating rating,
  service_notes text,
  ambience_rating rating,
  ambience_notes text,
  other_notes text
)

-- :name visit-by-id :? :1
-- :doc Get café visit by id
select * from visits where id = :id

-- :name all-visits :? :*
-- :doc Get all café visits
select * from visits

-- :name insert-visit :<!
-- :doc Create a café visit
insert into visits (cafe_name, date_visited, machine, grinder, roast, beverage_ordered, beverage_rating, beverage_notes, service_rating, service_notes, ambience_rating, ambience_notes, other_notes) VALUES (:name, :date, :machine, :grinder, :roast, :beverage-ordered, :beverage-rating, :beverage-notes, :service-rating, :service-notes, :ambience-rating, :ambience-notes, :other-notes) returning id

-- :name update-visit-by-id :! :n
-- :doc Update a café visit with the following values
update visits SET (cafe_name, date_visited, machine, grinder, roast, beverage_ordered, beverage_rating, beverage_notes, service_rating, service_notes, ambience_rating, ambience_notes, other_notes) = (:name, :date, :machine, :grinder, :roast, :beverage-ordered, :beverage-rating, :beverage-notes, :service-rating, :service-notes, :ambience-rating, :ambience-notes, :other-notes) where id = :id

-- :name delete-visit-by-id :! :n
-- :doc Delete a café vosot with the given ID
delete from visits where id = :id
