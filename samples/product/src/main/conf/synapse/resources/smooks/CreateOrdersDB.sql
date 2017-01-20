DROP DATABASE IF EXISTS OrdersDB;
create database OrdersDB;
grant all on OrdersDB.* to 'smooksuser'@'localhost' identified by 'wso2';
grant all on OrdersDB.* to 'smooksuser'@'%' identified by 'wso2';

use OrdersDB;

CREATE TABLE ORDERITEMS (
  itemId int(11),
  orderId int(11),
  productId int(11),
  quantity int(11),
  price double
);
CREATE TABLE ORDERS (
  orderId int (11),
  customerNumber int(11),
  cutomerName varchar(30)
);

