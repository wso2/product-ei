CREATE TABLE IF NOT EXISTS Students(
	studentNumber INTEGER,
	name VARCHAR(50),
	phone VARCHAR(50),
	state VARCHAR(50),
	country VARCHAR(50)
);

CREATE UNIQUE INDEX students_pk ON Students ( studentNumber );


insert into Students values (001, 'Madhawa Gunasekara','0094719411002', 'CMB', 'LK');
insert into Students values (002, 'Madhawa Kasun','0094112956051', 'NY', 'US');
insert into Students values (003, 'Madhu Madhawa Aravindha','097774546546', 'NY', 'US');
insert into Students values (004, 'Rajith Vitharana','097774546546', 'NY', 'US');
insert into Students values (005, 'Anjana Fernando','097774546546', 'NY', 'US');

