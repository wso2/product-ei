DROP TABLE IF EXISTS EmailUsers;

CREATE TABLE IF NOT EXISTS EmailUsers(
	employeeNumber INTEGER,
	firstname VARCHAR(50),
	lastName VARCHAR(50),
	email VARCHAR(50),
	salary VARCHAR(50)
);

CREATE UNIQUE INDEX  employeeNumber_pk ON EmailUsers( employeeNumber );


insert into EmailUsers values (1,'Atelier','Schmitt','atelier@wso2.com','5000');
insert into EmailUsers values (2,'Signal','King','signal@wso2.com','7000');
insert into EmailUsers values (3,'Jean','King','Jean@wso2.com','6000');