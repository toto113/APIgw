-- --------------------------------------------------------
-- Host:                         211.113.53.126
-- Server version:               5.5.20 - Source distribution
-- Server OS:                    Linux
-- HeidiSQL version:             7.0.0.4147
-- Date/time:                    2012-07-04 16:45:14
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;
USE backup;

-- Dumping structure for table backup.radix_business_platform
DROP TABLE IF EXISTS `radix_business_platform`;
CREATE TABLE IF NOT EXISTS `radix_business_platform` (
  `id` binary(16) NOT NULL,
  `businessPlatformKey` char(36) NOT NULL,
  `secret` char(40) NOT NULL,
  `description` varchar(255) NOT NULL,
  `domain` varchar(200) DEFAULT NULL,
  `redirect_uri` varchar(256) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `businessPlatformKey` (`businessPlatformKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Business Platform Information';

-- Dumping data for table backup.radix_business_platform: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_business_platform` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_business_platform` ENABLE KEYS */;


-- Dumping structure for table backup.radix_client
DROP TABLE IF EXISTS `radix_client`;
CREATE TABLE IF NOT EXISTS `radix_client` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `clientKey` char(36) NOT NULL,
  `secret` char(40) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `type` enum('P','D') NOT NULL,
  `redirect_uri` varchar(256) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  `application_name` varchar(50) DEFAULT NULL,
  `application_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `clientKey` (`clientKey`),
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Client Information';

-- Dumping data for table backup.radix_client: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_client` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_client` ENABLE KEYS */;


-- Dumping structure for table backup.radix_client_package
DROP TABLE IF EXISTS `radix_client_package`;
CREATE TABLE IF NOT EXISTS `radix_client_package` (
  `id` binary(16) NOT NULL,
  `clientID` binary(16) NOT NULL,
  `packageID` binary(16) NOT NULL,
  `parameters` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `clientID` (`clientID`,`packageID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Client-Package Information';

-- Dumping data for table backup.radix_client_package: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_client_package` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_client_package` ENABLE KEYS */;


-- Dumping structure for table backup.radix_package
DROP TABLE IF EXISTS `radix_package`;
CREATE TABLE IF NOT EXISTS `radix_package` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Package Information';

-- Dumping data for table backup.radix_package: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package` ENABLE KEYS */;


-- Dumping structure for table backup.radix_package_policy
DROP TABLE IF EXISTS `radix_package_policy`;
CREATE TABLE IF NOT EXISTS `radix_package_policy` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `packageID` binary(16) NOT NULL,
  `policyTypeID` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `properties` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH,
  KEY `packageID` (`packageID`,`policyTypeID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Package-Policy Information';

-- Dumping data for table backup.radix_package_policy: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package_policy` ENABLE KEYS */;


-- Dumping structure for table backup.radix_package_serviceapi
DROP TABLE IF EXISTS `radix_package_serviceapi`;
CREATE TABLE IF NOT EXISTS `radix_package_serviceapi` (
  `packageID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`packageID`,`serviceAPIID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Package-ServiceAPI Information';

-- Dumping data for table backup.radix_package_serviceapi: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package_serviceapi` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package_serviceapi` ENABLE KEYS */;


-- Dumping structure for table backup.radix_package_serviceapi_policy
DROP TABLE IF EXISTS `radix_package_serviceapi_policy`;
CREATE TABLE IF NOT EXISTS `radix_package_serviceapi_policy` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `packageID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `policyTypeID` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `properties` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH,
  KEY `packageID` (`packageID`,`serviceAPIID`,`policyTypeID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Package-ServiceAPI-Policy Information';

-- Dumping data for table backup.radix_package_serviceapi_policy: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package_serviceapi_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package_serviceapi_policy` ENABLE KEYS */;


-- Dumping structure for table backup.radix_policy_type
DROP TABLE IF EXISTS `radix_policy_type`;
CREATE TABLE IF NOT EXISTS `radix_policy_type` (
  `id` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `priority` int(3) DEFAULT '0',
  `properties` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isActivated` enum('Y','N') DEFAULT 'Y',
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Policy Information';

-- Dumping data for table backup.radix_policy_type: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_policy_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_policy_type` ENABLE KEYS */;


-- Dumping structure for table backup.radix_service
DROP TABLE IF EXISTS `radix_service`;
CREATE TABLE IF NOT EXISTS `radix_service` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `version` int(11) NOT NULL,
  `resourceOwner` enum('USER','PARTNER') NOT NULL,
  `resourceAuthUrl` varchar(256) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Service Information';

-- Dumping data for table backup.radix_service: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service` ENABLE KEYS */;


-- Dumping structure for table backup.radix_service_apis_mapping
DROP TABLE IF EXISTS `radix_service_apis_mapping`;
CREATE TABLE IF NOT EXISTS `radix_service_apis_mapping` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `partnerAPIID` binary(16) NOT NULL,
  `mappingType` enum('PARAMETER','RESULT') NOT NULL,
  `mapping` text NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `serviceID` (`serviceID`,`serviceAPIID`,`partnerAPIID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of API Mapping Specification';

-- Dumping data for table backup.radix_service_apis_mapping: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_apis_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_apis_mapping` ENABLE KEYS */;


-- Dumping structure for table backup.radix_service_apis_partnerapi
DROP TABLE IF EXISTS `radix_service_apis_partnerapi`;
CREATE TABLE IF NOT EXISTS `radix_service_apis_partnerapi` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `name` varchar(50) NOT NULL,
  `transportType` enum('REST','FILE','DBMS','SOAP','JMS','AWS') NOT NULL,
  `defaultTransformType` enum('XML','JSON','CSV','FORM') NOT NULL,
  `protocolType` varchar(15) NOT NULL,
  `protocolMeta` text,
  `parameters` text NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `serviceID` (`serviceID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of PartnerAPI Specification';

-- Dumping data for table backup.radix_service_apis_partnerapi: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_apis_partnerapi` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_apis_partnerapi` ENABLE KEYS */;


-- Dumping structure for table backup.radix_service_apis_serviceapi
DROP TABLE IF EXISTS `radix_service_apis_serviceapi`;
CREATE TABLE IF NOT EXISTS `radix_service_apis_serviceapi` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `apiKey` varchar(256) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `routingMethodType` enum('DIRECT','PIPELINING','PARALLEL') NOT NULL,
  `transportType` enum('REST','FILE','DBMS','SOAP','JMS','AWS') NOT NULL,
  `defaultTransformType` enum('XML','JSON','CSV','FORM') NOT NULL,
  `protocolType` varchar(15) NOT NULL,
  `protocolMeta` text,
  `parameters` text NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `serviceID` (`serviceID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of ServiceAPI Specification';

-- Dumping data for table backup.radix_service_apis_serviceapi: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_apis_serviceapi` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_apis_serviceapi` ENABLE KEYS */;


-- Dumping structure for table backup.radix_service_routing_direct
DROP TABLE IF EXISTS `radix_service_routing_direct`;
CREATE TABLE IF NOT EXISTS `radix_service_routing_direct` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `partnerAPIID` binary(16) NOT NULL,
  `parameterMappingID` binary(16) DEFAULT NULL,
  `resultMappingID` binary(16) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `removeDate` datetime DEFAULT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `serviceID` (`serviceID`,`serviceAPIID`,`partnerAPIID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Backup of Direct Routing ServiceAPI Information';

-- Dumping data for table backup.radix_service_routing_direct: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_routing_direct` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_routing_direct` ENABLE KEYS */;


USE radix;

-- Dumping structure for function radix.BINTOUUID
DROP FUNCTION IF EXISTS `BINTOUUID`;
DELIMITER //
CREATE FUNCTION `BINTOUUID`(bin BINARY(16)) RETURNS char(36) CHARSET latin1
    DETERMINISTIC
BEGIN
DECLARE hex BINARY(32);
SET hex = HEX(bin);
RETURN LOWER(CONCAT_WS('-',LEFT(hex,8),SUBSTR(hex,9,4),SUBSTR(hex,13,4),SUBSTR(hex,17,4),RIGHT(hex,12)));
END//
DELIMITER ;


-- Dumping structure for table radix.oauth_client_details
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE IF NOT EXISTS `oauth_client_details` (
  `client_id` varchar(50) NOT NULL,
  `resource_ids` varchar(50) NOT NULL,
  `client_secret` varchar(50) NOT NULL,
  `scope` varchar(50) NOT NULL,
  `authorized_grant_types` varchar(50) NOT NULL,
  `web_server_redirect_uri` varchar(255) NOT NULL,
  `authorities` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table radix.oauth_client_details: ~0 rows (approximately)
/*!40000 ALTER TABLE `oauth_client_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_client_details` ENABLE KEYS */;


-- Dumping structure for table radix.oauth_code
DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE IF NOT EXISTS `oauth_code` (
  `code` varchar(256) NOT NULL,
  `authentication` blob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table radix.oauth_code: ~0 rows (approximately)
/*!40000 ALTER TABLE `oauth_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_code` ENABLE KEYS */;


-- Dumping structure for table radix.radix_business_platform
DROP TABLE IF EXISTS `radix_business_platform`;
CREATE TABLE IF NOT EXISTS `radix_business_platform` (
  `id` binary(16) NOT NULL,
  `businessPlatformKey` char(36) NOT NULL,
  `secret` char(40) NOT NULL,
  `description` varchar(255) NOT NULL,
  `domain` varchar(200) DEFAULT NULL,
  `redirect_uri` varchar(256) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `businessPlatformKey` (`businessPlatformKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Business Platform Information';

-- Dumping data for table radix.radix_business_platform: ~1 rows (approximately)
/*!40000 ALTER TABLE `radix_business_platform` DISABLE KEYS */;
INSERT INTO `radix_business_platform` (`id`, `businessPlatformKey`, `secret`, `description`, `domain`, `redirect_uri`, `regDate`, `isValid`, `invalidStatus`) VALUES
	(_binary 0x59FF3F09014F3FAD44687540AB4B0000, '59ffa6f4-0901-4ffc-82ad-44687540ab4b', '21334d4ffb7994f5094eb41b5a70dd3a165780f9', 'puddingto', 'developer.pudding.to', 'http://developer.pudding.to', now(), 'Y', NULL);
/*!40000 ALTER TABLE `radix_business_platform` ENABLE KEYS */;


-- Dumping structure for table radix.radix_client
DROP TABLE IF EXISTS `radix_client`;
CREATE TABLE IF NOT EXISTS `radix_client` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `clientKey` char(36) NOT NULL,
  `secret` char(40) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `type` enum('P','D') NOT NULL,
  `redirect_uri` varchar(256) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  `application_name` varchar(50) DEFAULT NULL,
  `application_description` varchar(255) DEFAULT NULL,  
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `clientKey` (`clientKey`),
  UNIQUE KEY `businessPlatformID` (`businessPlatformID`,`clientKey`),
  KEY `businessPlatformID_2` (`businessPlatformID`,`partnerID`) USING HASH,
  CONSTRAINT `radix_client_ibfk_1` FOREIGN KEY (`businessPlatformID`) REFERENCES `radix_business_platform` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Client Information';

INSERT INTO `radix_client` (`id`, `businessPlatformID`, `clientKey`, `secret`, `partnerID`, `type`, `redirect_uri`, `regDate`, `isValid`, `invalidStatus`) VALUES
(_binary 0x59FF3F09014F3FAD44687540AB4B, _binary 0x59FF3F09014F3FAD44687540AB4B, '59ffa6f4-0901-4ffc-82ad-44687540ab4b', '21334d4ffb7994f5094eb41b5a70dd3a165780f9', 'appleTree', 'P', 'http://redirect.withapi.com', '2012-06-26 11:51:49', 'Y', NULL);

-- Dumping data for table radix.radix_client: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_client` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_client` ENABLE KEYS */;


-- Dumping structure for table radix.radix_client_package
DROP TABLE IF EXISTS `radix_client_package`;
CREATE TABLE IF NOT EXISTS `radix_client_package` (
  `id` binary(16) NOT NULL,
  `clientID` binary(16) NOT NULL,
  `packageID` binary(16) NOT NULL,
  `parameters` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `clientID` (`clientID`,`packageID`) USING HASH,
  KEY `packageID` (`packageID`),
  CONSTRAINT `radix_client_package_ibfk_1` FOREIGN KEY (`packageID`) REFERENCES `radix_package` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_client_package_ibfk_2` FOREIGN KEY (`clientID`) REFERENCES `radix_client` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Client-Package Information';

-- Dumping data for table radix.radix_client_package: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_client_package` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_client_package` ENABLE KEYS */;


-- Dumping structure for table radix.radix_oauth_access_token
DROP TABLE IF EXISTS `radix_oauth_access_token`;
CREATE TABLE IF NOT EXISTS `radix_oauth_access_token` (
  `token_id` varchar(256) NOT NULL,
  `token` blob NOT NULL,
  `authentication_id` varchar(256) NOT NULL,
  `authentication` blob NOT NULL,
  `refresh_token` varchar(256) NOT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  `username` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `create_date` timestamp NULL DEFAULT NULL,
  `expires_in` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`token_id`(255)),
  KEY `client_id` (`client_id`(255)) USING HASH,
  KEY `username` (`username`(255)) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table radix.radix_oauth_access_token: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_oauth_access_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_oauth_access_token` ENABLE KEYS */;


-- Dumping structure for table radix.radix_oauth_refresh_token
DROP TABLE IF EXISTS `radix_oauth_refresh_token`;
CREATE TABLE IF NOT EXISTS `radix_oauth_refresh_token` (
  `token_id` varchar(256) NOT NULL,
  `token` blob NOT NULL,
  `authentication` blob NOT NULL,
  PRIMARY KEY (`token_id`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table radix.radix_oauth_refresh_token: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_oauth_refresh_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_oauth_refresh_token` ENABLE KEYS */;


-- Dumping structure for table radix.radix_package
DROP TABLE IF EXISTS `radix_package`;
CREATE TABLE IF NOT EXISTS `radix_package` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH,
  CONSTRAINT `radix_package_ibfk_1` FOREIGN KEY (`businessPlatformID`) REFERENCES `radix_business_platform` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Package Information';

-- Dumping data for table radix.radix_package: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package` ENABLE KEYS */;


-- Dumping structure for table radix.radix_package_policy
DROP TABLE IF EXISTS `radix_package_policy`;
CREATE TABLE IF NOT EXISTS `radix_package_policy` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `packageID` binary(16) NOT NULL,
  `policyTypeID` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `properties` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH,
  KEY `packageID` (`packageID`,`policyTypeID`) USING HASH,
  KEY `policyTypeID` (`policyTypeID`),
  CONSTRAINT `radix_package_policy_ibfk_1` FOREIGN KEY (`businessPlatformID`) REFERENCES `radix_business_platform` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_package_policy_ibfk_2` FOREIGN KEY (`packageID`) REFERENCES `radix_package` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_package_policy_ibfk_3` FOREIGN KEY (`policyTypeID`) REFERENCES `radix_policy_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Package-Policy Information';

-- Dumping data for table radix.radix_package_policy: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package_policy` ENABLE KEYS */;


-- Dumping structure for table radix.radix_package_serviceapi
DROP TABLE IF EXISTS `radix_package_serviceapi`;
CREATE TABLE IF NOT EXISTS `radix_package_serviceapi` (
  `packageID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`packageID`,`serviceAPIID`) USING HASH,
  KEY `serviceAPIID` (`serviceAPIID`),
  CONSTRAINT `radix_package_serviceapi_ibfk_1` FOREIGN KEY (`packageID`) REFERENCES `radix_package` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_package_serviceapi_ibfk_2` FOREIGN KEY (`serviceAPIID`) REFERENCES `radix_service_apis_serviceapi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Package-ServiceAPI Information';

-- Dumping data for table radix.radix_package_serviceapi: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package_serviceapi` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package_serviceapi` ENABLE KEYS */;


-- Dumping structure for table radix.radix_package_serviceapi_policy
DROP TABLE IF EXISTS `radix_package_serviceapi_policy`;
CREATE TABLE IF NOT EXISTS `radix_package_serviceapi_policy` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `packageID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `policyTypeID` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `properties` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH,
  KEY `packageID` (`packageID`,`serviceAPIID`,`policyTypeID`) USING HASH,
  KEY `policyTypeID` (`policyTypeID`),
  KEY `serviceAPIID` (`serviceAPIID`),
  CONSTRAINT `radix_package_serviceapi_policy_ibfk_1` FOREIGN KEY (`businessPlatformID`) REFERENCES `radix_business_platform` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_package_serviceapi_policy_ibfk_2` FOREIGN KEY (`packageID`) REFERENCES `radix_package` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_package_serviceapi_policy_ibfk_3` FOREIGN KEY (`policyTypeID`) REFERENCES `radix_policy_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_package_serviceapi_policy_ibfk_4` FOREIGN KEY (`serviceAPIID`) REFERENCES `radix_service_apis_serviceapi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Package-ServiceAPI-Policy Information';

-- Dumping data for table radix.radix_package_serviceapi_policy: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_package_serviceapi_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_package_serviceapi_policy` ENABLE KEYS */;


-- Dumping structure for table radix.radix_policy_type
DROP TABLE IF EXISTS `radix_policy_type`;
CREATE TABLE IF NOT EXISTS `radix_policy_type` (
  `id` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `priority` int(3) DEFAULT '0',
  `properties` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isActivated` enum('Y','N') DEFAULT 'Y',
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Policy Information';

-- Dumping data for table radix.radix_policy_type: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_policy_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_policy_type` ENABLE KEYS */;


-- Dumping structure for table radix.radix_scope_repository
DROP TABLE IF EXISTS `radix_scope_repository`;
CREATE TABLE IF NOT EXISTS `radix_scope_repository` (
  `serviceAPIID` binary(16) NOT NULL,
  `clientID` binary(16) NOT NULL,
  `packageID` binary(16) NOT NULL,
  `apiKey` varchar(200) NOT NULL,
  `packageParams` text,
  `policyList` text,
  `status` text,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  PRIMARY KEY (`serviceAPIID`,`clientID`,`packageID`) USING HASH,
  KEY `packageID` (`packageID`),
  KEY `clientID` (`clientID`),
  CONSTRAINT `radix_scope_repository_ibfk_1` FOREIGN KEY (`serviceAPIID`) REFERENCES `radix_service_apis_serviceapi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_scope_repository_ibfk_2` FOREIGN KEY (`packageID`) REFERENCES `radix_package` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_scope_repository_ibfk_3` FOREIGN KEY (`clientID`) REFERENCES `radix_client` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Scope Repository';

-- Dumping data for table radix.radix_scope_repository: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_scope_repository` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_scope_repository` ENABLE KEYS */;


-- Dumping structure for table radix.radix_service
DROP TABLE IF EXISTS `radix_service`;
CREATE TABLE IF NOT EXISTS `radix_service` (
  `id` binary(16) NOT NULL,
  `businessPlatformID` binary(16) NOT NULL,
  `partnerID` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `version` int(11) NOT NULL,
  `resourceOwner` enum('USER','PARTNER') NOT NULL,
  `resourceAuthUrl` varchar(256) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `businessPlatformID_2` (`businessPlatformID`,`name`,`version`) USING HASH,
  KEY `businessPlatformID` (`businessPlatformID`,`partnerID`) USING HASH,
  CONSTRAINT `radix_service_ibfk_1` FOREIGN KEY (`businessPlatformID`) REFERENCES `radix_business_platform` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Service Information';

-- Dumping data for table radix.radix_service: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service` ENABLE KEYS */;


-- Dumping structure for table radix.radix_service_apis_mapping
DROP TABLE IF EXISTS `radix_service_apis_mapping`;
CREATE TABLE IF NOT EXISTS `radix_service_apis_mapping` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `partnerAPIID` binary(16) NOT NULL,
  `mappingType` enum('PARAMETER','RESULT') NOT NULL,
  `mapping` text NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `serviceID` (`serviceID`),
  KEY `serviceAPIID` (`serviceAPIID`),
  KEY `partnerAPIID` (`partnerAPIID`),
  CONSTRAINT `radix_service_apis_mapping_ibfk_1` FOREIGN KEY (`serviceID`) REFERENCES `radix_service` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_service_apis_mapping_ibfk_2` FOREIGN KEY (`serviceAPIID`) REFERENCES `radix_service_apis_serviceapi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_service_apis_mapping_ibfk_3` FOREIGN KEY (`partnerAPIID`) REFERENCES `radix_service_apis_partnerapi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='API Mapping Specification';

-- Dumping data for table radix.radix_service_apis_mapping: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_apis_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_apis_mapping` ENABLE KEYS */;


-- Dumping structure for table radix.radix_service_apis_partnerapi
DROP TABLE IF EXISTS `radix_service_apis_partnerapi`;
CREATE TABLE IF NOT EXISTS `radix_service_apis_partnerapi` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `name` varchar(50) NOT NULL,
  `transportType` enum('REST','FILE','DBMS','SOAP','JMS','AWS') NOT NULL,
  `defaultTransformType` enum('XML','JSON','CSV','FORM') NOT NULL,
  `protocolType` varchar(15) NOT NULL,
  `protocolMeta` text,
  `parameters` text NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `serviceID` (`serviceID`,`name`) USING HASH,
  CONSTRAINT `radix_service_apis_partnerapi_ibfk_1` FOREIGN KEY (`serviceID`) REFERENCES `radix_service` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='PartnerAPI Specification';

-- Dumping data for table radix.radix_service_apis_partnerapi: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_apis_partnerapi` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_apis_partnerapi` ENABLE KEYS */;


-- Dumping structure for table radix.radix_service_apis_serviceapi
DROP TABLE IF EXISTS `radix_service_apis_serviceapi`;
CREATE TABLE IF NOT EXISTS `radix_service_apis_serviceapi` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `apiKey` varchar(256) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `routingMethodType` enum('DIRECT','PIPELINING','PARALLEL') NOT NULL,
  `transportType` enum('REST','FILE','DBMS','SOAP','JMS','AWS') NOT NULL,
  `defaultTransformType` enum('XML','JSON','CSV','FORM') NOT NULL,
  `protocolType` varchar(15) NOT NULL,
  `protocolMeta` text,
  `parameters` text NOT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  KEY `serviceID` (`serviceID`,`name`) USING HASH,
  CONSTRAINT `radix_service_apis_serviceapi_ibfk_1` FOREIGN KEY (`serviceID`) REFERENCES `radix_service` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='ServiceAPI Specification';

-- Dumping data for table radix.radix_service_apis_serviceapi: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_apis_serviceapi` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_apis_serviceapi` ENABLE KEYS */;


-- Dumping structure for table radix.radix_service_routing_direct
DROP TABLE IF EXISTS `radix_service_routing_direct`;
CREATE TABLE IF NOT EXISTS `radix_service_routing_direct` (
  `id` binary(16) NOT NULL,
  `serviceID` binary(16) NOT NULL,
  `serviceAPIID` binary(16) NOT NULL,
  `partnerAPIID` binary(16) NOT NULL,
  `parameterMappingID` binary(16) DEFAULT NULL,
  `resultMappingID` binary(16) DEFAULT NULL,
  `regDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `isValid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `invalidStatus` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `serviceID` (`serviceID`,`serviceAPIID`,`partnerAPIID`) USING HASH,
  KEY `serviceAPIID` (`serviceAPIID`),
  KEY `parameterMappingID` (`parameterMappingID`),
  KEY `resultMappingID` (`resultMappingID`),
  CONSTRAINT `radix_service_routing_direct_ibfk_1` FOREIGN KEY (`serviceID`) REFERENCES `radix_service` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_service_routing_direct_ibfk_2` FOREIGN KEY (`serviceAPIID`) REFERENCES `radix_service_apis_serviceapi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_service_routing_direct_ibfk_3` FOREIGN KEY (`parameterMappingID`) REFERENCES `radix_service_apis_mapping` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `radix_service_routing_direct_ibfk_4` FOREIGN KEY (`resultMappingID`) REFERENCES `radix_service_apis_mapping` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='Direct Routing ServiceAPI Information';

-- Dumping data for table radix.radix_service_routing_direct: ~0 rows (approximately)
/*!40000 ALTER TABLE `radix_service_routing_direct` DISABLE KEYS */;
/*!40000 ALTER TABLE `radix_service_routing_direct` ENABLE KEYS */;


-- Dumping structure for function radix.UUIDTOBIN
DROP FUNCTION IF EXISTS `UUIDTOBIN`;
DELIMITER //
CREATE FUNCTION `UUIDTOBIN`(uuid CHAR(36)) RETURNS binary(16)
    DETERMINISTIC
BEGIN
RETURN UNHEX(REPLACE(uuid,'-',''));
END//
DELIMITER ;
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;


use radix;

delete from `radix_business_platform`;
delete from `radix_client`;

INSERT INTO `radix_business_platform` (`id`, `businessPlatformKey`, `secret`, `description`, `domain`, `redirect_uri`, `regDate`, `isValid`, `invalidStatus`) VALUES
	(uuidtobin('59ffa6f4-0901-4ffc-82ad-44687540ab4b'), '59ffa6f4-0901-4ffc-82ad-44687540ab4b', '21334d4ffb7994f5094eb41b5a70dd3a165780f9', '', 'api.withapi.com', 'http://redirect.withapi.com', '2012-06-26 11:51:49', 'Y', NULL);

INSERT INTO `radix_client` (`id`, `businessPlatformID`, `clientKey`, `secret`, `partnerID`, `type`, `redirect_uri`, `regDate`, `isValid`, `invalidStatus`) VALUES
	(uuidtobin('59ffa6f4-0901-4ffc-82ad-44687540ab4b'), uuidtobin('59ffa6f4-0901-4ffc-82ad-44687540ab4b'), '59ffa6f4-0901-4ffc-82ad-44687540ab4b', '21334d4ffb7994f5094eb41b5a70dd3a165780f9', 'appleTree', 'P', 'http://redirect.withapi.com', '2012-06-26 11:51:49', 'Y', NULL);

INSERT INTO `radix_policy_type` (`id`, `name`, `description`, `priority`, `properties`, `regDate`, `updateDate`, `isActivated`, `isValid`, `invalidStatus`) VALUES
('usageLimit', 'usageLimit', 'limit call count policy', 8, '["duration","maxCount","condition","startTimestamp","endTimestamp"]', '2012-06-26 11:51:51', '2012-06-26 11:51:51', 'Y', 'Y', NULL),
('usageTerm', 'usageTerm', 'limit term policy', 9, '["startTimestamp","endTimestamp"]', '2012-06-26 11:51:51', '2012-06-26 11:51:51', 'Y', 'Y', NULL);
