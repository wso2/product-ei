CREATE TABLE IF NOT EXISTS Customer(
	customerEmail VARCHAR(200) NOT NULL,
	customerName VARCHAR(200),
	phone VARCHAR(50),
	addressLine1 VARCHAR(50),
	addressLine2 VARCHAR(50),
	city VARCHAR(50),
	state VARCHAR(50),
	postalCode VARCHAR(15),
	country VARCHAR(50),
        CONSTRAINT customerEmailKey UNIQUE (customerEmail)
) ENGINE="InnoDB";

CREATE TABLE IF NOT EXISTS Category(
	categoryName VARCHAR(50) NOT NULL,
	description TEXT,
	image BLOB,
        CONSTRAINT categoryNameKey UNIQUE (categoryName)
) ENGINE="InnoDB";

CREATE TABLE IF NOT EXISTS Product(
	productCode VARCHAR(15),
	productName VARCHAR(70),
	categoryName VARCHAR(50),
	productVendor VARCHAR(50),
	productDescription TEXT,
	quantityInStock INTEGER,
	price DOUBLE,
        reorderLevel INTEGER,
        reorderQuantity INTEGER,
	image BLOB,
        CONSTRAINT productCode UNIQUE (productCode),
        FOREIGN KEY (categoryName) REFERENCES Category(categoryName)
) ENGINE="InnoDB";

CREATE TABLE IF NOT EXISTS OrderEntry(
	orderNumber INTEGER AUTO_INCREMENT,
	orderDate DATE,
	requiredDate DATE,
	shippedDate DATE,
	status VARCHAR(15),
	comments TEXT,
	customerEmail VARCHAR(200),
        PRIMARY KEY (orderNumber),
        FOREIGN KEY (customerEmail) REFERENCES Customer(customerEmail)
) ENGINE="InnoDB";

CREATE TABLE IF NOT EXISTS OrderItem(
	orderNumber INTEGER,
	productCode VARCHAR(15),
	quantityOrdered INTEGER,
	PRIMARY KEY (orderNumber,productCode),
        FOREIGN KEY (orderNumber) references OrderEntry(orderNumber),
        FOREIGN KEY (productCode) references Product(productCode)
) ENGINE="InnoDB";

CREATE TABLE IF NOT EXISTS Payment(
	orderNumber INTEGER,
	checkNumber VARCHAR(50),
	paymentDate DATE,
	amount DOUBLE,
        PRIMARY KEY (orderNumber,checkNumber),
        FOREIGN KEY (orderNumber) REFERENCES OrderEntry(orderNumber)
) ENGINE="InnoDB";

CREATE INDEX Order_customer ON OrderEntry( customerEmail );
