CREATE TABLE `profit_category` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `Name_UNIQUE` (`Name` ASC))
ENGINE = InnoDB;

INSERT INTO `profit_category` (`Id`, `Name`) VALUES ('1', 'Sale');
INSERT INTO `profit_category` (`Id`, `Name`) VALUES ('2', 'Dividends');

ALTER TABLE `transaction`
CHANGE COLUMN `Quantity` `Quantity` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `TotalPrice` `TotalPrice` DOUBLE NULL DEFAULT NULL ,
CHANGE COLUMN `Commission` `Commission` DOUBLE NULL DEFAULT NULL ,
CHANGE COLUMN `Isin` `Isin` VARCHAR(12) NULL DEFAULT NULL,
ADD COLUMN `ProfitCategoryId` INT(11) NULL DEFAULT NULL,
ADD COLUMN `TaxPeriodYear` INT(11) NULL DEFAULT NULL,
ADD COLUMN `TaxedProfit` DOUBLE NULL DEFAULT NULL,
ADD COLUMN `PaidTaxes` DOUBLE NULL DEFAULT NULL;

ALTER TABLE `transaction`
ADD CONSTRAINT `fk_transaction_profit_category1`
  FOREIGN KEY (`ProfitCategoryId`)
  REFERENCES `profit_category` (`Id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

INSERT INTO `transaction_type` (`Id`, `Name`) VALUES ('3', 'TaxPayment');
