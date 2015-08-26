--name:save-service-check<!
-- inserts a row into service_check
insert into service_checks (environment_id, service_check_id, description, updated_date, status)
values (:environment_id, :service_check_id, :description, :updated_date, :status)

--name:update-service-check-status!
-- updates the service-check status
update service_checks 
set status = :status 
where id = :id

--name:get-service-checks
-- selects all services check results
select  e.ID as ENVIRONMENT_ID,
        sc.ID as SERVICE_CHECK_ID,
        e.DESCRIPTION as ENVIRONMENT_DESCRIPTION,
        sc.DESCRIPTION as SERVICE_CHECK_DESCRIPTION,
        sc.UPDATED_DATE,
        sc.STATUS
from service_checks sc,
     environments e
where sc.ENVIRONMENT_ID = e.ID
order by e.ID, sc.ID

--name:delete-service-checks!
-- deletes all service checks
delete from service_checks 

--name:reset-service-check-counter!
-- reset the auto increment counter
ALTER TABLE service_checks ALTER COLUMN id RESTART WITH 1

--name:reset-environment-counter!
-- reset the auto increment counter
ALTER TABLE environments ALTER COLUMN id RESTART WITH 1

--name:delete-environments!
-- deletes all environments
delete from environments 

--name:save-environment<!
-- inserts a row into environment
insert into environments (key, description, status)
values (:key, :description, :status)

--name:get-environments
-- selects all environments
select * from environments

--name:get-environment-id-from-key
-- selects an for a specific environment by its key
select id 
from environments
where key = :key
