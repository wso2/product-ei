CREATE DATABASE  IF NOT EXISTS `OrderManagementRegistry`;
USE OrderManagementRegistry;
DROP TABLE IF EXISTS `OrderItem`;
DROP TABLE IF EXISTS `Order`;

CREATE TABLE `Order` (
  `orderid` int NOT NULL AUTO_INCREMENT,
  `totalprice` int NULL,
  `orderstatus` varchar(20) NOT NULL,
  `paymentStatus` varchar(20) NOT NULL,
  PRIMARY KEY (`orderId`)
);

CREATE TABLE `OrderItem` (
  `orderItemId` int NOT NULL AUTO_INCREMENT,
  `orderId` int NULL,
  `drinkName` varchar(50) NOT NULL,
  `additions` varchar(500),
  `cost` int NOT NULL,
  PRIMARY KEY (`orderItemId`),
  FOREIGN KEY (`orderId`) REFERENCES `Order`(`orderId`) ON DELETE CASCADE
);