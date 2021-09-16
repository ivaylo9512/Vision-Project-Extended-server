-- --------------------------------------------------------
-- Host:                         192.168.0.106
-- Server version:               10.3.8-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
SET FOREIGN_KEY_CHECKS = 0;

-- Dumping database structure for restaurant-app-test
CREATE DATABASE IF NOT EXISTS `restaurant-app-test` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `restaurant-app-test`;

-- Dumping structure for table restaurant-app-test.users
DELETE FROM `users`;
ALTER TABLE `users` AUTO_INCREMENT = 1;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `age` int(11) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `is_enabled` bigint(20) DEFAULT 1,
  `restaurant` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKc7w5h6b6d74bu9o1yulvrbh6c` (`profile_image`),
  KEY `FKsd7jrkn6c8wb1y8ffbdn3krel` (`restaurant`),
  CONSTRAINT `FKc7w5h6b6d74bu9o1yulvrbh6c` FOREIGN KEY (`profile_image`) REFERENCES `files` (`id`),
  CONSTRAINT `FKsd7jrkn6c8wb1y8ffbdn3krel` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

-- Dumping data for table restaurant-app-test.users: ~3 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `age`, `country`, `firstName`, `lastName`, `password`, `role`, `username`, `email`, `profile_image`, `is_enabled`, `restaurant`) VALUES
	(1, 25, 'Bulgaria', 'firstName', 'lastName', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'ROLE_ADMIN', 'adminUser', 'adminUser@gmail.com', 1, true, 1),
	(2, 30, 'Bulgaria', 'firsNameSecond', 'lastName2', '$2a$04$MRJSj9OWmmaWeDLPIHxU6.en5D8n10XTpFvgPQY5g.r61z0SFRkJW', 'ROLE_ADMIN', 'testUser', 'testUser@gmail.com', 2, true, 2),
	(3, 25, 'Bulgaria', 'firstNameThird', 'lastName3', '$2a$04$JrrsioMxE3HE7bJ/sXLWD.0Ty8iJB1W4zgxumoFmn9rWl5a0vATa6', 'ROLE_USER', 'testUser1', 'testUser1@gmail.com', 3, true, 1),
	(4, 24, 'Spain', 'First', 'Last', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'user1', 'user1@gmail.com', 4, true, 2),
	(5, 32, 'Italy', 'testFirst', 'testLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'firstTest', 'firstTest@gmail.com', 5, true, 3),
	(6, 24, 'Spain', 'secondFirst', 'secondLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'secondTest', 'secondTest@gmail.com', 6, true, 4),
	(7, 29, 'Italy', 'testThirdFirst', 'testThirdLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'testThird', 'testThird@gmail.com', 7, true, 1),
	(8, 29, 'Italy', 'testForthFirst', 'testForthLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'testForth', 'testForth@gmail.com', 8, true, 2),
	(9, 40, 'Spain', 'testFifthFirst', 'testFifthLast', '$2a$04$CMuyMiF6Wo5a4lbSdA68X.pj7jzYD6OPtv2KMLm.jl.B61waR/e9W', 'ROLE_USER', 'testFifth', 'testFifth@gmail.com', 9, true, 3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

SET FOREIGN_KEY_CHECKS = 1;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
