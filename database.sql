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


-- Dumping database structure for restaurant-app
CREATE DATABASE IF NOT EXISTS `restaurant-app` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restaurant-app`;

-- Dumping structure for table restaurant-app.chats
CREATE TABLE IF NOT EXISTS `chats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `first_user` bigint DEFAULT NULL,
  `second_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjkdygx0osyuythgf37fy512f8` (`first_user`),
  KEY `FK59axryomh25kdkerna0hwm7ps` (`second_user`),
  CONSTRAINT `FK59axryomh25kdkerna0hwm7ps` FOREIGN KEY (`second_user`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKjkdygx0osyuythgf37fy512f8` FOREIGN KEY (`first_user`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.chats: ~6 rows (approximately)
/*!40000 ALTER TABLE `chats` DISABLE KEYS */;
INSERT INTO `chats` (`id`, `created_at`, `updated_at`, `first_user`, `second_user`) VALUES
	(4, '2019-10-16 16:01:17.000000', '2019-10-16 16:01:16.000000', 1, 5),
	(8, '2019-10-16 16:01:21.000000', '2019-10-16 16:01:20.000000', 2, 1),
	(9, '2019-10-16 16:01:15.000000', '2019-10-16 16:01:15.000000', 1, 6),
	(10, '2019-10-16 16:01:22.000000', '2019-10-16 16:01:21.000000', 6, 2),
	(11, '2019-10-16 16:01:18.000000', '2019-10-16 16:01:17.000000', 1, 4),
	(12, '2019-10-16 16:01:19.000000', '2019-10-16 16:01:19.000000', 1, 3);
/*!40000 ALTER TABLE `chats` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.dishes
CREATE TABLE IF NOT EXISTS `dishes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ready` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9i7bdexxjb8go3rndk0ucj4gb` (`order_id`),
  KEY `FK2wdnjopvfbxb792imcc1ngxsl` (`updated_by`),
  CONSTRAINT `FK2wdnjopvfbxb792imcc1ngxsl` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK9i7bdexxjb8go3rndk0ucj4gb` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.dishes: ~96 rows (approximately)
/*!40000 ALTER TABLE `dishes` DISABLE KEYS */;
INSERT INTO `dishes` (`id`, `created_at`, `name`, `ready`, `updated_at`, `order_id`, `updated_by`) VALUES
	(19, '2021-10-16 16:03:16.000000', 'Pizza Chicago', 0, '2021-10-16 16:03:51.000000', 3, NULL),
	(20, '2021-10-16 16:03:16.000000', 'Pizza Margherita', 1, '2021-10-16 16:04:17.000000', 3, 1),
	(21, '2021-10-16 16:03:17.000000', 'Pizza Marinara', 0, '2021-10-16 16:04:18.000000', 3, NULL),
	(22, '2021-10-16 16:03:17.000000', 'Pizza Greek', 1, '2021-10-16 16:04:19.000000', 3, 1),
	(23, '2021-10-16 16:03:20.000000', 'Salad', 0, '2021-10-16 16:04:19.000000', 4, NULL),
	(24, '2021-10-16 16:03:18.000000', 'Fajitas', 1, '2021-10-16 16:04:20.000000', 4, 1),
	(25, '2021-10-16 16:03:19.000000', 'Banana split', 1, '2021-10-16 16:04:21.000000', 4, 1),
	(26, '2021-10-16 16:03:20.000000', 'Pizza Marinara', 1, '2021-10-16 16:04:21.000000', 4, 1),
	(27, '2021-10-16 16:03:21.000000', 'Pizza Sicilian', 0, '2021-10-16 16:04:22.000000', 4, NULL),
	(28, '2021-10-16 16:03:21.000000', 'Salad', 0, '2021-10-16 16:04:23.000000', 5, NULL),
	(29, '2021-10-16 16:03:23.000000', 'Pizza Margherita', 1, '2021-10-16 16:04:23.000000', 5, 2),
	(30, '2021-10-16 16:03:22.000000', 'Pizza Sicilian', 1, '2021-10-16 16:04:24.000000', 5, 2),
	(31, '2021-10-16 16:03:24.000000', 'Pizza Marinara', 1, '2021-10-16 16:04:24.000000', 5, 2),
	(32, '2021-10-16 16:03:23.000000', 'Tater tots', 1, '2021-10-16 16:04:25.000000', 5, 2),
	(33, '2021-10-16 16:03:25.000000', 'Tater tots', 1, '2021-10-16 16:04:25.000000', 6, 2),
	(34, '2021-10-16 16:03:25.000000', 'Salad', 0, '2021-10-16 16:04:26.000000', 6, NULL),
	(35, '2021-10-16 16:03:26.000000', 'Pizza Margherita', 1, '2021-10-16 16:04:26.000000', 6, 2),
	(36, '2021-10-16 16:03:26.000000', 'Pizza Sicilian', 0, '2021-10-16 16:04:27.000000', 6, NULL),
	(37, '2021-10-16 16:03:27.000000', 'Fajitas', 1, '2021-10-16 16:04:27.000000', 6, 2),
	(38, '2021-10-16 16:03:27.000000', 'Pizza California', 1, '2021-10-16 16:04:28.000000', 7, 2),
	(39, '2021-10-16 16:03:28.000000', 'Pizza Sicilian', 0, '2021-10-16 16:04:29.000000', 7, NULL),
	(40, '2021-10-16 16:03:28.000000', 'Pizza Marinara', 0, '2021-10-16 16:04:30.000000', 7, NULL),
	(41, '2021-10-16 16:03:29.000000', 'Pizza Chicago', 1, '2021-10-16 16:04:29.000000', 7, 2),
	(42, '2021-10-16 16:03:29.000000', 'Pizza Greek', 1, '2021-10-16 16:04:30.000000', 7, 2),
	(43, '2021-10-16 16:03:30.000000', 'Pizza Sicilian', 0, '2021-10-16 16:04:31.000000', 7, NULL),
	(44, '2021-10-16 16:03:30.000000', 'Pizza Chicago', 1, '2021-10-16 16:03:34.000000', 8, 1),
	(45, '2021-10-16 16:03:31.000000', 'Pizza Margherita', 1, '2021-10-16 16:04:35.000000', 8, 1),
	(46, '2021-10-16 16:03:32.000000', 'Pizza Greek', 1, '2021-10-16 16:04:35.000000', 8, 1),
	(47, '2021-10-16 16:03:33.000000', 'Pizza California', 1, '2021-10-16 16:04:36.000000', 8, 1),
	(48, '2021-10-16 16:03:33.000000', 'Banana split', 1, '2021-10-16 16:03:34.000000', 8, 1),
	(49, '2021-10-16 16:03:34.000000', 'Tater tots', 1, '2021-10-16 16:04:37.000000', 9, 1),
	(50, '2021-10-16 16:03:35.000000', 'Pizza Sicilian', 0, '2021-10-16 16:04:38.000000', 9, NULL),
	(51, '2021-10-16 16:03:36.000000', 'Tomato Pie', 1, '2021-10-16 16:04:39.000000', 9, 2),
	(52, '2021-10-16 16:03:36.000000', 'Salad', 0, '2021-10-16 16:04:39.000000', 9, NULL),
	(53, '2021-10-16 16:03:37.000000', 'Fajitas', 1, '2021-10-16 16:04:40.000000', 9, 2),
	(54, '2021-10-16 16:03:38.000000', 'Fajitas', 0, '2021-10-16 16:04:40.000000', 10, NULL),
	(55, '2021-10-16 16:03:37.000000', 'Pizza Greek', 1, '2021-10-16 16:04:41.000000', 10, 2),
	(56, '2021-10-16 16:03:39.000000', 'Pizza Chicago', 1, '2021-10-16 16:04:42.000000', 10, 2),
	(57, '2021-10-16 16:03:45.000000', 'Pizza California', 0, '2021-10-16 16:04:43.000000', 10, NULL),
	(58, '2021-10-16 16:03:39.000000', 'Tater tots', 1, '2021-10-16 16:04:42.000000', 10, 2),
	(59, '2021-10-16 16:03:40.000000', 'Banana split', 0, '2021-10-16 16:04:44.000000', 10, NULL),
	(60, '2021-10-16 16:03:41.000000', 'Tater tots', 1, '2021-10-16 16:04:43.000000', 11, 2),
	(61, '2021-10-16 16:03:41.000000', 'Pizza Sicilian', 0, '2021-10-16 16:04:44.000000', 11, NULL),
	(62, '2021-10-16 16:03:42.000000', 'Tomato Pie', 1, '2021-10-16 16:04:45.000000', 11, 2),
	(63, '2021-10-16 16:03:43.000000', 'Salad', 0, '2021-10-16 16:04:46.000000', 11, NULL),
	(64, '2021-10-16 16:03:43.000000', 'Fajitas', 0, '2021-10-16 16:04:46.000000', 11, NULL),
	(65, '2021-10-16 16:03:13.000000', 'Banana split', 1, '2021-10-16 16:04:48.000000', 11, 2),
	(66, '2021-10-16 16:03:44.000000', 'Fajitas', 1, '2021-10-16 16:04:49.000000', 12, 2),
	(67, '2021-10-16 16:03:12.000000', 'Pizza Chicago', 0, '2021-10-16 16:04:50.000000', 12, NULL),
	(68, '2021-10-16 16:03:12.000000', 'Fajitas', 1, '2021-10-16 16:04:49.000000', 12, 1),
	(69, '2021-10-16 16:03:11.000000', 'Pizza California', 0, '2021-10-16 16:04:50.000000', 12, NULL),
	(70, '2021-10-16 16:03:10.000000', 'Tater tots', 0, '2021-10-16 16:04:51.000000', 12, NULL),
	(71, '2021-10-16 16:03:10.000000', 'Pizza Margherita', 1, '2021-10-16 16:04:51.000000', 12, 1),
	(72, '2021-10-16 16:03:09.000000', 'Fajitas', 1, '2021-10-16 16:04:52.000000', 13, 1),
	(73, '2021-10-16 16:03:09.000000', 'Salad', 0, '2021-10-16 16:04:53.000000', 13, NULL),
	(74, '2021-10-16 16:03:08.000000', 'Banana split', 1, '2021-10-16 16:04:54.000000', 13, 1),
	(75, '2021-10-16 16:03:07.000000', 'Tomato Pie', 0, '2021-10-16 16:04:53.000000', 13, NULL),
	(76, '2021-10-16 16:03:49.000000', 'Pizza Sicilian', 1, '2021-10-16 16:04:54.000000', 13, 1),
	(77, '2021-10-16 16:03:07.000000', 'Fajitas', 1, '2021-10-16 16:04:55.000000', 14, 1),
	(78, '2021-10-16 16:03:05.000000', 'Tomato Pie', 1, '2021-10-16 16:04:56.000000', 14, 1),
	(79, '2021-10-16 16:03:06.000000', 'Banana split', 0, '2021-10-16 16:04:56.000000', 14, NULL),
	(80, '2021-10-16 16:03:05.000000', 'Tater tots', 1, '2021-10-16 16:04:57.000000', 14, 1),
	(81, '2021-10-16 16:03:04.000000', 'Pizza Marinara', 0, '2021-10-16 16:04:57.000000', 14, NULL),
	(82, '2021-10-16 16:03:03.000000', 'Salad', 0, '2021-10-16 16:04:58.000000', 14, NULL),
	(83, '2021-10-16 16:03:03.000000', 'Pizza California', 1, '2021-10-16 16:04:59.000000', 15, 1),
	(84, '2021-10-16 16:03:02.000000', 'Salad', 1, '2021-10-16 16:05:07.000000', 15, 1),
	(85, '2021-10-16 16:03:01.000000', 'Fajitas', 0, '2021-10-16 16:05:07.000000', 15, NULL),
	(86, '2021-10-16 16:03:01.000000', 'Pizza Margherita', 1, '2021-10-16 16:05:08.000000', 15, 1),
	(87, '2021-10-16 16:03:00.000000', 'Pizza Greek', 1, '2021-10-16 16:05:09.000000', 15, 1),
	(88, '2021-10-16 16:02:59.000000', 'Pizza Sicilian', 0, '2021-10-16 16:05:11.000000', 15, NULL),
	(89, '2021-10-16 16:02:59.000000', 'Fajitas', 0, '2021-10-16 16:05:10.000000', 16, NULL),
	(90, '2021-10-16 16:02:58.000000', 'Salad', 1, '2021-10-16 16:05:11.000000', 16, 1),
	(91, '2021-10-16 16:02:57.000000', 'Pizza Sicilian', 1, '2021-10-16 16:05:12.000000', 16, 1),
	(92, '2021-10-16 16:02:58.000000', 'Pizza California', 1, '2021-10-16 16:05:12.000000', 16, 1),
	(93, '2021-10-16 16:02:56.000000', 'Salad', 0, '2021-10-16 16:05:13.000000', 16, NULL),
	(94, '2021-10-16 16:02:56.000000', 'Tomato Pie', 1, '2021-10-16 16:05:15.000000', 16, 1),
	(95, '2021-10-16 16:02:54.000000', 'Banana split', 0, '2021-10-16 16:05:16.000000', 17, NULL),
	(96, '2021-10-16 16:02:55.000000', 'Pizza Sicilian', 1, '2021-10-16 16:05:15.000000', 17, 1),
	(97, '2021-10-16 16:02:47.000000', 'Tater tots', 0, '2021-10-16 16:05:17.000000', 17, NULL),
	(98, '2021-10-16 16:02:46.000000', 'Tater tots', 0, '2021-10-16 16:05:16.000000', 17, NULL),
	(99, '2021-10-16 16:02:46.000000', 'Pizza Chicago', 1, '2021-10-16 16:05:17.000000', 17, 1),
	(100, '2021-10-16 16:02:45.000000', 'Fajitas', 0, '2021-10-16 16:05:19.000000', 17, NULL),
	(101, '2021-10-16 16:02:45.000000', 'Banana split', 1, '2021-10-16 16:05:18.000000', 17, 1),
	(102, '2021-10-16 16:02:44.000000', 'Pizza Margherita', 0, '2021-10-16 16:05:20.000000', 18, NULL),
	(103, '2021-10-16 16:02:40.000000', 'Tomato Pie', 0, '2021-10-16 16:05:19.000000', 18, NULL),
	(104, '2021-10-16 16:02:41.000000', 'Pizza California', 1, '2021-10-16 16:05:20.000000', 18, 1),
	(105, '2021-10-16 16:02:40.000000', 'Banana split', 1, '2021-10-16 16:05:21.000000', 18, 1),
	(106, '2021-10-16 16:02:39.000000', 'Fajitas', 0, '2021-10-16 16:05:21.000000', 18, NULL),
	(107, '2021-10-16 16:02:39.000000', 'Pizza Greek', 1, '2021-10-16 16:05:22.000000', 18, 2),
	(108, '2021-10-16 16:02:37.000000', 'Pizza Chicago', 1, '2021-10-16 16:02:38.000000', 18, 2),
	(175, '2021-10-16 16:02:35.000000', 'Pizza Chicago', 1, '2021-10-16 16:02:36.000000', 19, 2),
	(176, '2021-10-16 16:02:36.000000', 'Banana split', 0, '2021-10-16 16:02:35.000000', 19, NULL),
	(177, '2021-10-16 16:02:33.000000', 'Pizza California', 0, '2021-10-16 16:01:34.000000', 19, NULL),
	(178, '2021-10-16 16:02:32.000000', 'Fajitas', 0, '2021-10-16 16:02:33.000000', 19, NULL),
	(179, '2021-10-16 16:02:30.000000', 'Tomato Pie', 0, '2021-10-16 16:02:31.000000', 19, NULL),
	(180, '2021-10-16 16:02:29.000000', 'Tater tots', 0, '2021-10-16 16:02:30.000000', 19, NULL);
/*!40000 ALTER TABLE `dishes` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.email_tokens
CREATE TABLE IF NOT EXISTS `email_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9j2pq9xa8fh246k3uruj9feec` (`user_id`),
  CONSTRAINT `FK9j2pq9xa8fh246k3uruj9feec` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.email_tokens: ~0 rows (approximately)
/*!40000 ALTER TABLE `email_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `email_tokens` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.files
CREATE TABLE IF NOT EXISTS `files` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `extension` varchar(255) DEFAULT NULL,
  `resource_type` varchar(255) DEFAULT NULL,
  `size` double NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `owner` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7smd1t0j0srej7e4e1wnfmrhc` (`owner`),
  CONSTRAINT `FK7smd1t0j0srej7e4e1wnfmrhc` FOREIGN KEY (`owner`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.files: ~9 rows (approximately)
/*!40000 ALTER TABLE `files` DISABLE KEYS */;
INSERT INTO `files` (`id`, `extension`, `resource_type`, `size`, `type`, `owner`) VALUES
	(1, 'png', 'profileImage', 43250, 'image/png', 1),
	(2, 'png', 'profileImage', 46000, 'image/png', 2),
	(3, 'png', 'profileImage', 32000, 'image/png', 3),
	(4, 'png', 'profileImage', 37000, 'image/png', 4),
	(5, 'png', 'profileImage', 50000, 'image/png', 5),
	(6, 'png', 'profileImage', 20000, 'image/png', 6),
	(7, 'png', 'profileImage', 31500, 'image/png', 7),
	(8, 'png', 'profileImage', 31200, 'image/png', 8),
	(9, 'png', 'test', 66800, 'image/png', 3);
/*!40000 ALTER TABLE `files` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.menu
CREATE TABLE IF NOT EXISTS `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `restaurant` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKha3mjlpyaejx4bxghj5emhcc7` (`restaurant`),
  CONSTRAINT `FKha3mjlpyaejx4bxghj5emhcc7` FOREIGN KEY (`restaurant`) REFERENCES `restaurants` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.menu: ~11 rows (approximately)
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` (`id`, `name`, `restaurant`) VALUES
	(1, 'Salad', 1),
	(2, 'Pizza Marinara', 1),
	(3, 'Pizza Margherita', 1),
	(4, 'Pizza Chicago', 1),
	(5, 'Pizza Sicilian', 1),
	(6, 'Pizza Greek', 1),
	(7, 'Pizza California', 1),
	(8, 'Tomato Pie', 1),
	(9, 'Tater tots', 1),
	(10, 'Fajitas', 1),
	(11, 'Banana split', 1);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.messages
CREATE TABLE IF NOT EXISTS `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message` varchar(255) DEFAULT NULL,
  `time` time DEFAULT NULL,
  `receiver` bigint DEFAULT NULL,
  `chat` bigint DEFAULT NULL,
  `session_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpmq5ifuu12ky74d5hbouj2gxn` (`receiver`),
  KEY `FK308xy9e9twocfxxphbkgjk20b` (`chat`,`session_date`),
  CONSTRAINT `FK308xy9e9twocfxxphbkgjk20b` FOREIGN KEY (`chat`, `session_date`) REFERENCES `sessions` (`chat`, `session_date`),
  CONSTRAINT `FKpmq5ifuu12ky74d5hbouj2gxn` FOREIGN KEY (`receiver`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.messages: ~31 rows (approximately)
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` (`id`, `message`, `time`, `receiver`, `chat`, `session_date`) VALUES
	(21, 'Hey, I am Peter we met yesterday.', '02:35:50', 1, 9, '2019-04-17'),
	(22, 'Hi, yeah I remember.', '02:37:15', 6, 9, '2019-04-17'),
	(23, 'How are you?', '02:37:19', 6, 9, '2019-04-17'),
	(24, 'I am fine, thanks.', '02:37:52', 1, 9, '2019-04-17'),
	(25, 'How do you do?', '02:42:29', 1, 9, '2019-04-17'),
	(26, 'I am good, just finished my project.', '02:43:13', 6, 9, '2019-04-17'),
	(36, 'Hey, how are you?', '14:13:10', 1, 9, '2019-04-17'),
	(37, 'I am fine, thanks.', '14:13:22', 6, 9, '2019-04-17'),
	(132, 'Hey, I am Peter\'s friend.', '16:26:16', 1, 4, '2019-04-17'),
	(133, 'We met last week', '16:26:21', 1, 4, '2019-04-17'),
	(134, 'Hey, What\'s up?', '16:26:56', 7, 4, '2019-04-17'),
	(135, 'I am throwing a party tomorrow.Do you want to come?', '16:28:09', 1, 4, '2019-04-17'),
	(136, 'Yeah, sure I am free.', '16:29:13', 7, 4, '2019-04-17'),
	(137, 'Cool, you can ask Peter for all the details and you can come together.', '16:31:20', 1, 4, '2019-04-17'),
	(138, 'We are are going out soon and I will ask him.', '16:32:41', 7, 4, '2019-04-17'),
	(139, 'See you tomorrow.', '16:33:51', 7, 4, '2019-04-17'),
	(166, 'Hey', '22:20:14', 1, 8, '2019-04-17'),
	(167, 'Hi', '22:20:24', 2, 8, '2019-04-17'),
	(168, 'How are you?', '22:20:29', 1, 8, '2019-04-17'),
	(169, 'Fine, you?', '22:20:44', 2, 8, '2019-04-17'),
	(170, 'Yeah, me too!', '22:20:56', 1, 8, '2019-04-17'),
	(180, 'Hey, are you going tomorrow?', '00:17:51', 1, 12, '2019-04-18'),
	(181, 'Yes, what about you?', '00:18:04', 9, 12, '2019-04-18'),
	(182, 'Yeah, me too.', '00:18:17', 1, 12, '2019-04-18'),
	(183, 'See you tomorrow then.', '00:18:28', 9, 12, '2019-04-18'),
	(184, 'Hey where have you been?', '00:19:26', 1, 11, '2019-04-18'),
	(185, 'Hey, I was busy yesterday.', '00:19:41', 8, 11, '2019-04-18'),
	(186, 'Maybe I will come tomorrow.', '00:19:51', 8, 11, '2019-04-18'),
	(187, 'Ok, I will wait for you.', '00:20:07', 1, 11, '2019-04-18'),
	(188, 'Hey, how are you?', '00:25:50', 6, 14, '2019-04-18'),
	(189, 'Are you going out tonight?', '00:28:04', 6, 10, '2019-04-18');
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.orders
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.orders: ~19 rows (approximately)
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` (`id`, `created_at`, `ready`, `updated_at`, `restaurant`, `created_by`) VALUES
	(1, '2019-04-15 16:41:59.304000', 0, '2019-04-16 02:42:02.000000', 2, 2),
	(2, '2019-04-13 13:44:27.000000', 0, '2019-04-13 18:44:31.000000', 2, 2),
	(3, '2019-03-30 00:57:51.000000', 0, '2019-04-12 05:24:27.000000', 1, 1),
	(4, '2019-03-31 02:57:59.000000', 0, '2019-04-12 09:25:18.000000', 1, 1),
	(5, '2019-04-01 12:58:04.000000', 0, '2019-04-11 06:26:10.000000', 1, 1),
	(6, '2019-04-02 05:58:10.000000', 0, '2019-04-10 12:25:00.000000', 1, 1),
	(7, '2019-04-03 05:58:16.000000', 0, '2019-04-09 15:24:53.000000', 1, 1),
	(9, '2019-04-05 00:58:36.000000', 0, '2019-04-05 01:24:38.000000', 1, 1),
	(10, '2019-04-06 00:58:44.000000', 0, '2019-04-06 01:24:31.000000', 1, 1),
	(11, '2019-04-07 00:58:52.000000', 0, '2019-04-07 01:24:23.000000', 1, 1),
	(12, '2019-04-08 00:58:58.000000', 0, '2019-04-08 05:24:15.000000', 1, 1),
	(13, '2019-04-09 00:59:05.000000', 0, '2019-04-09 01:24:08.000000', 1, 1),
	(14, '2019-04-10 06:59:13.000000', 0, '2019-04-10 15:23:59.000000', 1, 1),
	(15, '2019-04-11 05:59:20.000000', 0, '2019-04-18 12:36:53.000000', 1, 1),
	(16, '2019-04-12 00:59:27.000000', 0, '2019-04-18 12:21:57.000000', 1, 1),
	(17, '2019-04-13 01:00:32.000000', 0, '2019-04-13 11:58:01.000000', 1, 1),
	(18, '2019-04-14 01:00:43.000000', 0, '2019-04-14 17:02:16.000000', 1, 1),
	(19, '2019-04-15 17:51:21.000000', 0, '2019-04-16 15:52:21.000000', 1, 6);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.restaurants
CREATE TABLE IF NOT EXISTS `restaurants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.restaurants: ~2 rows (approximately)
/*!40000 ALTER TABLE `restaurants` DISABLE KEYS */;
INSERT INTO `restaurants` (`id`, `address`, `name`, `token`, `type`) VALUES
	(1, 'str. Vitosha 3', 'Happy', 'd32zer1', 'Casual Dining'),
	(2, 'str. Vitosha 4', 'Mcdonalds', 'z1rf34z2', 'Fast food');
/*!40000 ALTER TABLE `restaurants` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.sessions
CREATE TABLE IF NOT EXISTS `sessions` (
  `session_date` date NOT NULL,
  `chat` bigint NOT NULL,
  PRIMARY KEY (`chat`,`session_date`),
  CONSTRAINT `FKi239krmv92y7mk4hp7d5dccj7` FOREIGN KEY (`chat`) REFERENCES `chats` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.sessions: ~9 rows (approximately)
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
INSERT INTO `sessions` (`session_date`, `chat`) VALUES
	('2019-04-17', 4),
	('2019-04-17', 8),
	('2019-04-17', 9),
	('2020-07-08', 9),
	('2019-04-18', 10),
	('2019-04-18', 11),
	('2019-04-18', 12),
	('2019-04-18', 14);
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;

-- Dumping structure for table restaurant-app.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age` int NOT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `is_enabled` bit(1) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `profile_image` bigint DEFAULT NULL,
  `restaurant` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKc7w5h6b6d74bu9o1yulvrbh6c` (`profile_image`),
  KEY `FK77vld2hq44lseflaur47ne72c` (`restaurant`),
  CONSTRAINT `FK77vld2hq44lseflaur47ne72c` FOREIGN KEY (`restaurant`) REFERENCES `restaurants` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKc7w5h6b6d74bu9o1yulvrbh6c` FOREIGN KEY (`profile_image`) REFERENCES `files` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table restaurant-app.users: ~6 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `age`, `country`, `email`, `firstName`, `is_enabled`, `lastName`, `password`, `role`, `username`, `profile_image`, `restaurant`) VALUES
	(1, 24, 'Bulgaria', 'email@gmail.com', 'Ivaylo', b'1', 'Aleksandrov', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'Chef', 'admin', 1, 1),
	(2, 23, 'Bulgaria', 'email1@gmail.com', 'George', b'1', 'Petrov', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'Server', 'george90', 2, 1),
	(3, 23, 'Bulgaria', 'email2@gmail.com', 'Peter', b'1', 'Georgiev', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'Server', 'admin1', 3, 1),
	(4, 27, 'Bulgaria', 'email3@gmail.com', 'Merian', b'1', 'Dimitrova', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'Server', 'meri93', 4, 1),
	(5, 28, 'Bulgaria', 'email4@gmail.com', 'Erika', b'1', 'Ivanov', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'Chef', 'erika92', 5, 1),
	(6, 24, 'Bulgaria', 'email5@gmail.com', 'Elena', b'1', 'Georgieva', '$2a$04$PFraPHMqOOa7qiBJX5Mmq.STptiykt4m1H.p7rfpzzg/x1mQ9Ega6', 'Chef', 'elena89', 6, 1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
