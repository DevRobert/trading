-- CREATE SCHEMA `trading` DEFAULT CHARACTER SET utf8 ;
-- use `trading`;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Table `client`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `client` (
  `Id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `Name_UNIQUE` (`Name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `account` (
  `Id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ClientId` INT UNSIGNED NOT NULL,
  `SeedCapital` DOUBLE NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `fk_account_client1_idx` (`ClientId` ASC),
  CONSTRAINT `fk_account_client1`
    FOREIGN KEY (`ClientId`)
    REFERENCES `client` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `transaction_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `transaction_type` (
  `Id` INT UNSIGNED NOT NULL,
  `Name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `Name_UNIQUE` (`Name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `instrument`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `instrument` (
  `Isin` VARCHAR(12) NOT NULL,
  `Name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`Isin`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `transaction` (
  `Id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `AccountId` INT UNSIGNED NOT NULL,
  `TransactionTypeId` INT UNSIGNED NOT NULL,
  `Quantity` INT NOT NULL,
  `TotalPrice` DOUBLE NOT NULL,
  `Commission` DOUBLE NOT NULL,
  `Isin` VARCHAR(12) NOT NULL,
  `Created` DATETIME NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `fk_transaction_transaction_type1_idx` (`TransactionTypeId` ASC),
  INDEX `fk_transaction_instrument1_idx` (`Isin` ASC),
  INDEX `fk_transaction_account1_idx` (`AccountId` ASC),
  CONSTRAINT `fk_transaction_transaction_type1`
    FOREIGN KEY (`TransactionTypeId`)
    REFERENCES `transaction_type` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_transaction_instrument1`
    FOREIGN KEY (`Isin`)
    REFERENCES `instrument` (`Isin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_transaction_account1`
    FOREIGN KEY (`AccountId`)
    REFERENCES `account` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

INSERT INTO `transaction_type` (`Id`, `Name`) VALUES ('0', 'Buy');
INSERT INTO `transaction_type` (`Id`, `Name`) VALUES ('1', 'Sell');

INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000A1EWWW0', 'adidas');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0008404005', 'Allianz');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000BASF111', 'BASF');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000BAY0017', 'Bayer');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005200000', 'Beiersdorf');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005190003', 'BMW');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000CBK1001', 'Commerzbank');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005439004', 'Continental');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0006062144', 'Covestro');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0007100000', 'Daimler');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005140008', 'Deutsche Bank');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005810055', 'Deutsche Börse');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005552004', 'Deutsche Post');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005557508', 'Deutsche Telekom');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000ENAG999', 'E.ON');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005785604', 'Fresenius');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0005785802', 'Fresenius Medical Care');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0006047004', 'HeidelbergCement');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0006048432', 'Henkel vz.');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0006231004', 'Infineon');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000A2E4L75', 'Linde');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0008232125', 'Lufthansa');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0006599905', 'Merck');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0008430026', 'Münchener Rückversicherungs-Gesellschaft');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000PSM7770', 'ProSiebenSat.1 Media');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0007037129', 'RWE');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0007164600', 'SAP');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0007236101', 'Siemens');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0007500001', 'thyssenkrupp');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE0007664039', 'Volkswagen (VW) vz.');
INSERT INTO `instrument` (`Isin`, `Name`) VALUES ('DE000A1ML7J1', 'Vonovia');

INSERT INTO `client` (`Id`, `Name`) VALUES (1, 'Demo-Client 1');
INSERT INTO `client` (`Id`, `Name`) VALUES (2, 'Demo-Client 2');

INSERT INTO `account` (`Id`, `ClientId`, `SeedCapital`) VALUES (1, 2, 10000);
INSERT INTO `account` (`Id`, `ClientId`, `SeedCapital`) VALUES (2, 2, 10000);

INSERT INTO `transaction` (`Id`, `AccountId`, `TransactionTypeId`, `Quantity`, `TotalPrice`, `Commission`, `Isin`, `Created`) VALUES (1, 2, 0, 10, 5000, 10, 'DE0008430026', '2018-04-16 09:00:00');
INSERT INTO `transaction` (`Id`, `AccountId`, `TransactionTypeId`, `Quantity`, `TotalPrice`, `Commission`, `Isin`, `Created`) VALUES (2, 2, 1, 10, 5500, 10, 'DE0008430026', '2018-04-17 09:00:00');
