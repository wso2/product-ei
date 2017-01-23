CREATE table company(name varchar(10) primary key, id varchar(10), price double);

INSERT into company values ('IBM','c1',0.0);
INSERT into company values ('SUN','c2',0.0);
INSERT into company values ('MSFT','c3',0.0);

CREATE PROCEDURE getCompany(compName VARCHAR(10)) SELECT name, id, price FROM company WHERE name = compName;
CREATE PROCEDURE updateCompany(compPrice DOUBLE,compName VARCHAR(10)) UPDATE company SET price = compPrice WHERE name = compName;