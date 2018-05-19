ALTER TABLE `instrument`
ADD COLUMN `Code` VARCHAR(10) NULL AFTER `Name`,
ADD UNIQUE INDEX `Code_UNIQUE` (`Code` ASC);

UPDATE `instrument` SET `Code`='E-ADSD' WHERE `Isin`='DE000A1EWWW0';
UPDATE `instrument` SET `Code`='E-ALVD' WHERE `Isin`='DE0008404005';
UPDATE `instrument` SET `Code`='E-BASD' WHERE `Isin`='DE000BASF111';
UPDATE `instrument` SET `Code`='E-BAYND' WHERE `Isin`='DE000BAY0017';
UPDATE `instrument` SET `Code`='E-BEID' WHERE `Isin`='DE0005200000';
UPDATE `instrument` SET `Code`='E-BMWD' WHERE `Isin`='DE0005190003';
UPDATE `instrument` SET `Code`='E-CBKD' WHERE `Isin`='DE000CBK1001';
UPDATE `instrument` SET `Code`='E-COND' WHERE `Isin`='DE0005439004';
UPDATE `instrument` SET `Code`='E-DAID' WHERE `Isin`='DE0007100000';
UPDATE `instrument` SET `Code`='E-DBKD' WHERE `Isin`='DE0005140008';
UPDATE `instrument` SET `Code`='E-DB1D' WHERE `Isin`='DE0005810055';
UPDATE `instrument` SET `Code`='E-DPWD' WHERE `Isin`='DE0005552004';
UPDATE `instrument` SET `Code`='E-DTED' WHERE `Isin`='DE0005557508';
UPDATE `instrument` SET `Code`='E-EOAND' WHERE `Isin`='DE000ENAG999';
UPDATE `instrument` SET `Code`='E-FRED' WHERE `Isin`='DE0005785604';
UPDATE `instrument` SET `Code`='E-FMED' WHERE `Isin`='DE0005785802';
UPDATE `instrument` SET `Code`='E-HEID' WHERE `Isin`='DE0006047004';
UPDATE `instrument` SET `Code`='E-HEND' WHERE `Isin`='DE0006048432';
UPDATE `instrument` SET `Code`='E-IFXD' WHERE `Isin`='DE0006231004';
UPDATE `instrument` SET `Code`='E-LINUD' WHERE `Isin`='DE000A2E4L75';
UPDATE `instrument` SET `Code`='E-LHAD' WHERE `Isin`='DE0008232125';
UPDATE `instrument` SET `Code`='E-MRKD' WHERE `Isin`='DE0006599905';
UPDATE `instrument` SET `Code`='E-MUV2D' WHERE `Isin`='DE0008430026';
UPDATE `instrument` SET `Code`='E-PSMD' WHERE `Isin`='DE000PSM7770';
UPDATE `instrument` SET `Code`='E-RWED' WHERE `Isin`='DE0007037129';
UPDATE `instrument` SET `Code`='E-SAPD' WHERE `Isin`='DE0007164600';
UPDATE `instrument` SET `Code`='E-SIED' WHERE `Isin`='DE0007236101';
UPDATE `instrument` SET `Code`='E-TKAD' WHERE `Isin`='DE0007500001';
UPDATE `instrument` SET `Code`='E-VOW3D' WHERE `Isin`='DE0007664039';
UPDATE `instrument` SET `Code`='E-VNAD' WHERE `Isin`='DE000A1ML7J1';
UPDATE `instrument` SET `Code`='E-1COVD' WHERE `Isin`='DE0006062144';

ALTER TABLE `instrument` CHANGE COLUMN `Code` `Code` VARCHAR(10) NOT NULL ;

ALTER TABLE `instrument` ADD COLUMN `Category` VARCHAR(10) NULL AFTER `Code` ;

UPDATE `instrument` set `Category` = 'DAX' ;

ALTER TABLE `instrument` CHANGE COLUMN `Category` `Category` VARCHAR(10) NOT NULL ;

INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005408116', 'Aareal Bank', 'E-ARLD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('NL0000235190', 'Airbus', 'E-AIRD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A0LD2U1', 'alstria office REIT-AG', 'E-AOXD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('LU1673108939', 'Aroundtown SA', 'E-AT1D', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0006766504', 'Aurubis (ex Norddeutsche Affinerie)', 'E-NDAD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005501357', 'Axel Springer', 'E-SPRD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A1DAHH0', 'Brenntag', 'E-BNRD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0007257503', 'Ceconomy St.', 'E-CEC1D', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005470306', 'CTS Eventim', 'E-EVDD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0007480204', 'Deutsche Euroshop', 'E-DEQD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A0HN5C6', 'Deutsche Wohnen', 'E-DWNID', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005565204', 'Dürr', 'E-DUED', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000EVNK013', 'Evonik', 'E-EVKD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005772206', 'Fielmann', 'E-FIED', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005773303', 'Fraport', 'E-FRAD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005790430', 'FUCHS PETROLUB', 'E-FPE3D', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0006602006', 'GEA', 'E-G1AD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A0LD6E6', 'Gerresheimer', 'E-GXID', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('LU0775917882', 'Grand City Properties', 'E-GYCD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0008402215', 'Hannover Rück', 'E-HNR1D', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A13SX22', 'HELLA GmbH', 'E-HLED', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0006070006', 'HOCHTIEF', 'E-HOTD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A1PHFF7', 'HUGO BOSS', 'E-BOSSD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A2AADD2', 'innogy', 'E-IGYD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0006219934', 'Jungheinrich', 'E-JUN3D', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000KSAG888', 'K+S', 'E-SDFD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000KGX8881', 'KION GROUP', 'E-KGXD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0006335003', 'KRONES', 'E-KRND', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005470405', 'LANXESS', 'E-LXSD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000LEG1110', 'LEG Immobilien', 'E-LEGD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0005408884', 'LEONI', 'E-LEOD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000BFB0019', 'METRO (St.)', 'E-CECD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A0D9PT0', 'MTU Aero Engines', 'E-MTXD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A1H8BV3', 'NORMA Group', 'E-NOEJD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000LED4000', 'OSRAM', 'E-OSRD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0008019001', 'pbb', 'E-PBBD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0007030009', 'Rheinmetall', 'E-RHMD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000A12UKK6', 'Rocket Internet', 'E-RKETD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('LU0061462528', 'RTL', 'E-RRTLD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0006202005', 'Salzgitter', 'E-SZGD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000SHA0159', 'Schaeffler', 'E-SHAD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0007251803', 'STADA', 'E-SAZD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0007493991', 'Ströer', 'E-SAXD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000SYM9999', 'Symrise', 'E-SY1D', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE0008303504', 'TAG Immobilien', 'E-TEGD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000TLX1005', 'Talanx', 'E-TLXD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000UNSE018', 'Uniper', 'E-UN01D', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000WCH8881', 'WACKER CHEMIE', 'E-WCHD', 'MDAX');
INSERT INTO `instrument` (`Isin`, `Name`, `Code`, `Category`) VALUES ('DE000ZAL1111', 'Zalando', 'E-ZALD', 'MDAX');

UPDATE `instrument` SET `Category`='MDAX' WHERE `Isin`='DE000PSM7770';

ALTER TABLE `instrument` 
ADD COLUMN `Disabled` TINYINT(1) NOT NULL DEFAULT 0 AFTER `Category`;

UPDATE `instrument` SET `Disabled`='1' WHERE `Isin`='DE0007257503';
UPDATE `instrument` SET `Disabled`='1' WHERE `Isin`='LU1673108939';
