-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema pruebas
--

CREATE DATABASE IF NOT EXISTS thomas;
USE thomas;

--
-- Definition of table `serviceProfileID`
--
DROP TABLE IF EXISTS `serviceprofileid`;
CREATE TABLE `thomas`.`serviceprofileid` (
  `serviceprofileid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `urlprofile` TEXT  NOT NULL,
	`profilename` TEXT  NOT NULL,
  PRIMARY KEY (`serviceprofileid`)
)
ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `serviceProcessID`
--
DROP TABLE IF EXISTS `serviceprocessid`;
CREATE TABLE `thomas`.`serviceprocessid` (
  `serviceprocessid` TEXT  NOT NULL,
  `urlprocess` TEXT  NOT NULL,
  `servicenumid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
`serviceprofileid` TEXT  NOT NULL,
`processname` TEXT  NOT NULL,
  PRIMARY KEY (`servicenumid`)
)
ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `entity`
--

DROP TABLE IF EXISTS `entity`;
CREATE TABLE `entity` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `entityid` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Definition of table `entityplaylist`
--

DROP TABLE IF EXISTS `entityplaylist`;
CREATE TABLE `entityplaylist` (
  `role` int(10) unsigned NOT NULL,
  `unit` int(10) unsigned NOT NULL,
  `entity` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`role`,`unit`,`entity`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `incompatibilitynorm`
--

DROP TABLE IF EXISTS `incompatibilitynorm`;
CREATE TABLE `incompatibilitynorm` (
  `role1id` int(10) unsigned NOT NULL,
  `role2id` int(10) unsigned NOT NULL,
  `normid` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`role1id`,`role2id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `maxcardinalitynorm`
--

DROP TABLE IF EXISTS `maxcardinalitynorm`;
CREATE TABLE `maxcardinalitynorm` (
  `role1id` int(10) unsigned NOT NULL,
  `role2id` int(10) unsigned NOT NULL,
  `max` int(10) unsigned NOT NULL,
  `normid` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`role1id`,`role2id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `norm`
--

DROP TABLE IF EXISTS `norm`;
CREATE TABLE `norm` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `normid` varchar(45) NOT NULL,
  `normcontent` varchar(150) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;


--
-- Definition of table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `roleid` varchar(45) NOT NULL,
  `position` varchar(45) NOT NULL,
  `accessibility` varchar(45) NOT NULL,
  `visibility` varchar(45) NOT NULL,
  `inheritance` int(10) unsigned NOT NULL,
  `unit` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `role`
--

/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` (`id`,`roleid`,`position`,`accessibility`,`visibility`,`inheritance`,`unit`) VALUES 
 (1,'member','member','external','public',0,1);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;



--
-- Definition of table `simplerequestnorm`
--

DROP TABLE IF EXISTS `simplerequestnorm`;
CREATE TABLE `simplerequestnorm` (
  `roleid` int(10) unsigned NOT NULL,
  `serviceid` int(10) unsigned NOT NULL,
  `deonticconcept` varchar(45) NOT NULL,
  `normid` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`roleid`,`serviceid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `unit`
--

DROP TABLE IF EXISTS `unit`;
CREATE TABLE `unit` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `unitid` varchar(45) NOT NULL,
  `parentunit` int(10) unsigned default NULL,
  `type` varchar(45) NOT NULL,
  `goal` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `unit`
--

/*!40000 ALTER TABLE `unit` DISABLE KEYS */;
INSERT INTO `unit` (`id`,`unitid`,`parentunit`,`type`,`goal`) VALUES 
 (1,'virtual',0,'flat','\"\"');
/*!40000 ALTER TABLE `unit` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
