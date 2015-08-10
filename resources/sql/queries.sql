--name:save-service-check!
-- inserts a row into service_check
insert into service_checks (id, environment_id, description, updated_date, status)
values (:id, :environment_id, :description, :updated_date, :status)

--name:get-environments
-- selects all environments
select * from environments

--name:get-service-checks
-- selects all services check results
select  e.ID as ENVIRONMENT_ID,
        e.NAME,
        sc.DESCRIPTION,
        sc.UPDATED_DATE,
        sc.STATUS
from service_checks sc,
     environments e
where sc.ENVIRONMENT_ID = e.ID

--name:delete-service-checks!
-- deletes all environments
delete from service_checks 


