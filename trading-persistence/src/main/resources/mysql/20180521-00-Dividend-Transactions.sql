ALTER TABLE `transaction`
CHANGE COLUMN `TotalPrice` `TotalPrice` DOUBLE NULL ,
CHANGE COLUMN `Commission` `Commission` DOUBLE NULL ,
CHANGE COLUMN `Quantity` `Quantity` INT(11) NULL ,
ADD COLUMN `Amount` DOUBLE NULL AFTER `Commission`;

INSERT INTO `transaction_type` (`Id`, `Name`) VALUES ('2', 'Dividend');

INSERT INTO `transaction` (`Id`, `AccountId`, `TransactionTypeId`, `Amount`, `Isin`, `Date`, `Created`) VALUES (3, 2, 2, 50, 'DE0008430026', '2018-04-18', '2018-04-18 09:00:00');
