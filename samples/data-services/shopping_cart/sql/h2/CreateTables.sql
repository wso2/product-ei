CREATE TABLE IF NOT EXISTS Customer(
	customerEmail VARCHAR(200),
	customerName VARCHAR(200),
	phone VARCHAR(50),
	addressLine1 VARCHAR(50),
	addressLine2 VARCHAR(50),
	city VARCHAR(50),
	state VARCHAR(50),
	postalCode VARCHAR(15),
	country VARCHAR(50),
        PRIMARY KEY (customerEmail)
);

CREATE TABLE IF NOT EXISTS Category(
	categoryName VARCHAR(50),
	description LONGVARCHAR,
	image BLOB,
        PRIMARY KEY (categoryName)
);

CREATE TABLE IF NOT EXISTS Product(
	productCode VARCHAR(15),
	productName VARCHAR(70),
	categoryName VARCHAR(50),
	productVendor VARCHAR(50),
	productDescription LONGVARCHAR,
	quantityInStock INTEGER,
	price DOUBLE,
        reorderLevel INTEGER,
        reorderQuantity INTEGER,
	image BLOB,
        PRIMARY KEY (productCode),
        FOREIGN KEY (categoryName) REFERENCES Category(categoryName)
);

CREATE TABLE IF NOT EXISTS OrderEntry(
	orderNumber INTEGER AUTO_INCREMENT,
	orderDate DATE,
	requiredDate DATE,
	shippedDate DATE,
	status VARCHAR(15),
	comments LONGVARCHAR,
	customerEmail VARCHAR(200),
        PRIMARY KEY (orderNumber),
        FOREIGN KEY (customerEmail) REFERENCES Customer(customerEmail)
);
CREATE INDEX IF NOT EXISTS Order_customer ON OrderEntry( customerEmail );

CREATE TABLE IF NOT EXISTS OrderItem(
	orderNumber INTEGER,
	productCode VARCHAR(15),
	quantityOrdered INTEGER,
	PRIMARY KEY (orderNumber,productCode),
        FOREIGN KEY (orderNumber) references OrderEntry(orderNumber),
        FOREIGN KEY (productCode) references Product(productCode)
);

CREATE TABLE IF NOT EXISTS Payment(
	orderNumber INTEGER,
	checkNumber VARCHAR(50),
	paymentDate DATE,
	amount DOUBLE,
        PRIMARY KEY (orderNumber,checkNumber),
        FOREIGN KEY (orderNumber) REFERENCES OrderEntry(orderNumber)
);

