-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt


--
-- Create schema security
--

CREATE DATABASE IF NOT EXISTS security;
USE security;

--
-- Definition of table `serviceProfileID`
--
CREATE TABLE IF NOT EXISTS `security`.`registers`(
  `user` TEXT  NOT NULL,
  `dateFirst` TEXT  NOT NULL,
  `dateLast` TEXT  NOT NULL,
  `agent` varchar(150) NOT NULL,
  PRIMARY KEY (`agent`)
)
ENGINE=InnoDB DEFAULT CHARSET=latin1;



