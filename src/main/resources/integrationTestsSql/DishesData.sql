-- --------------------------------------------------------
-- Host:                         database-2.cdad4jowljyd.eu-central-1.rds.amazonaws.com
-- Server version:               8.0.23 - Source distribution
-- Server OS:                    Linux
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for restaurant-app-test
CREATE DATABASE IF NOT EXISTS `restaurant-app-test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restaurant-app-test`;

-- Dumping structure for table restaurant-app-test.dishes
DELETE FROM `dishes`;
ALTER TABLE `dishes` AUTO_INCREMENT = 10;
CREATE TABLE IF NOT EXISTS `dishes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `createdAt` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ready` int NOT NULL,
  `updatedAt` datetime(6) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9i7bdexxjb8go3rndk0ucj4gb` (`order_id`),
  KEY `FK2wdnjopvfbxb792imcc1ngxsl` (`updated_by`),
  CONSTRAINT `FK2wdnjopvfbxb792imcc1ngxsl` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK9i7bdexxjb8go3rndk0ucj4gb` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app-test.dishes: ~0 rows (approximately)
/*!40000 ALTER TABLE `dishes` DISABLE KEYS */;
INSERT INTO `dishes` (`id`, `createdAt`, `name`, `ready`, `updatedAt`, `order_id`, `updated_by`) VALUES
	(1, '2021-10-14 16:00:56.000000', 'burger', 0, '2021-10-14 16:01:12.000000', 1, NULL),
	(2, '2021-10-14 16:01:23.000000', 'pizza', 1, '2021-10-14 16:01:29.000000', 1, 1),
	(3, '2021-10-14 16:01:38.000000', 'salad', 0, '2021-10-14 16:14:10.000000', 1, NULL),
	(4, '2021-10-14 16:14:27.000000', 'sushi', 1, '2021-10-14 16:14:35.000000', 1, 1),
	(5, '2021-10-14 16:14:56.000000', 'pizza', 0, '2021-10-14 16:15:01.000000', 2, NULL),
	(6, '2021-10-14 16:15:10.000000', 'pizza', 1, '2021-10-14 16:15:16.000000', 3, 1),
	(7, '2021-10-14 16:15:27.000000', 'pizza', 1, '2021-10-14 16:15:31.000000', 4, 2),
	(8, '2021-10-14 16:27:58.000000', 'pizza', 0, '2021-10-14 16:27:53.000000', 5, NULL),
	(9, '2021-10-14 16:28:27.000000', 'pizza', 0, '2021-10-14 16:28:26.000000', 6, NULL),
	(10, '2021-10-14 16:31:35.000000', 'pizza', 1, '2021-10-15 16:31:34.000000', 7, 4),
	(11, '2021-10-14 16:34:01.000000', 'pizza', 0, '2021-10-16 16:32:01.000000', 8, NULL),
	(12, '2021-10-14 16:32:40.000000', 'pizza', 1, '2021-10-14 16:32:47.000000', 9, 5);
/*!40000 ALTER TABLE `dishes` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
