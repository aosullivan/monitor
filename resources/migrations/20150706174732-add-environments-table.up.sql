CREATE TABLE IF NOT EXISTS environments
(id VARCHAR(20) PRIMARY KEY,
 name VARCHAR(30),
 status VARCHAR(10));
--;;
 INSERT INTO environments (id, name, status) 
 VALUES (1, 'CouncelLink QA6', 'OK');
--;;
 INSERT INTO environments (id, name, status) 
 VALUES (2, 'CouncelLink QA9', 'OK');
