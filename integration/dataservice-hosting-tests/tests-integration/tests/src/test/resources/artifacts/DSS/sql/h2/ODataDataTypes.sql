DROP TABLE IF EXISTS testTable;

CREATE TABLE IF NOT EXISTS testTable (
  id INT NOT NULL,
  testTinyInt TINYINT NULL,
  testSmallInt SMALLINT NULL,
  testMediumInt MEDIUMINT NULL,
  testFloat FLOAT NULL,
  testDouble DOUBLE NULL,
  testDecimal DECIMAL(10000,3) NULL,
  testChar CHAR NULL,
  testVarchar VARCHAR(45) NULL,
  testBoolean BIT NULL,
  testDate DATE NULL,
  testTimestamp TIMESTAMP NULL,
  testTime TIME NULL,
  testBlob BLOB NULL,
  PRIMARY KEY (id));

