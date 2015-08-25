CREATE TABLE IF NOT EXISTS environments
(id bigint AUTO_INCREMENT NOT NULL,
 key VARCHAR(10),
 description VARCHAR(30),
 status VARCHAR(10),
PRIMARY KEY (id));

