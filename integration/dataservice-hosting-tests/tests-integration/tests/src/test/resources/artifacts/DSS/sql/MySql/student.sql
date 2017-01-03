CREATE TABLE IF NOT EXISTS STUDENT_T (
             REGISTRATIION_NUMBER_C VARCHAR (50),
             NAME_C VARCHAR (100),
             EMAIL_C VARCHAR (100),
             AGE_C INT,
             CLASS_C varchar(50),
             AVERAGE_C DOUBLE,
             PRIMARY KEY (REGISTRATIION_NUMBER_C)
)ENGINE INNODB;

insert into STUDENT_T values ('001', 'rasika','rasika@gmail.com', 15, '7B', 73.90);
insert into STUDENT_T values ('002', 'kamal', 'kamal@gmail.com',15, '7C', 80.56);

