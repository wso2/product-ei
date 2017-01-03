CREATE TABLE IF NOT EXISTS Customers(
	customerNumber INTEGER,
	customerName VARCHAR(50),
	contactLastName VARCHAR(50),
	contactFirstName VARCHAR(50),
	phone VARCHAR(50),
	addressLine1 VARCHAR(50),
	addressLine2 VARCHAR(50),
	city VARCHAR(50),
	state VARCHAR(50),
	postalCode VARCHAR(15),
	country VARCHAR(50),
	salesRepEmployeeNumber INTEGER,
	creditLimit DOUBLE
);

CREATE UNIQUE INDEX  customers_pk ON Customers( customerNumber );

CREATE TABLE IF NOT EXISTS Employees(
	employeeNumber INTEGER,
	lastName VARCHAR(50),
	firstName VARCHAR(50),
	extension VARCHAR(10),
	email VARCHAR(100),
	officeCode VARCHAR(10),
	reportsTo INTEGER,
	jobTitle VARCHAR(50),
        salary DOUBLE,
        CHECK (salary>=0)
);

CREATE UNIQUE INDEX  employees_pk ON Employees( employeeNumber );

CREATE TABLE IF NOT EXISTS Offices(
	officeCode VARCHAR(10),
	city VARCHAR(50),
	phone VARCHAR(50),
	addressLine1 VARCHAR(50),
	addressLine2 VARCHAR(50),
	state VARCHAR(50),
	country VARCHAR(50),
	postalCode VARCHAR(15),
	territory VARCHAR(10)
);

CREATE UNIQUE INDEX offices_pk ON Offices ( officeCode );

CREATE TABLE IF NOT EXISTS Products(
	productCode VARCHAR(15),
	productName VARCHAR(70),
	productLine VARCHAR(50),
	productScale VARCHAR(10),
	productVendor VARCHAR(50),
	productDescription VARCHAR(500),
	quantityInStock INTEGER,
	buyPrice DOUBLE,
	MSRP DOUBLE
);
CREATE UNIQUE INDEX products_pk ON Products( productCode );

CREATE TABLE IF NOT EXISTS ProductLines(
	productLine VARCHAR(50),
	textDescription VARCHAR(4000),
	htmlDescription VARCHAR(4000),
	image BLOB
);
CREATE UNIQUE INDEX  productLines_pk on ProductLines( productLine );

CREATE TABLE IF NOT EXISTS Orders(
	orderNumber INTEGER,
	orderDate DATE,
	requiredDate DATE,
	shippedDate DATE,
	status VARCHAR(15),
	comments VARCHAR(500),
	customerNumber INTEGER 
);
CREATE UNIQUE INDEX  orders_pk ON Orders( orderNumber );
CREATE INDEX orders_cutomer ON Orders( customerNumber );

CREATE TABLE IF NOT EXISTS OrderDetails(
	orderNumber INTEGER,
	productCode VARCHAR(15),
	quantityOrdered INTEGER,
	priceEach DOUBLE,
	orderLineNumber SMALLINT);
CREATE UNIQUE INDEX  orderDetails_pk ON OrderDetails( orderNumber, productCode );

CREATE TABLE IF NOT EXISTS Payments(
	customerNumber INTEGER,
	checkNumber VARCHAR(50),
	paymentDate DATE,
	amount DOUBLE 
);
CREATE UNIQUE INDEX  payments_pk ON Payments( customerNumber, checkNumber );

