DROP TABLE IF EXISTS Developer;

CREATE TABLE IF NOT EXISTS Developer(
	devId INTEGER,
	devName VARCHAR(50),
	devdob TIMESTAMP
);

CREATE UNIQUE INDEX  developer_pk ON Developer( devId );