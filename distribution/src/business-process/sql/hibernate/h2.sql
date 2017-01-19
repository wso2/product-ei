create table ODE_SCHEMA_VERSION(VERSION integer);
insert into ODE_SCHEMA_VERSION values (6);
-- Apache ODE - SimpleScheduler Database Schema
--
-- MySQL scripts by Maciej Szefler.
--
--
DROP TABLE IF EXISTS ODE_JOB;

CREATE TABLE ODE_JOB (
  jobid CHAR(64)  NOT NULL DEFAULT '',
  ts BIGINT  NOT NULL DEFAULT 0,
  nodeid char(64)  NULL,
  scheduled int  NOT NULL DEFAULT 0,
  transacted int  NOT NULL DEFAULT 0,

  instanceId BIGINT,
  mexId varchar(255),
  processId varchar(255),
  type varchar(255),
  channel varchar(255),
  correlatorId varchar(255),
  correlationKeySet varchar(255),
  retryCount int,
  inMem int,
  detailsExt blob(4096),

  PRIMARY KEY(jobid)
);

create index IDX_ODE_JOB_TS on ODE_JOB(ts);
create index IDX_ODE_JOB_NODEID on ODE_JOB(nodeid);

create table BPEL_ACTIVITY_RECOVERY (ID bigint not null auto_increment, PIID bigint, AID bigint, CHANNEL varchar(255), REASON varchar(255), DATE_TIME timestamp, DETAILS blob(2G), ACTIONS varchar(255), RETRIES integer, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_CORRELATION_PROP (ID bigint not null auto_increment, NAME varchar(255), NAMESPACE varchar(255), VALUE varchar(255), CORR_SET_ID bigint, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_CORRELATION_SET (ID bigint not null auto_increment, VALUE varchar(255), CORR_SET_NAME varchar(255), SCOPE_ID bigint, PIID bigint, PROCESS_ID bigint, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_CORRELATOR (ID bigint not null auto_increment, CID varchar(255), PROCESS_ID bigint, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_CORRELATOR_MESSAGE_CKEY (ID bigint not null auto_increment, CKEY varchar(255), CORRELATOR_MESSAGE_ID bigint, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_EVENT (ID bigint not null auto_increment, IID bigint, PID bigint, TSTAMP timestamp, TYPE varchar(255), DETAIL clob, DATA blob(2G), SID bigint, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_FAULT (ID bigint not null auto_increment, FAULTNAME varchar(255), DATA blob(2G), EXPLANATION varchar(4000), LINE_NUM integer, AID integer, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_INSTANCE (ID bigint not null auto_increment, INSTANTIATING_CORRELATOR bigint, FAULT bigint, JACOB_STATE_DATA blob(2G), PREVIOUS_STATE smallint, PROCESS_ID bigint, STATE smallint, LAST_ACTIVE_DT timestamp, SEQUENCE bigint, FAILURE_COUNT integer, FAILURE_DT timestamp, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_MESSAGE (ID bigint not null auto_increment, MEX bigint, TYPE varchar(255), MESSAGE_DATA blob(2G), MESSAGE_HEADER blob(2G), INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_MESSAGE_EXCHANGE (ID bigint not null auto_increment, PORT_TYPE varchar(255), CHANNEL_NAME varchar(255), CLIENTKEY varchar(255), ENDPOINT blob(2G), CALLBACK_ENDPOINT blob(2G), REQUEST bigint, RESPONSE bigint, INSERT_DT timestamp, OPERATION varchar(255), STATE varchar(255), PROCESS bigint, PIID bigint, DIR char(255), PLINK_MODELID integer, PATTERN varchar(255), CORR_STATUS varchar(255), FAULT_TYPE varchar(255), FAULT_EXPL varchar(255), CALLEE varchar(255), PARTNERLINK bigint, PIPED_ID varchar(255), SUBSCRIBER_COUNT integer, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_MEX_PROPS (MEX bigint not null, VALUE varchar(8000), NAME varchar(255) not null, primary key (MEX, NAME));
create table BPEL_PLINK_VAL (ID bigint not null auto_increment, PARTNER_LINK varchar(100) not null, PARTNERROLE varchar(100), MYROLE_EPR_DATA blob(2G), PARTNERROLE_EPR_DATA blob(2G), PROCESS bigint, SCOPE bigint, SVCNAME varchar(255), MYROLE varchar(100), MODELID integer, MYSESSIONID varchar(255), PARTNERSESSIONID varchar(255), INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_PROCESS (ID bigint not null auto_increment, PROCID varchar(255) not null unique, deployer varchar(255), deploydate timestamp, type_name varchar(255), type_ns varchar(255), version bigint, ACTIVE_ bit, guid varchar(255), INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_SCOPE (ID bigint not null auto_increment, PIID bigint, PARENT_SCOPE_ID bigint, STATE varchar(255) not null, NAME varchar(255) not null, MODELID integer, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_SELECTORS (ID bigint not null auto_increment, PIID bigint not null auto_increment, SELGRPID varchar(255) not null, IDX integer not null, CORRELATION_KEY varchar(255) not null, PROC_TYPE varchar(255) not null, ROUTE_POLICY varchar(255), CORRELATOR bigint not null, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID), unique (CORRELATION_KEY, CORRELATOR));
create table BPEL_UNMATCHED (ID bigint not null auto_increment, MEX bigint, CORRELATION_KEY varchar(255), CORRELATOR bigint not null, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table BPEL_XML_DATA (ID bigint not null auto_increment, DATA blob(2G), NAME varchar(255) not null, SIMPLE_VALUE varchar(255), SCOPE_ID bigint, PIID bigint, IS_SIMPLE_TYPE bit, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create table STORE_DU (NAME varchar(255) not null, deployer varchar(255), DEPLOYDT timestamp, DIR varchar(255), primary key (NAME));
create table STORE_PROCESS (PID varchar(255) not null, DU varchar(255), TYPE varchar(255), version bigint, STATE varchar(255), primary key (PID));
create table STORE_PROCESS_PROP (propId varchar(255) not null, value varchar(255), name varchar(255) not null, primary key (propId, name));
create table STORE_VERSIONS (ID integer not null, VERSION bigint, primary key (ID));
create table VAR_PROPERTY (ID bigint not null auto_increment, XML_DATA_ID bigint, PROP_VALUE varchar(255), PROP_NAME varchar(255) not null, INSERT_TIME timestamp, MLOCK integer not null, primary key (ID));
create sequence hibernate_seqhilo;

create index IDX_CORRELATOR_CID on BPEL_CORRELATOR (CID);
create index IDX_BPEL_CORRELATOR_MESSAGE_CKEY on BPEL_CORRELATOR_MESSAGE_CKEY (CKEY);
create index IDX_SELECTOR_SELGRPID on BPEL_SELECTORS (SELGRPID);
create index IDX_SELECTOR_CKEY on BPEL_SELECTORS (CORRELATION_KEY);
create index IDX_SELECTOR_CORRELATOR on BPEL_SELECTORS (CORRELATOR);
create index IDX_UNMATCHED_CORRELATOR on BPEL_UNMATCHED (CORRELATOR);
create index IDX_UNMATCHED_CKEY on BPEL_UNMATCHED (CORRELATION_KEY);
create index IDX_XMLDATA_IID on BPEL_XML_DATA (PIID) ;
create index IDX_XMLDATA_SID on BPEL_XML_DATA (SCOPE_ID)  ;
create index IDX_XMLDATA_NAME on BPEL_XML_DATA (NAME)  ;
create index IDX_XMLDATA_NAME_SID on BPEL_XML_DATA (NAME, SCOPE_ID)  ;
create index IDX_EVENT_IID on BPEL_EVENT (IID)  ;
create index IDX_EVENT_PID on BPEL_EVENT (PID)  ;
create index IDX_CORR_SET_NAME on BPEL_CORRELATION_SET (CORR_SET_NAME)  ;
create index IDX_CORR_SET_SCOPE_ID on BPEL_CORRELATION_SET (SCOPE_ID)  ;
create index IDX_BPEL_INSTANCE_PROCESS_ID on BPEL_INSTANCE (PROCESS_ID)  ;
create index IDX_BPEL_INSTANCE_STATE on BPEL_INSTANCE (STATE)  ;
create index IDX_BPEL_PROCESS_TYPE_NAME on BPEL_PROCESS (type_name)  ;
create index IDX_BPEL_PROCESS_TYPE_NS on BPEL_PROCESS (type_ns)  ;
create index IDX_BPEL_CORRELATOR_PROCESS_ID on BPEL_CORRELATOR(PROCESS_ID)  ;
create index IDX_UNMATCHED_CORRELATOR_CKEY on BPEL_UNMATCHED (CORRELATOR,CORRELATION_KEY)  ;
create index IDX_PLINK_VAL_PROCESS_IDX on BPEL_PLINK_VAL (PROCESS)  ;
create index IDX_PLINK_VAL_SCOPE on BPEL_PLINK_VAL (SCOPE)  ;
create index IDX_PLINK_VAL_MODELID on BPEL_PLINK_VAL (MODELID)  ;
create index IDX_SELECTOR_INSTANCE on BPEL_SELECTORS (PIID)  ;
create index IDX_VARPROP_XMLDATA on VAR_PROPERTY (XML_DATA_ID)  ;
create index IDX_VARPROP_NAME on VAR_PROPERTY (PROP_NAME)  ;
create index IDX_VARPROP_VALUE on VAR_PROPERTY (PROP_VALUE)  ;
create index IDX_UNMATCHED_MEX on BPEL_UNMATCHED (MEX)  ;
create index IDX_MESSAGE_MEX on BPEL_MESSAGE(MEX) ;
create index IDX_MESSAGE_EXCHANGE_PIID on BPEL_MESSAGE_EXCHANGE(PIID) ;
create index IDX_SCOPE_PIID on BPEL_SCOPE(PIID) ;
create index IDX_BPEL_SELECTORS_PROC_TYPE on BPEL_SELECTORS(PROC_TYPE);
create index IDX_BPEL_SELECTORS_SELGRPID on BPEL_SELECTORS(SELGRPID);