CREATE TABLE IF NOT EXISTS department(
	id INTEGER,
	name VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS Files(
	fileName VARCHAR(300),
        type VARCHAR(50),
        PRIMARY KEY (fileName)
);
CREATE UNIQUE INDEX  files_pk ON Files( fileName );

CREATE TABLE IF NOT EXISTS FileRecords(
        fileRecordId INTEGER NOT NULL AUTO_INCREMENT,
        data BLOB,
        fileName VARCHAR(300),
        PRIMARY KEY (fileRecordId),
        FOREIGN KEY (fileName) REFERENCES Files(fileName) ON DELETE CASCADE
);
CREATE UNIQUE INDEX files_record_pk ON FileRecords( fileRecordId );

CREATE TABLE IF NOT EXISTS Accounts(
        accountId INTEGER NOT NULL AUTO_INCREMENT,
        balance DOUBLE,
        PRIMARY KEY (accountId)
);
CREATE UNIQUE INDEX account_pk ON Accounts( accountId );


insert into Employees values (1002,'Murphy','Diane','x5800','dmurphy@classicmodelcars.com','1',null,'President', 2000);
insert into Employees values (1056,'Patterson','Mary','x4611','mpatterso@classicmodelcars.com','1',1002,'VP Sales', 2000);
insert into Employees values (1076,'Firrelli','Jeff','x9273','jfirrelli@classicmodelcars.com','1',1002,'VP Marketing', 2000);
insert into Employees values (1088,'Patterson','William','x4871','wpatterson@classicmodelcars.com','6',1056,'Sales Manager (APAC)', 2000);
insert into Employees values (1102,'Bondur','Gerard','x5408','gbondur@classicmodelcars.com','4',1056,'Sale Manager (EMEA)', 2000);
insert into Employees values (1143,'Bow','Anthony','x5428','abow@classicmodelcars.com','1',1056,'Sales Manager (NA)', 2000);
insert into Employees values (1165,'Jennings','Leslie','x3291','ljennings@classicmodelcars.com','1',1143,'Sales Rep', 2000);
insert into Employees values (1166,'Thompson','Leslie','x4065','lthompson@classicmodelcars.com','1',1143,'Sales Rep', 2000);
insert into Employees values (1188,'Firrelli','Julie','x2173','jfirrelli@classicmodelcars.com','2',1143,'Sales Rep', 2000);
insert into Employees values (1216,'Patterson','Steve','x4334','spatterson@classicmodelcars.com','2',1143,'Sales Rep', 2000);
insert into Employees values (1286,'Tseng','Foon Yue','x2248','ftseng@classicmodelcars.com','3',1143,'Sales Rep', 2000);
insert into Employees values (1323,'Vanauf','George','x4102','gvanauf@classicmodelcars.com','3',1143,'Sales Rep', 2000);
insert into Employees values (1337,'Bondur','Loui','x6493','lbondur@classicmodelcars.com','4',1102,'Sales Rep', 2000);
insert into Employees values (1370,'Hernandez','Gerard','x2028','ghernande@classicmodelcars.com','4',1102,'Sales Rep', 2000);
insert into Employees values (1401,'Castillo','Pamela','x2759','pcastillo@classicmodelcars.com','4',1102,'Sales Rep', 2000);
insert into Employees values (1501,'Bott','Larry','x2311','lbott@classicmodelcars.com','7',1102,'Sales Rep', 2000);
insert into Employees values (1504,'Jones','Barry','x102','bjones@classicmodelcars.com','7',1102,'Sales Rep', 2000);
insert into Employees values (1611,'Fixter','Andy','x101','afixter@classicmodelcars.com','6',1088,'Sales Rep', 2000);
insert into Employees values (1612,'Marsh','Peter','x102','pmarsh@classicmodelcars.com','6',1088,'Sales Rep', 2000);
insert into Employees values (1619,'King','Tom','x103','tking@classicmodelcars.com','6',1088,'Sales Rep', 2000);
insert into Employees values (1621,'Nishi','Mami','x101','mnishi@classicmodelcars.com','5',1056,'Sales Rep', 2000);
insert into Employees values (1625,'Kato','Yoshimi','x102','ykato@classicmodelcars.com','5',1621,'Sales Rep', 2000);
insert into Employees values (1702,'Gerard','Martin','x2312','mgerard@classicmodelcars.com','4',1102,'Sales Rep', 2000);
