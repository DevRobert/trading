ALTER TABLE `account` 
ADD CONSTRAINT `fk_account_client1`
  FOREIGN KEY (`ClientId`)
  REFERENCES `client` (`Id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `transaction` 
ADD CONSTRAINT `fk_transaction_account1`
  FOREIGN KEY (`AccountId`)
  REFERENCES `account` (`Id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
