CREATE TABLE PersonNullTest(
  PersonID int,
  LastName varchar(255),
  City int,
  Weight double
  );

insert into PersonNullTest values (null, 'WSO2 Inc.', null, null);