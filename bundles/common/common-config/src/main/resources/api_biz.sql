-- --------------------------------------------------------
-- Host:                         211.113.53.126
-- Server version:               5.5.20 - Source distribution
-- Server OS:                    Linux
-- HeidiSQL version:             7.0.0.4147
-- Date/time:                    2012-07-04 17:01:50
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;

USE api_biz;

-- Dumping structure for table api_biz.api
DROP TABLE IF EXISTS `api`;
CREATE TABLE IF NOT EXISTS `api` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `service_seq` bigint(8) NOT NULL,
  `id` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`),
  KEY `service_seq` (`service_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.api: ~0 rows (approximately)
/*!40000 ALTER TABLE `api` DISABLE KEYS */;
/*!40000 ALTER TABLE `api` ENABLE KEYS */;


-- Dumping structure for table api_biz.api_package_map
DROP TABLE IF EXISTS `api_package_map`;
CREATE TABLE IF NOT EXISTS `api_package_map` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `api_seq` bigint(8) NOT NULL,
  `package_seq` bigint(8) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.api_package_map: ~0 rows (approximately)
/*!40000 ALTER TABLE `api_package_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_package_map` ENABLE KEYS */;


-- Dumping structure for table api_biz.api_package_policy
DROP TABLE IF EXISTS `api_package_policy`;
CREATE TABLE IF NOT EXISTS `api_package_policy` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `seq_type` enum('api','package') NOT NULL,
  `relate_seq` bigint(8) NOT NULL,
  `policy_seq` bigint(8) NOT NULL,
  `property_data` text NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.api_package_policy: ~0 rows (approximately)
/*!40000 ALTER TABLE `api_package_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_package_policy` ENABLE KEYS */;


