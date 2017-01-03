insert into Files (fileName,type)  values ('WSO2DSS','WSO2 Data Services Server');
insert into Files (fileName,type)  values ('WSO2DAS','WSO2 Data Analytics Server');
insert into Files (fileName,type)  values ('WSO2ML','WSO2 Machine Learner');
insert into Files (fileName,type)  values ('WSO2CEP','WSO2 Complex Event Processor');

insert into FileRecords values (1,RAWTOHEX('BLOB1'),'WSO2DSS');
insert into FileRecords values (2,RAWTOHEX('BLOB1'),'WSO2DSS');
insert into FileRecords values (3,RAWTOHEX('BLOB1'),'WSO2DSS');
insert into FileRecords values (4,RAWTOHEX('BLOB1'),'WSO2DSS');

insert into FileRecords values (5,RAWTOHEX('BLOB2'),'WSO2DAS');
insert into FileRecords values (6,RAWTOHEX('BLOB2'),'WSO2DAS');
insert into FileRecords values (7,RAWTOHEX('BLOB2'),'WSO2DAS');
insert into FileRecords values (8,RAWTOHEX('BLOB2'),'WSO2DAS');
