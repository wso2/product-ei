DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS OrderDetails;

CREATE TABLE IF NOT EXISTS Products(
	productCode VARCHAR(15),
	productName VARCHAR(70),
	productLine VARCHAR(50),
	productDescription VARCHAR(500),
	quantityInStock INTEGER
);
CREATE UNIQUE INDEX products_pk ON Products( productCode );

CREATE TABLE IF NOT EXISTS Orders(
	orderNumber INTEGER,
	productCode VARCHAR(15),
	productName VARCHAR(70),
	productLine VARCHAR(50),
	comments VARCHAR(500)
);
CREATE UNIQUE INDEX  orders_pk ON Orders( orderNumber );