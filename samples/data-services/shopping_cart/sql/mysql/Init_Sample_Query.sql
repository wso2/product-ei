# Run this script to create the MYSQL DB for the shaopping cart sample and populate the DB
# Run this script as follows =>  mysql -u root -proot < ./Init_Sample_Query.sql
drop database if exists shopping_cart_db;

create database shopping_cart_db;

use shopping_cart_db;

source ./CreateDB.sql;

