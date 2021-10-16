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
SET FOREIGN_KEY_CHECKS = 0;

-- Dumping database structure for restaurant-app-test
CREATE DATABASE IF NOT EXISTS `restaurant-app-test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restaurant-app-test`;

-- Dumping structure for table restaurant-app-test.orders
DELETE FROM `orders`;
ALTER TABLE `orders` AUTO_INCREMENT = 10;
CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `ready` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `restaurant` bigint DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl1i3a4n2v0xb5fq8sxe41r6w6` (`restaurant`),
  KEY `FKtjwuphstqm46uffgc7l1r27a9` (`created_by`),
  CONSTRAINT `FKl1i3a4n2v0xb5fq8sxe41r6w6` FOREIGN KEY (`restaurant`) REFERENCES `restaurants` (`id`),
  CONSTRAINT `FKtjwuphstqm46uffgc7l1r27a9` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app-test.orders: ~0 rows (approximately)
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` (`id`, `created_at`, `ready`, `updated_at`, `restaurant`, `created_by`) VALUES
	(1, '2021-04-14 15:53:37.000000', 0, '2021-05-14 15:53:41.000000', 1, 1),
	(2, '2021-05-14 15:53:57.000000', 0, '2021-07-14 15:54:00.000000', 1, 2),
	(3, '2021-07-14 15:54:11.000000', 1, '2021-08-14 15:54:13.000000', 1, 1),
	(4, '2021-09-14 15:54:23.000000', 1, '2021-10-14 15:54:25.000000', 1, 3),
	(5, '2021-08-14 15:54:38.000000', 0, '2021-09-14 15:54:42.000000', 1, 2),
	(6, '2021-09-14 15:54:50.000000', 0, '2021-10-14 15:54:56.000000', 1, 1),
	(7, '2021-10-14 15:55:52.000000', 1, '2021-10-14 15:55:55.000000', 2, 4),
	(8, '2021-10-14 15:56:02.000000', 0, '2021-10-14 15:56:05.000000', 2, 4),
	(9, '2021-10-14 15:56:13.000000', 1, '2021-10-14 15:56:16.000000', 3, 5);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;

SET FOREIGN_KEY_CHECKS = 1;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