-- Dumping structure for table api_biz.biz_portal
DROP TABLE IF EXISTS `biz_portal`;
CREATE TABLE IF NOT EXISTS `biz_portal` (
  `id` varchar(20) NOT NULL,
  `token` varchar(50) NOT NULL,
  `key` varchar(50) NOT NULL,
  `secret` varchar(50) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.biz_portal: ~0 rows (approximately)
/*!40000 ALTER TABLE `biz_portal` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_portal` ENABLE KEYS */;


-- Dumping structure for table api_biz.client_key_list
DROP TABLE IF EXISTS `client_key_list`;
CREATE TABLE IF NOT EXISTS `client_key_list` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `key_id` varchar(50) NOT NULL,
  `key_secret` varchar(50) NOT NULL,
  `key_name` varchar(50) NOT NULL,
  `key_used_url` text NOT NULL,
  `key_used_detail` text NOT NULL,
  `package_seq` bigint(8) NOT NULL,
  `user_info_seq` bigint(8) NOT NULL,
  `state` enum('on','off') NOT NULL DEFAULT 'on',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_agent` enum('web','android','ios','etc') NOT NULL DEFAULT 'etc',
  PRIMARY KEY (`seq`),
  KEY `key_id` (`key_id`),
  KEY `user_info_id` (`user_info_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.client_key_list: ~0 rows (approximately)
/*!40000 ALTER TABLE `client_key_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_key_list` ENABLE KEYS */;


-- Dumping structure for table api_biz.package
DROP TABLE IF EXISTS `package`;
CREATE TABLE IF NOT EXISTS `package` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `id` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.package: ~0 rows (approximately)
/*!40000 ALTER TABLE `package` DISABLE KEYS */;
/*!40000 ALTER TABLE `package` ENABLE KEYS */;


-- Dumping structure for table api_biz.policy
DROP TABLE IF EXISTS `policy`;
CREATE TABLE IF NOT EXISTS `policy` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `element` varchar(50) NOT NULL,
  `element_name` varchar(50) NOT NULL,
  `properties` text NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.policy: ~0 rows (approximately)
/*!40000 ALTER TABLE `policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `policy` ENABLE KEYS */;


-- Dumping structure for table api_biz.service
DROP TABLE IF EXISTS `service`;
CREATE TABLE IF NOT EXISTS `service` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `user_info_seq` bigint(8) NOT NULL,
  `id` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `biz_portal_id` enum('puddingto','etc') NOT NULL DEFAULT 'puddingto',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq`),
  KEY `user_info_seq` (`user_info_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.service: ~0 rows (approximately)
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
/*!40000 ALTER TABLE `service` ENABLE KEYS */;


-- Dumping structure for table api_biz.statis_daily
DROP TABLE IF EXISTS `statis_daily`;
CREATE TABLE IF NOT EXISTS `statis_daily` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `client_key` varchar(50) NOT NULL,
  `api_id` varchar(50) NOT NULL,
  `path_template` varchar(255) DEFAULT NULL,
  `timestamp` int(4) unsigned NOT NULL DEFAULT '0',
  `success` bigint(8) unsigned NOT NULL DEFAULT '0',
  `failure` bigint(8) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`seq`),
  KEY `client_key` (`client_key`),
  KEY `api_id` (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.statis_daily: ~0 rows (approximately)
/*!40000 ALTER TABLE `statis_daily` DISABLE KEYS */;
/*!40000 ALTER TABLE `statis_daily` ENABLE KEYS */;


-- Dumping structure for table api_biz.statis_hourly
DROP TABLE IF EXISTS `statis_hourly`;
CREATE TABLE IF NOT EXISTS `statis_hourly` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `client_key` varchar(50) NOT NULL,
  `api_id` varchar(50) NOT NULL,
  `path_template` varchar(255) DEFAULT NULL,
  `timestamp` int(4) unsigned NOT NULL DEFAULT '0',
  `success` bigint(8) NOT NULL DEFAULT '0',
  `failure` bigint(8) NOT NULL DEFAULT '0',
  PRIMARY KEY (`seq`),
  KEY `client_key` (`client_key`),
  KEY `api_id` (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.statis_hourly: ~0 rows (approximately)
/*!40000 ALTER TABLE `statis_hourly` DISABLE KEYS */;
/*!40000 ALTER TABLE `statis_hourly` ENABLE KEYS */;


-- Dumping structure for table api_biz.statis_monthly
DROP TABLE IF EXISTS `statis_monthly`;
CREATE TABLE IF NOT EXISTS `statis_monthly` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `client_key` varchar(50) NOT NULL,
  `api_id` varchar(50) NOT NULL,
  `path_template` varchar(255) DEFAULT NULL,
  `timestamp` mediumint(3) unsigned NOT NULL DEFAULT '0',
  `success` bigint(8) NOT NULL DEFAULT '0',
  `failure` bigint(8) NOT NULL DEFAULT '0',
  PRIMARY KEY (`seq`),
  KEY `client_key` (`client_key`),
  KEY `api_id` (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.statis_monthly: ~0 rows (approximately)
/*!40000 ALTER TABLE `statis_monthly` DISABLE KEYS */;
/*!40000 ALTER TABLE `statis_monthly` ENABLE KEYS */;


-- Dumping structure for table api_biz.statis_weekly
DROP TABLE IF EXISTS `statis_weekly`;
CREATE TABLE IF NOT EXISTS `statis_weekly` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `client_key` varchar(50) NOT NULL,
  `api_id` varchar(50) NOT NULL,
  `path_template` varchar(255) DEFAULT NULL,
  `timestamp` int(4) unsigned NOT NULL DEFAULT '0',
  `success` bigint(8) unsigned NOT NULL DEFAULT '0',
  `failure` bigint(8) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`seq`),
  KEY `client_key` (`client_key`),
  KEY `api_id` (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.statis_weekly: ~0 rows (approximately)
/*!40000 ALTER TABLE `statis_weekly` DISABLE KEYS */;
/*!40000 ALTER TABLE `statis_weekly` ENABLE KEYS */;


-- Dumping structure for table api_biz.statis_yearly
DROP TABLE IF EXISTS `statis_yearly`;
CREATE TABLE IF NOT EXISTS `statis_yearly` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `client_key` text NOT NULL,
  `api_id` text NOT NULL,
  `path_template` varchar(255) DEFAULT NULL,
  `timestamp` smallint(2) unsigned NOT NULL DEFAULT '0',
  `success` bigint(8) unsigned NOT NULL DEFAULT '0',
  `failure` bigint(8) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.statis_yearly: ~0 rows (approximately)
/*!40000 ALTER TABLE `statis_yearly` DISABLE KEYS */;
/*!40000 ALTER TABLE `statis_yearly` ENABLE KEYS */;


-- Dumping structure for table api_biz.user_info
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE IF NOT EXISTS `user_info` (
  `seq` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `biz_portal_id` enum('puddingto','etc') NOT NULL DEFAULT 'puddingto',
  `id` varchar(50) NOT NULL,
  `pw` varchar(70) NOT NULL,
  `nick` varchar(30) NOT NULL,
  `firstname` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `grade` tinyint(1) unsigned NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `phone` varchar(20) DEFAULT NULL,
  `sex` enum('m','f') DEFAULT NULL,
  `birth` varchar(15) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table api_biz.user_info: ~0 rows (approximately)
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
