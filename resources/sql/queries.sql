--name:save-service-check!
-- inserts a service
insert into service-checks (id, environment_id, description, updated_date, status)
values (:id, :environment_id, :description, :updated_date, :status)

--name:get-service-checks
-- selects all services check results
select * from service-checks

--name:get-environments
-- selects all services check results
select * from environments