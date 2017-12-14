CREATE DATABASE  IF NOT EXISTS `VehicleRegistry`;
USE `VehicleRegistry`;

-- Table structure for table `LicenseDetails`
DROP TABLE IF EXISTS `LicenseDetails`;
CREATE TABLE `LicenseDetails` (
  `VehicleNumber` varchar(45) NOT NULL,
  `LicenseExpiry` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`VehicleNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `LicenseDetails` WRITE;
INSERT INTO `LicenseDetails` VALUES ('CAV-5527','05-02-2018'),('HI-4257','02-01-2018'),('KP-6531','02-04-2018'),('KW-2577','10-10-2018');
UNLOCK TABLES;

-- Table structure for table `LicenseFees`
DROP TABLE IF EXISTS `LicenseFees`;
CREATE TABLE `LicenseFees` (
  `VehicleClass` varchar(45) NOT NULL,
  `LicenseFee` int(11) DEFAULT NULL,
  PRIMARY KEY (`VehicleClass`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `LicenseFees` WRITE;
INSERT INTO `LicenseFees` VALUES ('MOTOR_BICYCLE',10),('MOTOR_CAR',30),('MOTOR_LORY',60),('PASSENGER_BUS',100),('PASSENGER_VAN',60);
UNLOCK TABLES;

-- Table structure for table `VehicleDetails`
DROP TABLE IF EXISTS `VehicleDetails`;
CREATE TABLE `VehicleDetails` (
  `VehicleNumber` varchar(45) NOT NULL,
  `EngineNumber` varchar(45) DEFAULT NULL,
  `VehicleClass` varchar(45) DEFAULT NULL,
  `Make` varchar(45) DEFAULT NULL,
  `Model` varchar(45) DEFAULT NULL,
  `YOM` int(11) DEFAULT NULL,
  PRIMARY KEY (`VehicleNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `VehicleDetails` WRITE;
INSERT INTO `VehicleDetails` VALUES ('CAV-5527','1NZ-821376','MOTOR_CAR','TOYOTA','PREMIO',2017),('HI-4257','QG15-129791','MOTOR_LORY','ISUZU','ELF',2000),('KP-6531','1NZ-112908','PASSENGER_VAN','NISSAN','CARAVAN',2007),('KW-2577','1NZ-318989','MOTOR_CAR','TOYOTA','ALLION',2013);
UNLOCK TABLES;