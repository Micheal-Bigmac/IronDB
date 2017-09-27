-- ----------------------------
-- Table structure for irondbcolumns
-- ----------------------------
# DROP TABLE IF EXISTS `irondbcolumns`;
CREATE TABLE `irondbcolumns` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `column_name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `irondb_id` int(11) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `suppor_function` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `irondb_id` (`irondb_id`),
  CONSTRAINT `irondbcolumns_ibfk_1` FOREIGN KEY (`irondb_id`) REFERENCES `irondbtables` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for irondbtables
-- ----------------------------
# DROP TABLE IF EXISTS `irondbtables`;
CREATE TABLE `irondbtables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tablename` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `storage_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

