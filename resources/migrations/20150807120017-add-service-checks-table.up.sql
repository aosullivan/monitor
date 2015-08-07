CREATE TABLE IF NOT EXISTS service_checks
(id VARCHAR(20) NOT NULL,
 environment_id VARCHAR(20) NOT NULL,
 description VARCHAR(90),
 updated_date TIMESTAMP ,
 status VARCHAR(10),
PRIMARY KEY (id, environment_id),
FOREIGN KEY (environment_id) REFERENCES environments(id)); 
