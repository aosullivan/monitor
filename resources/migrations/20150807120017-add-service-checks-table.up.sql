CREATE TABLE IF NOT EXISTS service_checks
(id bigint AUTO_INCREMENT NOT NULL,
 environment_id bigint NOT NULL,
 description VARCHAR(90),
 updated_date TIMESTAMP ,
 status VARCHAR(20),
PRIMARY KEY (id, environment_id),
FOREIGN KEY (environment_id) REFERENCES environments(id));