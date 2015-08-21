--name:save-service-check<!
-- inserts a row into service_check
insert into service_checks (environment_id, description, updated_date, status)
values (:environment_id, :description, :updated_date, :status)

--name:get-environments
-- selects all environments
select * from environments

--name:get-service-checks
-- selects all services check results
select  e.ID as ENVIRONMENT_ID,
        sc.ID as SERVICE_CHECK_ID,
        e.NAME as ENVIRONMENT_NAME,
        sc.DESCRIPTION as SERVICE_CHECK_DESCRIPTION,
        sc.UPDATED_DATE,
        sc.STATUS
from service_checks sc,
     environments e
where sc.ENVIRONMENT_ID = e.ID
order by e.ID, sc.ID

--name:delete-service-checks!
-- deletes all environments
delete from service_checks 

--name:reset-service-check-counter!
-- reset the auto increment counter
ALTER TABLE service_checks ALTER COLUMN id RESTART WITH 1


