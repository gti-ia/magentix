SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `thomas` ;
CREATE SCHEMA IF NOT EXISTS `thomas` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `thomas` ;

-- -----------------------------------------------------
-- Table `thomas`.`unitType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`unitType` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`unitType` (
  `idunitType` INT NOT NULL AUTO_INCREMENT ,
  `unitTypeName` VARCHAR(45) NULL ,
  PRIMARY KEY (`idunitType`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`unitList`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`unitList` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`unitList` (
  `idunitList` INT NOT NULL AUTO_INCREMENT ,
  `unitName` VARCHAR(45) NOT NULL ,
  `idunitType` INT NOT NULL ,
  PRIMARY KEY (`idunitList`) ,
  UNIQUE INDEX `unitName_UNIQUE` (`unitName` ASC) ,
  INDEX `fk_unitList_unitType` (`idunitType` ASC) ,
  UNIQUE INDEX `idunitList_UNIQUE` (`idunitList` ASC) ,
  CONSTRAINT `fk_unitList_unitType`
    FOREIGN KEY (`idunitType` )
    REFERENCES `thomas`.`unitType` (`idunitType` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`unitHierarchy`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`unitHierarchy` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`unitHierarchy` (
  `idParentUnit` INT NOT NULL ,
  `idChildUnit` INT NOT NULL ,
  PRIMARY KEY (`idParentUnit`, `idChildUnit`) ,
  INDEX `fk_unitHierarchy_unitList1` (`idParentUnit` ASC) ,
  INDEX `fk_unitHierarchy_unitList2` (`idChildUnit` ASC) ,
  CONSTRAINT `fk_unitHierarchy_unitList1`
    FOREIGN KEY (`idParentUnit` )
    REFERENCES `thomas`.`unitList` (`idunitList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_unitHierarchy_unitList2`
    FOREIGN KEY (`idChildUnit` )
    REFERENCES `thomas`.`unitList` (`idunitList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`position`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`position` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`position` (
  `idposition` INT NOT NULL AUTO_INCREMENT ,
  `position` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idposition`) ,
  UNIQUE INDEX `position_UNIQUE` (`position` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`visibility`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`visibility` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`visibility` (
  `idvisibility` INT NOT NULL AUTO_INCREMENT ,
  `visibility` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`idvisibility`) ,
  UNIQUE INDEX `visibility_UNIQUE` (`visibility` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`accesibility`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`accesibility` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`accesibility` (
  `idaccesibility` INT NOT NULL AUTO_INCREMENT ,
  `accesibility` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`idaccesibility`) ,
  UNIQUE INDEX `accesiblity_UNIQUE` (`accesibility` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`roleList`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`roleList` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`roleList` (
  `idroleList` INT NOT NULL AUTO_INCREMENT ,
  `roleName` VARCHAR(45) NOT NULL ,
  `idunitList` INT NOT NULL ,
  `idposition` INT NOT NULL ,
  `idaccesibility` INT NOT NULL ,
  `idvisibility` INT NOT NULL ,
  PRIMARY KEY (`idroleList`) ,
  INDEX `fk_roleList_unitList1` (`idunitList` ASC) ,
  INDEX `fk_roleList_position1` (`idposition` ASC) ,
  INDEX `fk_roleList_accesibility1` (`idaccesibility` ASC) ,
  INDEX `fk_roleList_visibility1` (`idvisibility` ASC) ,
  CONSTRAINT `fk_roleList_unitList1`
    FOREIGN KEY (`idunitList` )
    REFERENCES `thomas`.`unitList` (`idunitList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_roleList_position1`
    FOREIGN KEY (`idposition` )
    REFERENCES `thomas`.`position` (`idposition` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_roleList_accesibility1`
    FOREIGN KEY (`idaccesibility` )
    REFERENCES `thomas`.`accesibility` (`idaccesibility` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_roleList_visibility1`
    FOREIGN KEY (`idvisibility` )
    REFERENCES `thomas`.`visibility` (`idvisibility` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`agentPlayList`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`agentPlayList` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`agentPlayList` (
  `agentName` VARCHAR(45) NOT NULL ,
  `idroleList` INT NOT NULL ,
  PRIMARY KEY (`agentName`, `idroleList`) ,
  INDEX `fk_agentPlayList_roleList1` (`idroleList` ASC) ,
  CONSTRAINT `fk_agentPlayList_roleList1`
    FOREIGN KEY (`idroleList` )
    REFERENCES `thomas`.`roleList` (`idroleList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- ----------------------------------------------
--	Inserts unit types 
--	(Flat, Team, Hierarchy)
-- ----------------------------------------------
INSERT INTO `unitType` (`unitTypeName`) VALUES 
 ('flat');
INSERT INTO `unitType` (`unitTypeName`) VALUES 
 ('team');
INSERT INTO `unitType` (`unitTypeName`) VALUES 
 ('hierarchy');

-- -----------------------------------------------
--	Inserts position types 
--	(Member, Creator, Supervisor, Subordinate)
-- -----------------------------------------------
INSERT INTO `position` (`position`) VALUES 
 ('member');
INSERT INTO `position` (`position`) VALUES 
 ('creator');
INSERT INTO `position` (`position`) VALUES 
 ('supervisor');
INSERT INTO `position` (`position`) VALUES 
 ('subordinate');

-- -----------------------------------------------
--	Inserts visibility types 
--	(Public, Private)
-- -----------------------------------------------
INSERT INTO `visibility` (`visibility`) VALUES 
 ('public');
INSERT INTO `visibility` (`visibility`) VALUES 
 ('private');

-- -----------------------------------------------
--	Inserts accesibility types 
--	(External, Internal)
-- -----------------------------------------------
INSERT INTO `accesibility` (`accesibility`) VALUES 
 ('external');
INSERT INTO `accesibility` (`accesibility`) VALUES 
 ('internal');

-- ---------------------------------------------
--	Inserts unit virtual 
--	* Unit type=flat
--	* Parent unit=none
-- ---------------------------------------------


INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES 
 ('virtual',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'));

INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES 
 ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'virtual'));

-- ---------------------------------------------
--	Inserts role participant in unit 
--	* RoleName=Participant
--	* Unit=virtual
--	* Position=Creator
--	* Visibility=Public
--	* Accessibility=External
-- ---------------------------------------------

INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccesibility`,`idvisibility`) VALUES 
 ('participant',(SELECT idunitList FROM unitList WHERE unitName = 'virtual'),
 (SELECT idposition FROM position WHERE position = 'creator'), 
 (SELECT idaccesibility FROM accesibility WHERE accesibility = 'external'), 
 (SELECT idvisibility FROM visibility WHERE visibility = 'public'));
