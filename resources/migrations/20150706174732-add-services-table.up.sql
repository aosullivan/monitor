CREATE TABLE IF NOT EXISTS services
(id VARCHAR(20) PRIMARY KEY,
 name VARCHAR(30),
 url VARCHAR(100),
 status VARCHAR(10));
--;;
 INSERT INTO services (id, name, url, status) 
 VALUES (1, 'CouncelLink QA6', 'q6cl.examen.com/login/login.jsp?force', 'OK');
 --;;
 INSERT INTO services (id, name, url, status) 
 VALUES (2, 'CouncelLink QA8', 'q8cl.examen.com/login/login.jsp?force', 'OK'); 