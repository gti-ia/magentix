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
  `positionName` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idposition`) ,
  UNIQUE INDEX `position_UNIQUE` (`positionName` ASC) )
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
-- Table `thomas`.`accessibility`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`accessibility` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`accessibility` (
  `idaccessibility` INT NOT NULL AUTO_INCREMENT ,
  `accessibility` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`idaccessibility`) ,
  UNIQUE INDEX `accesiblity_UNIQUE` (`accessibility` ASC) )
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
  `idaccessibility` INT NOT NULL ,
  `idvisibility` INT NOT NULL ,
  PRIMARY KEY (`idroleList`) ,
  INDEX `fk_roleList_unitList1` (`idunitList` ASC) ,
  INDEX `fk_roleList_position1` (`idposition` ASC) ,
  INDEX `fk_roleList_accesibility1` (`idaccessibility` ASC) ,
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
    FOREIGN KEY (`idaccessibility` )
    REFERENCES `thomas`.`accessibility` (`idaccessibility` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_roleList_visibility1`
    FOREIGN KEY (`idvisibility` )
    REFERENCES `thomas`.`visibility` (`idvisibility` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`agentList`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`agentList` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`agentList` (
  `idagentList` INT NOT NULL AUTO_INCREMENT ,
  `agentName` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idagentList`) ,
  UNIQUE INDEX `agentName_UNIQUE` (`agentName` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`agentPlayList`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`agentPlayList` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`agentPlayList` (
  `idroleList` INT NOT NULL ,
  `idagentList` INT NOT NULL ,
  PRIMARY KEY (`idroleList`, `idagentList`) ,
  INDEX `fk_agentPlayList_roleList1` (`idroleList` ASC) ,
  INDEX `fk_agentPlayList_agentList` (`idagentList` ASC) ,
  CONSTRAINT `fk_agentPlayList_roleList1`
    FOREIGN KEY (`idroleList` )
    REFERENCES `thomas`.`roleList` (`idroleList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_agentPlayList_agentList`
    FOREIGN KEY (`idagentList` )
    REFERENCES `thomas`.`agentList` (`idagentList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`deontic`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`deontic` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`deontic` (
  `iddeontic` INT NOT NULL AUTO_INCREMENT ,
  `deonticdesc` CHAR NULL ,
  PRIMARY KEY (`iddeontic`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`targetType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`targetType` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`targetType` (
  `idtargetType` INT NOT NULL AUTO_INCREMENT ,
  `targetName` VARCHAR(45) NOT NULL ,
  `targetTable` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idtargetType`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`actionNorm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`actionNorm` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`actionNorm` (
  `idactionNorm` INT NOT NULL AUTO_INCREMENT ,
  `description` VARCHAR(45) NOT NULL ,
  `numParams` INT NULL ,
  PRIMARY KEY (`idactionNorm`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`normList`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`normList` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`normList` (
  `idnormList` INT NOT NULL AUTO_INCREMENT ,
  `idunitList` INT NOT NULL ,
  `normName` VARCHAR(45) NOT NULL ,
  `iddeontic` INT NOT NULL ,
  `idtargetType` INT NOT NULL ,
  `targetValue` INT NOT NULL ,
  `idactionnorm` INT NOT NULL ,
  `normContent` TEXT NOT NULL ,
  `normRule` TEXT NOT NULL ,
  PRIMARY KEY (`idnormList`) ,
  INDEX `fk_normList_unitList1` (`idunitList` ASC) ,
  INDEX `fk_normList_deontic1` (`iddeontic` ASC) ,
  INDEX `fk_normList_targetType1` (`idtargetType` ASC) ,
  INDEX `fk_normList_actionNorm1` (`idactionnorm` ASC) ,
  CONSTRAINT `fk_normList_unitList1`
    FOREIGN KEY (`idunitList` )
    REFERENCES `thomas`.`unitList` (`idunitList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_normList_deontic1`
    FOREIGN KEY (`iddeontic` )
    REFERENCES `thomas`.`deontic` (`iddeontic` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_normList_targetType1`
    FOREIGN KEY (`idtargetType` )
    REFERENCES `thomas`.`targetType` (`idtargetType` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_normList_actionNorm1`
    FOREIGN KEY (`idactionnorm` )
    REFERENCES `thomas`.`actionNorm` (`idactionNorm` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`actionNormParam`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`actionNormParam` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`actionNormParam` (
  `idactionNormParam` INT NOT NULL AUTO_INCREMENT ,
  `idnormList` INT NOT NULL ,
  `idactionNorm` INT NOT NULL ,
  `value` VARCHAR(45) NULL ,
  PRIMARY KEY (`idactionNormParam`, `idnormList`) ,
  INDEX `fk_actionNormParam_actionNorm` (`idactionNorm` ASC) ,
  INDEX `fk_actionNormParam_normList` (`idnormList` ASC) ,
  CONSTRAINT `fk_actionNormParam_actionNorm`
    FOREIGN KEY (`idactionNorm` )
    REFERENCES `thomas`.`actionNorm` (`idactionNorm` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_actionNormParam_normList`
    FOREIGN KEY (`idnormList` )
    REFERENCES `thomas`.`normList` (`idnormList` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `thomas`.`reservedWordList`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `thomas`.`reservedWordList` ;

CREATE  TABLE IF NOT EXISTS `thomas`.`reservedWordList` (
  `idreservedWordList` INT NOT NULL AUTO_INCREMENT ,
  `reservedWord` VARCHAR(45) NULL ,
  PRIMARY KEY (`idreservedWordList`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


-- -----------------------------------------------------
-- Inserts unit types
-- (Flat, Team, Hierarchy)
-- -----------------------------------------------------


INSERT INTO `unitType` (`unitTypeName`) VALUES
  ('flat');
INSERT INTO `unitType` (`unitTypeName`) VALUES
  ('team');
INSERT INTO `unitType` (`unitTypeName`) VALUES
  ('hierarchy');

-- -----------------------------------------------
-- Inserts position types
-- (Member, Creator, Supervisor, Subordinate)
-- -----------------------------------------------
INSERT INTO `position` (`positionName`) VALUES
  ('member');
INSERT INTO `position` (`positionName`) VALUES
  ('creator');
INSERT INTO `position` (`positionName`) VALUES
  ('supervisor');
INSERT INTO `position` (`positionName`) VALUES
  ('subordinate');

-- -----------------------------------------------
-- Inserts visibility types
-- (Public, Private)
-- -----------------------------------------------
INSERT INTO `visibility` (`visibility`) VALUES
  ('public');
INSERT INTO `visibility` (`visibility`) VALUES
  ('private');

-- ----------------------------------------------- 
-- Inserts accessibility types
-- (External, Internal)
-- -----------------------------------------------
INSERT INTO `accessibility` (`accessibility`) VALUES
  ('external');
INSERT INTO `accessibility` (`accessibility`) VALUES
  ('internal');

-- ---------------------------------------------
-- Inserts unit virtual
-- * Unit type=flat
-- * Parent unit=none
-- ---------------------------------------------


INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES
  ('virtual',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'));

INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES
  ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'virtual'));

-- ----------------------------------------------
-- Inserts role participant in unit
-- * RoleName=Participant
-- * Unit=virtual
-- * Visibility=Public
-- * Accessibility=External
-- ---------------------------------------------

INSERT INTO `roleList`
(`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`)
VALUES
  ('participant',(SELECT idunitList FROM unitList WHERE unitName = 'virtual'),
  (SELECT idposition FROM position WHERE positionName = 'creator'),
  (SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),
  (SELECT idvisibility FROM visibility WHERE visibility = 'public'));
  
-- ----------------------------------------------------
-- Inserts default target types values:
-- agentName, roleName, targetName
-- -----------------------------------------------------

INSERT INTO `targetType`
(`targetName`,`targetTable`)
VALUES
  ('agentName','agentList');  
  
INSERT INTO `targetType`
(`targetName`,`targetTable`)
VALUES
  ('roleName','roleList');
  
INSERT INTO `targetType`
(`targetName`,`targetTable`)
VALUES
  ('positionName','position');

-- ----------------------------------------------------
-- Inserts default deontic values:
-- f,o,p
-- -----------------------------------------------------
  
INSERT INTO `deontic`
(`deonticdesc`)
VALUES
  ('f');  

  
INSERT INTO `deontic`
(`deonticdesc`)
VALUES
  ('o');  
  
INSERT INTO `deontic`
(`deonticdesc`)
VALUES
  ('p');  
  
-- ----------------------------------------------------
-- Inserts default oms actions 
-- -----------------------------------------------------
INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('registerUnit',5);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('deregisterUnit',2);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('registerRole',6);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('deregisterRole',3);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('registerNorm',3);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('deregisterNorm',3);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('allocateRole',4);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('deallocateRole',4);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('joinUnit',3);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('informAgentRole',2);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('informMembers',4);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('InformQuantityMembers',4);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('informUnit',2);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('informUnitRoles',2);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('informTargetNorms',4);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('informRole',3);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('informNorm',3);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('acquireRole',3);

INSERT INTO `actionNorm`
(`description`,`numParams`)
VALUES ('leaveRole',3);

-- ----------------------------------------------------
-- Inserts reserved Words related with
-- the normative language
-- -----------------------------------------------------


INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('flat');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('team');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hierarchy');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('public');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('private');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('external');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('internal');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('creator');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('member');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('supervisor');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('subordinate');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('registerUnit');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('deregisterUnit');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('registerRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('deregisterRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('registerNorm');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('deregisterNorm');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('allocateRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('deallocateRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('joinUnit');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informAgentRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informMembers');


INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informQuantityMembers');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informUnit');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informUnitRoles');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informTargetNorms');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('informNorm');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('acquireRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('leaveRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('isNorm');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasDeontic');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasTarget');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasAction');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('isRole');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasAccessibility');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasVisibility');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasPosition');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('isUnit');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasType');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('hasParent');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('div');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('mod');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('not');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('_');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('agentName');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('roleName');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('positionName');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('o');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('f');

INSERT INTO `reservedWordList`
(`reservedWord`)
VALUES ('p');