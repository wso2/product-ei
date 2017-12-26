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

CREATE UNIQUE INDEX IF NOT EXISTS customers_pk ON Customers( customerNumber );

CREATE VIEW USACustomers AS SELECT * FROM Customers WHERE country = 'USA';

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

CREATE UNIQUE INDEX IF NOT EXISTS employees_pk ON Employees( employeeNumber );

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

CREATE UNIQUE INDEX IF NOT EXISTS offices_pk ON Offices ( officeCode );

CREATE TABLE IF NOT EXISTS Products(
	productCode VARCHAR(15),
	productName VARCHAR(70),
	productLine VARCHAR(50),
	productScale VARCHAR(10),
	productVendor VARCHAR(50),
	productDescription LONGVARCHAR,
	quantityInStock INTEGER,
	buyPrice DOUBLE,
	MSRP DOUBLE
);
CREATE UNIQUE INDEX IF NOT EXISTS products_pk ON Products( productCode );

CREATE TABLE IF NOT EXISTS ProductLines(
	productLine VARCHAR(50),
	textDescription VARCHAR(4000),
	htmlDescription VARCHAR(4000),
	image BLOB
);
CREATE UNIQUE INDEX IF NOT EXISTS productLines_pk on ProductLines( productLine );

CREATE TABLE IF NOT EXISTS Orders(
	orderNumber INTEGER,
	orderDate DATE,
	requiredDate DATE,
	shippedDate DATE,
	status VARCHAR(15),
	comments LONGVARCHAR,
	customerNumber INTEGER 
);
CREATE UNIQUE INDEX IF NOT EXISTS orders_pk ON Orders( orderNumber );
CREATE INDEX orders_cutomer ON Orders( customerNumber );

CREATE TABLE IF NOT EXISTS OrderDetails(
	orderNumber INTEGER,
	productCode VARCHAR(15),
	quantityOrdered INTEGER,
	priceEach DOUBLE,
	orderLineNumber SMALLINT);
CREATE UNIQUE INDEX IF NOT EXISTS orderDetails_pk ON OrderDetails( orderNumber, productCode );

CREATE TABLE IF NOT EXISTS Payments(
	customerNumber INTEGER,
	checkNumber VARCHAR(50),
	paymentDate DATE,
	amount DOUBLE 
);
CREATE UNIQUE INDEX IF NOT EXISTS payments_pk ON Payments( customerNumber, checkNumber );

CREATE TABLE IF NOT EXISTS department(
	id INTEGER,
	name VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS Files(
	fileName VARCHAR(300),
        type VARCHAR(50),
        PRIMARY KEY (fileName)
);
CREATE UNIQUE INDEX IF NOT EXISTS files_pk ON Files( fileName );

CREATE TABLE IF NOT EXISTS FileRecords(
        fileRecordId INTEGER NOT NULL AUTO_INCREMENT,
        data BLOB,
        fileName VARCHAR(300),
        PRIMARY KEY (fileRecordId),
        FOREIGN KEY (fileName) REFERENCES Files(fileName) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS files_record_pk ON FileRecords( fileRecordId );

CREATE TABLE IF NOT EXISTS Accounts(
        accountId INTEGER NOT NULL AUTO_INCREMENT,
        balance DOUBLE,
        PRIMARY KEY (accountId),
);
CREATE UNIQUE INDEX IF NOT EXISTS account_pk ON Accounts( accountId );


