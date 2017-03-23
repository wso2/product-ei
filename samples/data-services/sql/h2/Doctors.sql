CREATE TABLE IF NOT EXISTS Doctors(
	name VARCHAR(50),
	hospital VARCHAR(50),
	speciality VARCHAR(50),
	availability VARCHAR(50),
	charge INTEGER(10)
);

insert into Doctors values ('thomas collins', 'grand oak community hospital', 'surgery', '9.00 a.m - 11.00 a.m', 7000);
insert into Doctors values ('henry parker', 'grand oak community hospital', 'ent', '9.00 a.m - 11.00 a.m', 4500);
insert into Doctors values ('abner jones', 'grand oak community hospital', 'gynaecology', '8.00 a.m - 10.00 a.m', 11000);
insert into Doctors values ('abner jones', 'grand oak community hospital', 'ent', '8.00 a.m - 10.00 a.m', 6750);
insert into Doctors values ('anne clement', 'clemency medical center', 'surgery', '8.00 a.m - 10.00 a.m', 12000);
insert into Doctors values ('thomas kirk', 'clemency medical center', 'gynaecology', '9.00 a.m - 11.00 a.m', 8000);
insert into Doctors values ('cailen cooper', 'clemency medical center', 'paediatric', '9.00 a.m - 11.00 a.m', 5500);
insert into Doctors values ('seth mears', 'pine valley community hospital', 'surgery', '3.00 p.m - 5.00 p.m', 8000);
insert into Doctors values ('emeline fulton', 'pine valley community hospital', 'cardiology', '8.00 a.m - 10.00 a.m', 4000);
insert into Doctors values ('jared morris', 'willow gardens general hospital', 'cardiology', '9.00 a.m - 11.00 a.m', 10000);
insert into Doctors values ('henry foster', 'willow gardens general hospital', 'paediatric', '8.00 a.m - 10.00 a.m', 10000);