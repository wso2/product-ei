CREATE TABLE Persons(
  PersonID int,
  LastName varchar(255),
  FirstName varchar(255),
  Address varchar(255),
  Image blob
  );

CREATE TABLE IF NOT EXISTS Students(
	studentNumber INTEGER,
	name VARCHAR(100),
	phone VARCHAR(50),
	state VARCHAR(50),
	country VARCHAR(50)
);

CREATE UNIQUE INDEX students_pk ON Students ( studentNumber );