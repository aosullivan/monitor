--name:save-service!
-- inserts a service
insert into services (id, name, url, status)
values (:id, :name, :url, :status)

--name:get-services
-- selects all services
select * from services