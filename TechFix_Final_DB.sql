-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: techfix_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
  `BrandID` int NOT NULL AUTO_INCREMENT,
  `BrandName` varchar(50) NOT NULL,
  `OriginCountry` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`BrandID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` VALUES (1,'Dell','USA'),(2,'HP','USA'),(3,'Apple','USA'),(4,'Samsung','Korea'),(5,'Lenovo','China'),(6,'Asus','Taiwan'),(7,'Sony','Japan');
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `city` (
  `CityID` int NOT NULL AUTO_INCREMENT,
  `CityName` varchar(50) NOT NULL,
  `ZipCode` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`CityID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'Nablus','00970'),(2,'Ramallah','00972'),(3,'Jenin','00971'),(4,'Hebron','00973'),(5,'Tulkarm','00974');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `CustomerID` int NOT NULL AUTO_INCREMENT,
  `FullName` varchar(100) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `Phone` varchar(15) NOT NULL,
  `Address` varchar(255) DEFAULT NULL,
  `CityID` int DEFAULT NULL,
  `IsActive` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`CustomerID`),
  UNIQUE KEY `Phone` (`Phone`),
  KEY `CityID` (`CityID`),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`CityID`) REFERENCES `city` (`CityID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Ahmad Masri','Ahmad','Masri','0599111222','Rafidia Main St',1,1),(2,'Sara Zaid','Sara','Zaid','0568222333','Al-Masyoun',2,1),(3,'Tariq Jabari','Tariq','Jabari','0597333444','Ein Sara',4,1),(4,'Lina Awad','Lina','Awad','0595444555','Haifa St',3,1),(5,'Mahmoud Saleh','Mahmoud','Saleh','0569555666','Shweika',5,1),(6,'Yasmine Nader','Yasmine','Nader','0599666777','Al-Irsal St',2,1),(7,'Omar Kanaan','Omar','Kanaan','0598777888','Old City',1,1),(8,'Hala Qasim','Hala','Qasim','0568888999','University St',1,1),(9,'Ibrahim Salem','Ibrahim','Salem','0599999000','Al-Manara',2,1),(10,'Karam Saif','Karam','Saif','0567111222','Wadi Tuffah',4,1),(11,'Rami Jaber','Rami','Jaber','0599222333','Makhfiya',1,1),(12,'Suha Nabil','Suha','Nabil','0599333444','Iktaba',5,1),(13,'Majd Hasan','Majd','Hasan','0569444555','Jenin Camp',3,1),(14,'Dana Fouad','Dana','Fouad','0599555666','Al-Makhfiya',1,1),(15,'Ziad Ammar','Ziad','Ammar','0599666111','Al-Tira',2,1);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `DeviceID` int NOT NULL AUTO_INCREMENT,
  `SerialNo` varchar(50) NOT NULL,
  `Model` varchar(100) NOT NULL,
  `Color` varchar(30) DEFAULT NULL,
  `DevicePIN` varchar(20) DEFAULT NULL,
  `CustomerID` int DEFAULT NULL,
  `BrandID` int DEFAULT NULL,
  `TypeID` int DEFAULT NULL,
  `Accessories` varchar(255) DEFAULT 'None',
  PRIMARY KEY (`DeviceID`),
  UNIQUE KEY `SerialNo` (`SerialNo`),
  KEY `CustomerID` (`CustomerID`),
  KEY `BrandID` (`BrandID`),
  KEY `TypeID` (`TypeID`),
  CONSTRAINT `device_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`CustomerID`),
  CONSTRAINT `device_ibfk_2` FOREIGN KEY (`BrandID`) REFERENCES `brand` (`BrandID`),
  CONSTRAINT `device_ibfk_3` FOREIGN KEY (`TypeID`) REFERENCES `device_type` (`TypeID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (1,'SN-1001','Legion 5 Pro','Gray','1234',1,5,1,'Charger, Laptop Bag'),(2,'SN-1002','iPhone 13 Pro','Blue','000000',2,3,2,'Clear Case, Original Box'),(3,'SN-1003','Galaxy S23 Ultra','Black',NULL,3,4,2,'Clear Case, Original Box'),(4,'SN-1004','MacBook Air M1','Silver','1122',4,3,1,'Charger, Laptop Bag'),(5,'SN-1005','Inspiron 15 3000','Black','2580',5,1,1,'Charger, Laptop Bag'),(6,'SN-1006','PlayStation 5','White',NULL,6,7,4,'1 Controller, Power Cable'),(7,'SN-1007','iPad Pro 11','Space Gray','147258',7,3,3,'Stylus Pen, Cover'),(8,'SN-1008','TUF Gaming F15','Black','1234',8,6,1,'Charger, Laptop Bag'),(9,'SN-1009','Pavilion 15','Silver','8899',9,2,1,'Charger, Laptop Bag'),(10,'SN-1010','Galaxy Tab S8','Gray','0000',10,4,3,'Stylus Pen, Cover'),(11,'SN-1011','Custom PC Build','RGB Black','1111',11,6,5,'None'),(12,'SN-1012','iPhone 14','Purple','123123',12,3,2,'Clear Case, Original Box'),(13,'SN-1013','ThinkPad T14','Black','5566',13,5,1,'Charger, Laptop Bag'),(14,'SN-1014','XPS 13','Silver',NULL,14,1,1,'Charger, Laptop Bag'),(15,'SN-1015','ROG Zephyrus','White','0987',15,6,1,'Charger, Laptop Bag');
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_type`
--

DROP TABLE IF EXISTS `device_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_type` (
  `TypeID` int NOT NULL AUTO_INCREMENT,
  `TypeName` varchar(50) NOT NULL,
  PRIMARY KEY (`TypeID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_type`
--

LOCK TABLES `device_type` WRITE;
/*!40000 ALTER TABLE `device_type` DISABLE KEYS */;
INSERT INTO `device_type` VALUES (1,'Laptop'),(2,'Smartphone'),(3,'Tablet'),(4,'Console'),(5,'Desktop');
/*!40000 ALTER TABLE `device_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `ItemID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`ItemID`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'RAM 8GB DDR4',35.00),(2,'RAM 16GB DDR4',60.00),(3,'RAM 16GB DDR5',110.00),(4,'SSD 256GB NVMe',45.00),(5,'SSD 512GB NVMe',75.00),(6,'SSD 1TB NVMe',130.00),(7,'SSD 2TB NVMe',250.00),(8,'Laptop Screen 15.6\" IPS',120.00),(9,'Smartphone OLED Screen Panel',180.00),(10,'Laptop Battery - Standard',60.00),(11,'Smartphone Battery',40.00),(12,'Cooling Fan Replacement',25.00),(13,'Charging Port Module',20.00),(14,'High-End Thermal Paste',15.00),(15,'OS Installation (Windows/Mac)',30.00),(16,'Deep Cleaning & Repaste',45.00),(17,'Basic Device Diagnostic',15.00),(18,'Motherboard IC Micro-Soldering',120.00),(19,'Basic Data Recovery',75.00),(20,'Advanced Data Recovery',250.00),(21,'Screen Protector & Apply',10.00),(22,'MacBook Replacement Battery (Air/Pro)',110.00),(23,'Laptop Replacement Keyboard (Various)',35.00),(24,'Type-C 65W Universal Laptop Charger',35.00),(25,'iPhone Charging IC (U2 Tristar)',25.00),(26,'Laptop Hinge Replacement Set',25.00),(27,'CMOS Battery (CR2032)',5.00),(28,'Hard Drive Caddy 9.5mm',15.00),(29,'Thermal Pads for GPU/CPU',12.00);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job_item`
--

DROP TABLE IF EXISTS `job_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_item` (
  `JobID` int NOT NULL,
  `ItemID` int NOT NULL,
  `Quantity` int DEFAULT '1',
  PRIMARY KEY (`JobID`,`ItemID`),
  KEY `ItemID` (`ItemID`),
  CONSTRAINT `job_item_ibfk_1` FOREIGN KEY (`JobID`) REFERENCES `maintenance_job` (`JobID`),
  CONSTRAINT `job_item_ibfk_2` FOREIGN KEY (`ItemID`) REFERENCES `item` (`ItemID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_item`
--

LOCK TABLES `job_item` WRITE;
/*!40000 ALTER TABLE `job_item` DISABLE KEYS */;
INSERT INTO `job_item` VALUES (1,2,1),(1,3,1),(2,9,1),(3,12,1),(3,16,1),(4,15,1),(4,19,1),(5,7,1),(5,16,1),(7,10,1),(8,7,1),(8,12,6),(8,15,1),(8,16,1),(9,6,1),(9,15,1),(11,14,1);
/*!40000 ALTER TABLE `job_item` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `UpdateTotalCost` AFTER INSERT ON `job_item` FOR EACH ROW BEGIN
    DECLARE item_price DECIMAL(10, 2);
    SELECT Price INTO item_price FROM `Item` WHERE ItemID = NEW.ItemID;
    UPDATE `Maintenance_Job`
    SET TotalCost = IFNULL(TotalCost, 0) + (item_price * NEW.Quantity)
    WHERE JobID = NEW.JobID;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `maintenance_job`
--

DROP TABLE IF EXISTS `maintenance_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maintenance_job` (
  `JobID` int NOT NULL AUTO_INCREMENT,
  `ProblemDescription` text,
  `DateIn` datetime DEFAULT CURRENT_TIMESTAMP,
  `DateOut` datetime DEFAULT NULL,
  `TotalCost` decimal(10,2) DEFAULT '0.00',
  `DeviceID` int DEFAULT NULL,
  `UserID` int DEFAULT NULL,
  `StatusID` int DEFAULT NULL,
  PRIMARY KEY (`JobID`),
  KEY `DeviceID` (`DeviceID`),
  KEY `UserID` (`UserID`),
  KEY `StatusID` (`StatusID`),
  CONSTRAINT `maintenance_job_ibfk_1` FOREIGN KEY (`DeviceID`) REFERENCES `device` (`DeviceID`),
  CONSTRAINT `maintenance_job_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `user` (`UserID`),
  CONSTRAINT `maintenance_job_ibfk_3` FOREIGN KEY (`StatusID`) REFERENCES `status` (`StatusID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_job`
--

LOCK TABLES `maintenance_job` WRITE;
/*!40000 ALTER TABLE `maintenance_job` DISABLE KEYS */;
INSERT INTO `maintenance_job` VALUES (1,'Upgrade storage and RAM','2024-04-01 10:00:00','2024-04-02 12:00:00',260.00,1,2,5),(2,'Broken screen needs replacement','2024-04-02 11:30:00','2024-04-04 14:00:00',260.00,2,2,5),(3,'Overheating and shutting down','2024-04-05 09:15:00',NULL,90.00,8,2,4),(4,'Format and backup files','2024-04-06 14:20:00',NULL,150.00,4,2,4),(5,'Keyboard keys not working','2026-05-01 10:00:00','2026-05-12 00:00:00',295.00,5,2,5),(6,'HDMI port broken on PS5','2026-05-02 16:45:00',NULL,0.00,6,1,2),(7,'Battery drains in 1 hour','2026-05-03 11:00:00','2026-05-05 00:00:00',90.00,14,2,5),(8,'Dead completely, no signs of life','2026-05-04 09:00:00','2026-05-05 00:00:00',375.00,11,2,5),(9,'Upgrade to 1TB SSD','2026-05-04 12:30:00','2026-05-05 10:00:00',270.00,13,2,5),(10,'Stuck on Apple Logo','2026-05-05 08:30:00',NULL,0.00,12,2,1),(11,'Needs screen protector and checkup','2026-05-05 14:00:00','2026-05-06 00:00:00',10.00,3,2,4),(12,'Fans making grinding noise','2026-05-05 15:30:00',NULL,0.00,15,2,1);
/*!40000 ALTER TABLE `maintenance_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `part`
--

DROP TABLE IF EXISTS `part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `part` (
  `ItemID` int NOT NULL,
  `StockQuantity` int DEFAULT '0',
  `WarrantyPeriod` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ItemID`),
  CONSTRAINT `part_ibfk_1` FOREIGN KEY (`ItemID`) REFERENCES `item` (`ItemID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `part`
--

LOCK TABLES `part` WRITE;
/*!40000 ALTER TABLE `part` DISABLE KEYS */;
INSERT INTO `part` VALUES (1,20,'1 Year'),(2,15,'1 Year'),(3,10,'1 Year'),(4,25,'3 Years'),(5,30,'3 Years'),(6,15,'3 Years'),(7,3,'3 Years'),(8,8,'6 Months'),(9,5,'6 Months'),(10,12,'6 Months'),(11,20,'6 Months'),(12,9,'1 Month'),(13,30,'1 Month'),(14,49,'None'),(22,5,NULL),(23,10,NULL),(24,15,NULL),(25,20,NULL),(26,8,NULL),(27,50,NULL),(28,12,NULL),(29,25,NULL);
/*!40000 ALTER TABLE `part` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `JobID` int NOT NULL,
  `PaymentID` int NOT NULL AUTO_INCREMENT,
  `Amount` decimal(10,2) NOT NULL,
  `PaymentDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `Method` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`PaymentID`),
  KEY `JobID` (`JobID`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`JobID`) REFERENCES `maintenance_job` (`JobID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,130.00,'2024-04-02 12:05:00','Cash'),(2,2,130.00,'2024-04-04 14:10:00','Visa'),(9,3,135.00,'2026-05-05 10:15:00','Cash'),(5,5,295.00,'2026-05-12 00:00:00','Cash'),(7,7,90.00,'2026-05-05 00:00:00','Cash'),(8,8,375.00,'2026-05-05 00:00:00','Cash');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `ReviewID` int NOT NULL AUTO_INCREMENT,
  `Rating` int DEFAULT NULL,
  `Comment` text,
  `ReviewDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `JobID` int DEFAULT NULL,
  PRIMARY KEY (`ReviewID`),
  UNIQUE KEY `JobID` (`JobID`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`JobID`) REFERENCES `maintenance_job` (`JobID`),
  CONSTRAINT `review_chk_1` CHECK ((`Rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,5,'Upgraded my laptop to SSD, it flies now! Thanks!','2024-04-03 10:00:00',1),(2,4,'Screen replacement was fast, a bit expensive though.','2024-04-05 11:00:00',2);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `RoleID` int NOT NULL AUTO_INCREMENT,
  `RoleName` varchar(50) NOT NULL,
  `Description` text,
  PRIMARY KEY (`RoleID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Manager','Full System Access'),(2,'Technician','Repairs & Updates'),(3,'Receptionist','Front Desk & Customer Entry'),(4,'Manager','Shop Owner / Super Admin');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service` (
  `ItemID` int NOT NULL,
  `EstimatedTime` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ItemID`),
  CONSTRAINT `service_ibfk_1` FOREIGN KEY (`ItemID`) REFERENCES `item` (`ItemID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (15,'2 Hours'),(16,'1 Hour'),(17,'30 Mins'),(18,'3 Days'),(19,'2 Days'),(20,'5 Days'),(21,'15 Mins');
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status` (
  `StatusID` int NOT NULL AUTO_INCREMENT,
  `StatusName` varchar(50) NOT NULL,
  PRIMARY KEY (`StatusID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status`
--

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` VALUES (1,'Pending'),(2,'In Progress'),(3,'Waiting for Parts'),(4,'Completed'),(5,'Delivered'),(6,'Cancelled');
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
  `SupplierID` int NOT NULL AUTO_INCREMENT,
  `SupplierName` varchar(100) NOT NULL,
  `ContactPhone` varchar(20) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`SupplierID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` VALUES (1,'BCI Mobile','+970-2-294-6600','info.ps@bci.tech'),(2,'Akram Sbitany & Sons','1-700-550-110','info@sbitany.com'),(3,'Mega Technology','+970-2-297-6060','info@megatech.ps'),(4,'Click Computer & Mobile','+970-59-998-6680','click-service@live.com'),(5,'BIS Distribution','+970-2-295-0717','info@bis-distribution.com'),(6,'Watani Mall','+970-9-238-6666','info@watanimall.ps');
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier_part`
--

DROP TABLE IF EXISTS `supplier_part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier_part` (
  `SupplierID` int NOT NULL,
  `ItemID` int NOT NULL,
  PRIMARY KEY (`SupplierID`,`ItemID`),
  KEY `ItemID` (`ItemID`),
  CONSTRAINT `supplier_part_ibfk_1` FOREIGN KEY (`SupplierID`) REFERENCES `supplier` (`SupplierID`),
  CONSTRAINT `supplier_part_ibfk_2` FOREIGN KEY (`ItemID`) REFERENCES `part` (`ItemID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier_part`
--

LOCK TABLES `supplier_part` WRITE;
/*!40000 ALTER TABLE `supplier_part` DISABLE KEYS */;
INSERT INTO `supplier_part` VALUES (3,1),(4,1),(6,1),(3,2),(6,2),(4,3),(6,3),(3,4),(4,4),(6,4),(3,5),(6,5),(5,6),(6,6),(3,7),(4,7),(6,7),(5,8),(6,8),(1,9),(2,9),(3,10),(6,10),(1,11),(2,11),(4,12),(6,12),(1,13),(4,14),(6,14),(5,22),(6,22),(3,23),(5,23),(3,24),(4,24),(6,24),(1,25),(4,25),(3,26),(4,26),(3,27),(4,27),(3,28),(4,28),(4,29),(6,29);
/*!40000 ALTER TABLE `supplier_part` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) DEFAULT NULL,
  `FullName` varchar(100) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `HireDate` date DEFAULT NULL,
  `RoleID` int DEFAULT NULL,
  `IsActive` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `Username` (`Username`),
  UNIQUE KEY `Email` (`Email`),
  KEY `RoleID` (`RoleID`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`RoleID`) REFERENCES `role` (`RoleID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Ahmad Khalil','Ahmad','Khalil','manager@tf.com','2023-01-01',4,1),(2,'tech1','3ac40463b419a7de590185c7121f0bfbe411d6168699e8014f521b050b1d6653','Sami Yousef','Sami','Yousef','sami@tf.com','2023-03-15',2,1),(3,'recep1','5d37ed314cf2b5c8462b52b12cd512e2ac4a180e75598da4f12bfb0dea6d0a67','Lara Nasser','Lara','Nasser','lara@tf.com','2023-06-01',3,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'techfix_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-12 19:16:46
