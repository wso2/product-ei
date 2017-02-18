
DROP PROCEDURE If EXISTS getEmployeeById;
CREATE PROCEDURE getEmployeeById(IN employeeNo INTEGER) SELECT employeeNumber,lastName,firstName,email FROM Employees where employeeNumber = employeeNo;

DROP PROCEDURE If EXISTS countEmployees;
CREATE PROCEDURE countEmployees(OUT employees INTEGER) SELECT count(*) INTO employees FROM Employees;

DROP PROCEDURE If EXISTS getEmployees;
CREATE PROCEDURE getEmployees() SELECT employeeNumber,lastName,firstName,email FROM Employees;

DROP PROCEDURE If EXISTS insertEmployee;
CREATE PROCEDURE insertEmployee(employeeNo INTEGER, lastName VARCHAR(100), firstName VARCHAR(100), email  VARCHAR(100), salary DOUBLE) insert into Employees (employeeNumber, lastName, firstName, email, salary) values(employeeNo,lastName,firstName,email, salary);










