CREATE TABLE IF NOT EXISTS Accounts(
        accountId INTEGER NOT NULL AUTO_INCREMENT,
        balance DOUBLE,
        PRIMARY KEY (accountId),
);
CREATE UNIQUE INDEX IF NOT EXISTS account_pk ON Accounts( accountId );

