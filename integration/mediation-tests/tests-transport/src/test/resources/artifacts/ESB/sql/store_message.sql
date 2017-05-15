CREATE TABLE message_stored(
indexId BIGINT( 20 ) NOT NULL AUTO_INCREMENT ,
msg_id VARCHAR( 200 ) NOT NULL ,
message BLOB NOT NULL ,
PRIMARY KEY ( indexId )
);