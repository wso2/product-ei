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

CREATE TABLE IF NOT EXISTS Student(
        studentId INTEGER NOT NULL AUTO_INCREMENT,
        firstName  VARCHAR(300),
        lastName  VARCHAR(300),
        PRIMARY KEY (studentId)
);

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
	creditLimit DOUBLE,
	PRIMARY KEY (customerNumber)
);

CREATE UNIQUE INDEX  customers_pk ON Customers( customerNumber );

CREATE VIEW USACustomers AS SELECT * FROM Customers WHERE country = 'USA';
