/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Initiate Database Connection to create EI_ANALYTICS tables.
 */
public class DatabaseConnection {

    /**
     * Variable which stores the database type.
     */
    private String dbType;
    /**
     * Variable which stores the database host.
     */
    private String host;
    /**
     * Variable which stores the database port.
     */
    private String port;
    /**
     * Variable which stores the database name.
     */
    private String dbName;
    /**
     * Variable which stores the database username.
     */
    private String user;
    /**
     * Variable which stores the database password.
     */
    private String pass;
    /**
     * Variable which stores the database driver location.
     */
    private String dbDriver;
    /**
     * Variable which stores the jdbcDriver.
     */
    private String jdbcDriver;
    /**
     * Variable which stores the database URL.
     */
    private String dbUrl;

    /**
     * Initialize Logger object to log messages
     */
    private static final Log LOG = LogFactory.getLog(DatabaseConnection.class);

    /**
     * Represent the set of databases
     */
    private enum DBTYPE {
        MYSQL, POSTGRESQL, ORACLE, MSSQL;
    }

    /**
     * Object of SQL connection.
     */
    Connection connection = null;
    /**
     * Object of SQL statement.
     */
    Statement statement = null;

    /**
     * Constructor which initiate the variables dbType,host,port,user,pass,dbDriver.
     *
     * @param dbType   type of database.
     * @param host     host of database.
     * @param port     port of database.
     * @param dbName   name of database.
     * @param user     username of database.
     * @param pass     password of database.
     * @param dbDriver driver location of database.
     */
    public DatabaseConnection(String dbType, String host, String port, String dbName, String user, String pass,
                              String dbDriver) {

        this.dbType = dbType;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.pass = pass;
        this.dbDriver = dbDriver;
    }

    /**
     * Getter of jdbcDriver
     *
     * @return the jdbcDriver
     */
    public String getJdbcDriver() {

        return jdbcDriver;
    }

    /**
     * Setter of jdbcDriver
     *
     * @param jdbcDriver the jdbcDriver to set
     */
    public void setJdbcDriver(String jdbcDriver) {

        this.jdbcDriver = jdbcDriver;
    }

    /**
     * Getter of dbUrl
     *
     * @return the dbUrl
     */
    public String getDbUrl() {

        return dbUrl;
    }

    /**
     * Setter of dbUrl
     *
     * @param dbUrl the dbUrl to set
     */
    public void setDbUrl(String dbUrl) {

        this.dbUrl = dbUrl;
    }

    /**
     * Create EI_ANALYTICS tables in MySQL database.
     */
    public void createMySQLTables() {

        String createComponentNameTable = "CREATE TABLE ComponentNameTable (componentId varchar(254) NOT NULL," +
                "componentName varchar(254) DEFAULT NULL,componentType varchar(254) DEFAULT NULL," +
                "PRIMARY KEY (componentId),KEY ComponentNameTable_INDEX (componentType));";
        String createESBEventTable = "CREATE TABLE ESBEventTable ( metaTenantId int(11) DEFAULT NULL," +
                " messageFlowId varchar(254) DEFAULT NULL, host varchar(254) DEFAULT NULL," +
                " hashCode varchar(254) DEFAULT NULL, componentName varchar(254) DEFAULT NULL," +
                " componentType varchar(254) DEFAULT NULL, componentIndex int(11) DEFAULT NULL," +
                " componentId varchar(254) DEFAULT NULL, startTime bigint(20) DEFAULT NULL," +
                " endTime bigint(20) DEFAULT NULL, duration bigint(20) DEFAULT NULL," +
                " beforePayload varchar(5000) DEFAULT NULL, afterPayload varchar(5000) DEFAULT NULL," +
                " contextPropertyMap varchar(5000) DEFAULT NULL, transportPropertyMap varchar(5000) DEFAULT NULL," +
                " children varchar(254) DEFAULT NULL, entryPoint varchar(254) DEFAULT NULL," +
                " entryPointHashcode varchar(254) DEFAULT NULL, faultCount int(11) DEFAULT NULL," +
                " eventTimestamp bigint(20) DEFAULT NULL," +
                " KEY ESBEventTable_INDEX (metaTenantId,messageFlowId,host,hashCode,componentName,componentType," +
                "componentIndex,componentId,startTime,endTime,entryPoint,entryPointHashcode,faultCount));";
        String createESBStatAgg_HOURS = "CREATE TABLE ESBStatAgg_HOURS ( AGG_TIMESTAMP bigint(20) NOT NULL," +
                "AGG_EVENT_TIMESTAMP bigint(20) NOT NULL,metaTenantId int(11) NOT NULL," +
                "componentId varchar(254) NOT NULL,  componentName varchar(254) NOT NULL," +
                "componentType varchar(254) NOT NULL,entryPoint varchar(254) NOT NULL," +
                "AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL,eventTimestamp bigint(20) DEFAULT NULL," +
                "AGG_SUM_duration bigint(20) DEFAULT NULL,AGG_COUNT bigint(20) DEFAULT NULL," +
                "AGG_MIN_duration bigint(20) DEFAULT NULL,AGG_MAX_duration bigint(20) DEFAULT NULL," +
                "AGG_SUM_faultCount bigint(20) DEFAULT NULL,PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint));";
        String createESBStatAgg_DAYS = "CREATE TABLE ESBStatAgg_DAYS ( AGG_TIMESTAMP bigint(20) NOT NULL," +
                "AGG_EVENT_TIMESTAMP bigint(20) NOT NULL,metaTenantId int(11) NOT NULL," +
                "componentId varchar(254) NOT NULL,  componentName varchar(254) NOT NULL," +
                "componentType varchar(254) NOT NULL,entryPoint varchar(254) NOT NULL," +
                "AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL,eventTimestamp bigint(20) DEFAULT NULL," +
                "AGG_SUM_duration bigint(20) DEFAULT NULL,AGG_COUNT bigint(20) DEFAULT NULL," +
                "AGG_MIN_duration bigint(20) DEFAULT NULL,AGG_MAX_duration bigint(20) DEFAULT NULL," +
                "AGG_SUM_faultCount bigint(20) DEFAULT NULL,PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint));";
        String createESBStatAgg_MONTHS = "CREATE TABLE ESBStatAgg_MONTHS ( AGG_TIMESTAMP bigint(20) NOT NULL," +
                "AGG_EVENT_TIMESTAMP bigint(20) NOT NULL,metaTenantId int(11) NOT NULL," +
                "componentId varchar(254) NOT NULL,  componentName varchar(254) NOT NULL," +
                "componentType varchar(254) NOT NULL,entryPoint varchar(254) NOT NULL," +
                "AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL,eventTimestamp bigint(20) DEFAULT NULL," +
                "AGG_SUM_duration bigint(20) DEFAULT NULL,AGG_COUNT bigint(20) DEFAULT NULL," +
                "AGG_MIN_duration bigint(20) DEFAULT NULL,AGG_MAX_duration bigint(20) DEFAULT NULL," +
                "AGG_SUM_faultCount bigint(20) DEFAULT NULL,PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint));";
        String createESBStatAgg_YEARS = "CREATE TABLE ESBStatAgg_YEARS ( AGG_TIMESTAMP bigint(20) NOT NULL," +
                "AGG_EVENT_TIMESTAMP bigint(20) NOT NULL,metaTenantId int(11) NOT NULL," +
                "componentId varchar(254) NOT NULL,  componentName varchar(254) NOT NULL," +
                "componentType varchar(254) NOT NULL,entryPoint varchar(254) NOT NULL," +
                "AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL,eventTimestamp bigint(20) DEFAULT NULL," +
                "AGG_SUM_duration bigint(20) DEFAULT NULL,AGG_COUNT bigint(20) DEFAULT NULL," +
                "AGG_MIN_duration bigint(20) DEFAULT NULL,AGG_MAX_duration bigint(20) DEFAULT NULL," +
                "AGG_SUM_faultCount bigint(20) DEFAULT NULL,PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint));";
        String createMediatorStatAgg_HOURS = "CREATE TABLE MediatorStatAgg_HOURS (AGG_TIMESTAMP bigint(20) NOT NULL," +
                "AGG_EVENT_TIMESTAMP bigint(20) NOT NULL,metaTenantId int(11) NOT NULL," +
                "componentId varchar(254) NOT NULL,componentName varchar(254) NOT NULL," +
                "componentType varchar(254) NOT NULL,entryPoint varchar(254) NOT NULL," +
                "entryPointHashcode varchar(254) NOT NULL,hashCode varchar(254) NOT NULL," +
                "AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL,startTime bigint(20) DEFAULT NULL," +
                "AGG_SUM_duration bigint(20) DEFAULT NULL,AGG_COUNT bigint(20) DEFAULT NULL," +
                "AGG_MIN_duration bigint(20) DEFAULT NULL,AGG_MAX_duration bigint(20) DEFAULT NULL," +
                "AGG_SUM_faultCount bigint(20) DEFAULT NULL,PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint,entryPointHashcode,hashCode));";
        String createMediatorStatAgg_DAYS = "CREATE TABLE MediatorStatAgg_DAYS (AGG_TIMESTAMP bigint(20) NOT NULL," +
                "AGG_EVENT_TIMESTAMP bigint(20) NOT NULL,metaTenantId int(11) NOT NULL," +
                "componentId varchar(254) NOT NULL,componentName varchar(254) NOT NULL," +
                "componentType varchar(254) NOT NULL,entryPoint varchar(254) NOT NULL," +
                "entryPointHashcode varchar(254) NOT NULL,hashCode varchar(254) NOT NULL," +
                "AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL,startTime bigint(20) DEFAULT NULL," +
                "AGG_SUM_duration bigint(20) DEFAULT NULL,AGG_COUNT bigint(20) DEFAULT NULL," +
                "AGG_MIN_duration bigint(20) DEFAULT NULL,AGG_MAX_duration bigint(20) DEFAULT NULL," +
                "AGG_SUM_faultCount bigint(20) DEFAULT NULL,PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint,entryPointHashcode,hashCode));";
        String createMediatorStatAgg_MONTHS = "CREATE TABLE MediatorStatAgg_MONTHS " +
                "(AGG_TIMESTAMP bigint(20) NOT NULL,AGG_EVENT_TIMESTAMP bigint(20) NOT NULL," +
                "metaTenantId int(11) NOT NULL,componentId varchar(254) NOT NULL," +
                "componentName varchar(254) NOT NULL,componentType varchar(254) NOT NULL," +
                "entryPoint varchar(254) NOT NULL,entryPointHashcode varchar(254) NOT NULL," +
                "hashCode varchar(254) NOT NULL,AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL," +
                "startTime bigint(20) DEFAULT NULL,AGG_SUM_duration bigint(20) DEFAULT NULL," +
                "AGG_COUNT bigint(20) DEFAULT NULL,AGG_MIN_duration bigint(20) DEFAULT NULL," +
                "AGG_MAX_duration bigint(20) DEFAULT NULL,AGG_SUM_faultCount bigint(20) DEFAULT NULL," +
                "PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode));";
        String createMediatorStatAgg_YEARS = "CREATE TABLE MediatorStatAgg_YEARS " +
                "(AGG_TIMESTAMP bigint(20) NOT NULL,AGG_EVENT_TIMESTAMP bigint(20) NOT NULL," +
                "metaTenantId int(11) NOT NULL,componentId varchar(254) NOT NULL," +
                "componentName varchar(254) NOT NULL,componentType varchar(254) NOT NULL," +
                "entryPoint varchar(254) NOT NULL,entryPointHashcode varchar(254) NOT NULL," +
                "hashCode varchar(254) NOT NULL,AGG_LAST_EVENT_TIMESTAMP bigint(20) DEFAULT NULL," +
                "startTime bigint(20) DEFAULT NULL,AGG_SUM_duration bigint(20) DEFAULT NULL," +
                "AGG_COUNT bigint(20) DEFAULT NULL,AGG_MIN_duration bigint(20) DEFAULT NULL," +
                "AGG_MAX_duration bigint(20) DEFAULT NULL,AGG_SUM_faultCount bigint(20) DEFAULT NULL," +
                "PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode));";
        List<String> al = new ArrayList<>();
        al.add(createComponentNameTable);
        al.add(createESBEventTable);
        al.add(createESBStatAgg_HOURS);
        al.add(createESBStatAgg_DAYS);
        al.add(createESBStatAgg_MONTHS);
        al.add(createESBStatAgg_YEARS);
        al.add(createMediatorStatAgg_HOURS);
        al.add(createMediatorStatAgg_DAYS);
        al.add(createMediatorStatAgg_MONTHS);
        al.add(createMediatorStatAgg_YEARS);
        try {
            for (String s : al) {
                statement.executeUpdate(s);
            }
            LOG.info("EI_ANALYTICS tables created in MySQL");
        } catch (SQLException e) {
            LOG.error(e);
            LOG.info("Drop all existing tables in the database & re-run the script");
        }
    }

    /**
     * Create EI_ANALYTICS tables in Postgresql database.
     */
    public void createPostgresqlTables() {

        String createComponentNameTable = "CREATE TABLE ComponentNameTable ( componentId varchar(254) NOT NULL," +
                " componentName varchar(254), componentType varchar(254), PRIMARY KEY (componentId) );";
        String createComponentNameTableINDEX = "CREATE INDEX ComponentNameTable_INDEX" +
                " ON ComponentNameTable (componentType);";
        String createESBEventTable = "CREATE TABLE ESBEventTable ( metaTenantId int, messageFlowId varchar(254)," +
                " host varchar(254), hashCode varchar(254), componentName varchar(254), componentType varchar(254)," +
                " componentIndex int, componentId varchar(254), startTime bigint, endTime bigint, duration bigint," +
                " beforePayload varchar(5000), afterPayload varchar(5000), contextPropertyMap varchar(5000)," +
                " transportPropertyMap varchar(5000), children varchar(254), entryPoint varchar(254)," +
                " entryPointHashcode varchar(254), faultCount int, eventTimestamp bigint );";
        String createESBEventTableINDEX = "CREATE INDEX ESBEventTable_INDEX ON ESBEventTable ( metaTenantId," +
                " messageFlowId, host, hashCode, componentName, componentType, componentIndex, componentId," +
                " startTime, endTime, entryPoint, entryPointHashcode, faultCount );";
        String createESBStatAgg_HOURS = "CREATE TABLE ESBStatAgg_HOURS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL," +
                " componentId varchar(254) NOT NULL, componentName varchar(254) NOT NULL," +
                " componentType varchar(254) NOT NULL, entryPoint varchar(254) NOT NULL," +
                " AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL, eventTimestamp bigint DEFAULT NULL," +
                " AGG_SUM_duration bigint DEFAULT NULL, AGG_COUNT bigint DEFAULT NULL," +
                " AGG_MIN_duration bigint DEFAULT NULL, AGG_MAX_duration bigint DEFAULT NULL," +
                " AGG_SUM_faultCount bigint DEFAULT NULL, PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint) );";
        String createESBStatAgg_DAYS = "CREATE TABLE ESBStatAgg_DAYS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL," +
                " componentId varchar(254) NOT NULL, componentName varchar(254) NOT NULL," +
                " componentType varchar(254) NOT NULL, entryPoint varchar(254) NOT NULL," +
                " AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL, eventTimestamp bigint DEFAULT NULL," +
                " AGG_SUM_duration bigint DEFAULT NULL, AGG_COUNT bigint DEFAULT NULL," +
                " AGG_MIN_duration bigint DEFAULT NULL, AGG_MAX_duration bigint DEFAULT NULL," +
                " AGG_SUM_faultCount bigint DEFAULT NULL, PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint) );";
        String createESBStatAgg_MONTHS = "CREATE TABLE ESBStatAgg_MONTHS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL," +
                " componentId varchar(254) NOT NULL, componentName varchar(254) NOT NULL," +
                " componentType varchar(254) NOT NULL, entryPoint varchar(254) NOT NULL," +
                " AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL, eventTimestamp bigint DEFAULT NULL," +
                " AGG_SUM_duration bigint DEFAULT NULL, AGG_COUNT bigint DEFAULT NULL," +
                " AGG_MIN_duration bigint DEFAULT NULL, AGG_MAX_duration bigint DEFAULT NULL," +
                " AGG_SUM_faultCount bigint DEFAULT NULL, PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint) );";
        String createESBStatAgg_YEARS = "CREATE TABLE ESBStatAgg_YEARS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL," +
                " componentId varchar(254) NOT NULL, componentName varchar(254) NOT NULL," +
                " componentType varchar(254) NOT NULL, entryPoint varchar(254) NOT NULL," +
                " AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL, eventTimestamp bigint DEFAULT NULL," +
                " AGG_SUM_duration bigint DEFAULT NULL, AGG_COUNT bigint DEFAULT NULL," +
                " AGG_MIN_duration bigint DEFAULT NULL, AGG_MAX_duration bigint DEFAULT NULL," +
                " AGG_SUM_faultCount bigint DEFAULT NULL, PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP," +
                "metaTenantId,componentId,componentType,componentName,entryPoint) );";
        String createMediatorStatAgg_HOURS = "CREATE TABLE MediatorStatAgg_HOURS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254), componentName varchar(254)," +
                " componentType varchar(254), entryPoint varchar(254), entryPointHashcode varchar(254)," +
                " hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint, startTime bigint," +
                " AGG_SUM_duration bigint, AGG_COUNT bigint, AGG_MIN_duration bigint, AGG_MAX_duration bigint," +
                " AGG_SUM_faultCount bigint, PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId," +
                "componentId,componentType,componentName,entryPoint,entryPointHashcode,hashCode) );";
        String createMediatorStatAgg_DAYS = "CREATE TABLE MediatorStatAgg_DAYS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254), componentName varchar(254)," +
                " componentType varchar(254), entryPoint varchar(254), entryPointHashcode varchar(254)," +
                " hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint, startTime bigint, AGG_SUM_duration bigint," +
                " AGG_COUNT bigint, AGG_MIN_duration bigint, AGG_MAX_duration bigint, AGG_SUM_faultCount bigint," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode) );";
        String createMediatorStatAgg_MONTHS = "CREATE TABLE MediatorStatAgg_MONTHS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254)," +
                " componentName varchar(254), componentType varchar(254), entryPoint varchar(254)," +
                " entryPointHashcode varchar(254), hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint," +
                " startTime bigint, AGG_SUM_duration bigint, AGG_COUNT bigint, AGG_MIN_duration bigint," +
                " AGG_MAX_duration bigint, AGG_SUM_faultCount bigint," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode) );";
        String createMediatorStatAgg_YEARS = "CREATE TABLE MediatorStatAgg_YEARS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254), componentName varchar(254)," +
                " componentType varchar(254), entryPoint varchar(254), entryPointHashcode varchar(254)," +
                " hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint, startTime bigint," +
                " AGG_SUM_duration bigint, AGG_COUNT bigint, AGG_MIN_duration bigint, AGG_MAX_duration bigint," +
                " AGG_SUM_faultCount bigint, PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId," +
                "componentId,componentType,componentName,entryPoint,entryPointHashcode,hashCode) );";
        List<String> al = new ArrayList<>();
        al.add(createComponentNameTable);
        al.add(createComponentNameTableINDEX);
        al.add(createESBEventTable);
        al.add(createESBEventTableINDEX);
        al.add(createESBStatAgg_HOURS);
        al.add(createESBStatAgg_DAYS);
        al.add(createESBStatAgg_MONTHS);
        al.add(createESBStatAgg_YEARS);
        al.add(createMediatorStatAgg_HOURS);
        al.add(createMediatorStatAgg_DAYS);
        al.add(createMediatorStatAgg_MONTHS);
        al.add(createMediatorStatAgg_YEARS);
        try {
            for (String s : al) {
                statement.executeUpdate(s);
            }
            LOG.info("EI_ANALYTICS tables created in Postgresql");
        } catch (SQLException e) {
            LOG.error(e);
            LOG.info("Drop all existing tables in the database & re-run the script");
        }
    }

    /**
     * Create EI_ANALYTICS tables in Oracle database.
     */
    public void createOracleTables() {

        String createComponentNameTable = "create table COMPONENTNAMETABLE ( COMPONENTID VARCHAR2(254) PRIMARY KEY," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254) )";
        String createComponentNameTableINDEX = "create index COMPONENTNAMETABLE_INDEX" +
                " on COMPONENTNAMETABLE (COMPONENTTYPE)";
        String createESBEventTable = "create table ESBEVENTTABLE ( METATENANTID NUMBER(10)," +
                " MESSAGEFLOWID VARCHAR2(254), HOST VARCHAR2(254), HASHCODE VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), COMPONENTINDEX NUMBER(10)," +
                " COMPONENTID VARCHAR2(254), STARTTIME NUMBER(19), ENDTIME NUMBER(19), DURATION NUMBER(19)," +
                " BEFOREPAYLOAD CLOB, AFTERPAYLOAD CLOB, CONTEXTPROPERTYMAP CLOB, TRANSPORTPROPERTYMAP CLOB," +
                " CHILDREN VARCHAR2(254), ENTRYPOINT VARCHAR2(254), ENTRYPOINTHASHCODE VARCHAR2(254)," +
                " FAULTCOUNT NUMBER(10), EVENTTIMESTAMP NUMBER(19))";
        String createESBEventTableINDEX = "create index ESBEVENTTABLE_INDEX on ESBEVENTTABLE (METATENANTID," +
                "MESSAGEFLOWID,HOST,HASHCODE,COMPONENTNAME,COMPONENTTYPE,COMPONENTINDEX,COMPONENTID,STARTTIME," +
                "ENDTIME,ENTRYPOINT,ENTRYPOINTHASHCODE,FAULTCOUNT)";
        String createESBStatAgg_HOURS = "create table ESBSTATAGG_HOURS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " AGG_LAST_EVENT_TIMESTAMP NUMBER(19), EVENTTIMESTAMP NUMBER(19), AGG_SUM_DURATION NUMBER(19)," +
                " AGG_COUNT NUMBER(19), AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP," +
                " METATENANTID, COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT))";
        String createESBStatAgg_DAYS = "create table ESBSTATAGG_DAYS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " AGG_LAST_EVENT_TIMESTAMP NUMBER(19), EVENTTIMESTAMP NUMBER(19), AGG_SUM_DURATION NUMBER(19)," +
                " AGG_COUNT NUMBER(19), AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, METATENANTID," +
                " COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT))";
        String createESBStatAgg_MONTHS = "create table ESBSTATAGG_MONTHS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " AGG_LAST_EVENT_TIMESTAMP NUMBER(19), EVENTTIMESTAMP NUMBER(19), AGG_SUM_DURATION NUMBER(19)," +
                " AGG_COUNT NUMBER(19), AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, METATENANTID," +
                " COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT))";
        String createESBStatAgg_YEARS = "create table ESBSTATAGG_YEARS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " AGG_LAST_EVENT_TIMESTAMP NUMBER(19), EVENTTIMESTAMP NUMBER(19), AGG_SUM_DURATION NUMBER(19)," +
                " AGG_COUNT NUMBER(19), AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, METATENANTID," +
                " COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT))";
        String createMediatorStatAgg_HOURS = "create table MEDIATORSTATAGG_HOURS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " ENTRYPOINTHASHCODE VARCHAR2(254), HASHCODE VARCHAR2(254), AGG_LAST_EVENT_TIMESTAMP NUMBER(19)," +
                " STARTTIME NUMBER(19), AGG_SUM_DURATION NUMBER(19), AGG_COUNT NUMBER(19)," +
                " AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, METATENANTID," +
                " COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT, ENTRYPOINTHASHCODE, HASHCODE))";
        String createMediatorStatAgg_DAYS = "create table MEDIATORSTATAGG_DAYS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " ENTRYPOINTHASHCODE VARCHAR2(254), HASHCODE VARCHAR2(254), AGG_LAST_EVENT_TIMESTAMP NUMBER(19)," +
                " STARTTIME NUMBER(19), AGG_SUM_DURATION NUMBER(19), AGG_COUNT NUMBER(19)," +
                " AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, METATENANTID," +
                " COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT, ENTRYPOINTHASHCODE, HASHCODE))";
        String createMediatorStatAgg_MONTHS = "create table MEDIATORSTATAGG_MONTHS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " ENTRYPOINTHASHCODE VARCHAR2(254), HASHCODE VARCHAR2(254), AGG_LAST_EVENT_TIMESTAMP NUMBER(19)," +
                " STARTTIME NUMBER(19), AGG_SUM_DURATION NUMBER(19), AGG_COUNT NUMBER(19)," +
                " AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, METATENANTID," +
                " COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT, ENTRYPOINTHASHCODE, HASHCODE))";
        String createMediatorStatAgg_YEARS = "create table MEDIATORSTATAGG_YEARS ( AGG_TIMESTAMP NUMBER(19)," +
                " AGG_EVENT_TIMESTAMP NUMBER(19), METATENANTID NUMBER(10), COMPONENTID VARCHAR2(254)," +
                " COMPONENTNAME VARCHAR2(254), COMPONENTTYPE VARCHAR2(254), ENTRYPOINT VARCHAR2(254)," +
                " ENTRYPOINTHASHCODE VARCHAR2(254), HASHCODE VARCHAR2(254), AGG_LAST_EVENT_TIMESTAMP NUMBER(19)," +
                " STARTTIME NUMBER(19), AGG_SUM_DURATION NUMBER(19), AGG_COUNT NUMBER(19)," +
                " AGG_MIN_DURATION NUMBER(19), AGG_MAX_DURATION NUMBER(19)," +
                " AGG_SUM_FAULTCOUNT NUMBER(19), PRIMARY KEY ( AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, METATENANTID," +
                " COMPONENTID, COMPONENTNAME, COMPONENTTYPE, ENTRYPOINT, ENTRYPOINTHASHCODE, HASHCODE))";
        List<String> al = new ArrayList<>();
        al.add(createComponentNameTable);
        al.add(createComponentNameTableINDEX);
        al.add(createESBEventTable);
        al.add(createESBEventTableINDEX);
        al.add(createESBStatAgg_HOURS);
        al.add(createESBStatAgg_DAYS);
        al.add(createESBStatAgg_MONTHS);
        al.add(createESBStatAgg_YEARS);
        al.add(createMediatorStatAgg_HOURS);
        al.add(createMediatorStatAgg_DAYS);
        al.add(createMediatorStatAgg_MONTHS);
        al.add(createMediatorStatAgg_YEARS);
        try {
            for (String s : al) {
                statement.executeUpdate(s);
            }
            LOG.info("EI_ANALYTICS tables created in Oracle");
        } catch (SQLException e) {
            LOG.error(e);
            LOG.info("Drop all existing tables in the database & re-run the script");
        }
    }

    /**
     * Create EI_ANALYTICS tables in Mssql database.
     */
    public void createMssqlTables() {

        String createComponentNameTable = "CREATE TABLE ComponentNameTable ( componentId varchar(254) NOT NULL," +
                " componentName varchar(254), componentType varchar(254), PRIMARY KEY (componentId) );";
        String createComponentNameTableINDEX = "CREATE INDEX ComponentNameTable_INDEX" +
                " ON ComponentNameTable (componentType);";
        String createESBEventTable = "CREATE TABLE ESBEventTable ( metaTenantId int, messageFlowId varchar(254)," +
                " host varchar(254), hashCode varchar(254), componentName varchar(254), componentType varchar(254)," +
                " componentIndex int, componentId varchar(254), startTime bigint, endTime bigint, duration bigint," +
                " beforePayload varchar(5000), afterPayload varchar(5000), contextPropertyMap varchar(5000)," +
                " transportPropertyMap varchar(5000), children varchar(254), entryPoint varchar(254)," +
                " entryPointHashcode varchar(254), faultCount int, eventTimestamp bigint );";
        String createESBEventTableINDEX = "CREATE INDEX ESBEventTable_INDEX ON ESBEventTable ( metaTenantId," +
                " messageFlowId, host, hashCode, componentName, componentType, componentIndex, componentId," +
                " startTime, endTime, entryPoint, entryPointHashcode, faultCount );";
        String createESBStatAgg_HOURS = "CREATE TABLE ESBStatAgg_HOURS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL, componentId varchar(254) NOT NULL," +
                " componentName varchar(254) NOT NULL, componentType varchar(254) NOT NULL," +
                " entryPoint varchar(254) NOT NULL, AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL," +
                " eventTimestamp bigint DEFAULT NULL, AGG_SUM_duration bigint DEFAULT NULL," +
                " AGG_COUNT bigint DEFAULT NULL, AGG_MIN_duration bigint DEFAULT NULL," +
                " AGG_MAX_duration bigint DEFAULT NULL, AGG_SUM_faultCount bigint DEFAULT NULL," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint) );";
        String createESBStatAgg_DAYS = "CREATE TABLE ESBStatAgg_DAYS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL, componentId varchar(254) NOT NULL," +
                " componentName varchar(254) NOT NULL, componentType varchar(254) NOT NULL," +
                " entryPoint varchar(254) NOT NULL, AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL," +
                " eventTimestamp bigint DEFAULT NULL, AGG_SUM_duration bigint DEFAULT NULL," +
                " AGG_COUNT bigint DEFAULT NULL, AGG_MIN_duration bigint DEFAULT NULL," +
                " AGG_MAX_duration bigint DEFAULT NULL, AGG_SUM_faultCount bigint DEFAULT NULL," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint) );";
        String createESBStatAgg_MONTHS = "CREATE TABLE ESBStatAgg_MONTHS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL, componentId varchar(254) NOT NULL," +
                " componentName varchar(254) NOT NULL, componentType varchar(254) NOT NULL," +
                " entryPoint varchar(254) NOT NULL, AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL," +
                " eventTimestamp bigint DEFAULT NULL, AGG_SUM_duration bigint DEFAULT NULL," +
                " AGG_COUNT bigint DEFAULT NULL, AGG_MIN_duration bigint DEFAULT NULL," +
                " AGG_MAX_duration bigint DEFAULT NULL, AGG_SUM_faultCount bigint DEFAULT NULL," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint) );";
        String createESBStatAgg_YEARS = "CREATE TABLE ESBStatAgg_YEARS ( AGG_TIMESTAMP bigint NOT NULL," +
                " AGG_EVENT_TIMESTAMP bigint NOT NULL, metaTenantId int NOT NULL, componentId varchar(254) NOT NULL," +
                " componentName varchar(254) NOT NULL, componentType varchar(254) NOT NULL," +
                " entryPoint varchar(254) NOT NULL, AGG_LAST_EVENT_TIMESTAMP bigint DEFAULT NULL," +
                " eventTimestamp bigint DEFAULT NULL, AGG_SUM_duration bigint DEFAULT NULL," +
                " AGG_COUNT bigint DEFAULT NULL, AGG_MIN_duration bigint DEFAULT NULL," +
                " AGG_MAX_duration bigint DEFAULT NULL, AGG_SUM_faultCount bigint DEFAULT NULL," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint) );";
        String createMediatorStatAgg_HOURS = "CREATE TABLE MediatorStatAgg_HOURS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254)," +
                " componentName varchar(254), componentType varchar(254), entryPoint varchar(254)," +
                " entryPointHashcode varchar(254), hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint," +
                " startTime bigint, AGG_SUM_duration bigint, AGG_COUNT bigint, AGG_MIN_duration bigint," +
                " AGG_MAX_duration bigint, AGG_SUM_faultCount bigint," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode) );";
        String createMediatorStatAgg_DAYS = "CREATE TABLE MediatorStatAgg_DAYS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254), componentName varchar(254)," +
                " componentType varchar(254), entryPoint varchar(254), entryPointHashcode varchar(254)," +
                " hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint, startTime bigint, AGG_SUM_duration bigint," +
                " AGG_COUNT bigint, AGG_MIN_duration bigint, AGG_MAX_duration bigint, AGG_SUM_faultCount bigint," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode) );";
        String createMediatorStatAgg_MONTHS = "CREATE TABLE MediatorStatAgg_MONTHS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254), componentName varchar(254)," +
                " componentType varchar(254), entryPoint varchar(254), entryPointHashcode varchar(254)," +
                " hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint, startTime bigint, AGG_SUM_duration bigint," +
                " AGG_COUNT bigint, AGG_MIN_duration bigint, AGG_MAX_duration bigint, AGG_SUM_faultCount bigint," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode) );";
        String createMediatorStatAgg_YEARS = "CREATE TABLE MediatorStatAgg_YEARS ( AGG_TIMESTAMP bigint," +
                " AGG_EVENT_TIMESTAMP bigint, metaTenantId int, componentId varchar(254), componentName varchar(254)," +
                " componentType varchar(254), entryPoint varchar(254), entryPointHashcode varchar(254)," +
                " hashCode varchar(254), AGG_LAST_EVENT_TIMESTAMP bigint, startTime bigint, AGG_SUM_duration bigint," +
                " AGG_COUNT bigint, AGG_MIN_duration bigint, AGG_MAX_duration bigint, AGG_SUM_faultCount bigint," +
                " PRIMARY KEY (AGG_TIMESTAMP,AGG_EVENT_TIMESTAMP,metaTenantId,componentId,componentType," +
                "componentName,entryPoint,entryPointHashcode,hashCode) );";
        List<String> al = new ArrayList<>();
        al.add(createComponentNameTable);
        al.add(createComponentNameTableINDEX);
        al.add(createESBEventTable);
        al.add(createESBEventTableINDEX);
        al.add(createESBStatAgg_HOURS);
        al.add(createESBStatAgg_DAYS);
        al.add(createESBStatAgg_MONTHS);
        al.add(createESBStatAgg_YEARS);
        al.add(createMediatorStatAgg_HOURS);
        al.add(createMediatorStatAgg_DAYS);
        al.add(createMediatorStatAgg_MONTHS);
        al.add(createMediatorStatAgg_YEARS);
        try {
            for (String s : al) {
                statement.executeUpdate(s);
            }
            LOG.info("EI_ANALYTICS tables created in Mssql");
        } catch (SQLException e) {
            LOG.error(e);
            LOG.info("Drop all existing tables in the database & re-run the script");
        }
    }

    /**
     * Load JDBC driver. Create database connection. Create EI_ANALYTICS tables.
     */
    public void connect() {

        LOG.info("Starting migration process...");
        switch (DBTYPE.valueOf(dbType)) {
            case MYSQL:
                setJdbcDriver("com.mysql.jdbc.Driver");
                setDbUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName + "?allowMultiQueries=true");
                LOG.info("Set JDBC driver & Database URL");
                break;
            case POSTGRESQL:
                setJdbcDriver("org.postgresql.Driver");
                setDbUrl("jdbc:postgresql://" + host + ":" + port + "/" + dbName + "?allowMultiQueries=true");
                LOG.info("Set JDBC driver & Database URL");
                break;
            case ORACLE:
                setJdbcDriver("oracle.jdbc.driver.OracleDriver");
                setDbUrl("jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName);
                LOG.info("Set JDBC driver & Database URL");
                break;
            case MSSQL:
                setJdbcDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                setDbUrl("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName);
                LOG.info("Set JDBC driver & Database URL");
                break;
            default:
                LOG.info("Invalid Database Type");
        }

        try {
            // An abstract representation of file and directory path names.
            // This creates a new File instance by converting the given pathname
            // string into an abstract pathname.
            File file = new File(dbDriver);
            URL url = null;
            // This method constructs a file : URI that represents this abstract pathname.
            url = file.toURI().toURL();
            URLClassLoader ucl = new URLClassLoader(new URL[]{url});
            Driver driver = null;
            LOG.info("Attempting to load driver...");
            driver = (Driver) Class.forName(jdbcDriver, true, ucl).newInstance();
            DriverManager.registerDriver(new DriverWrapper(driver));
            LOG.info("Driver Loaded");

            LOG.info("Attempting to establish connection to the selected database...");
            connection = DriverManager.getConnection(dbUrl, user, pass);
            LOG.info("Connection established");

            LOG.info("Attempting to create tables in the given database...");
            statement = connection.createStatement();
            switch (DBTYPE.valueOf(dbType)) {
                case MYSQL:
                    createMySQLTables();
                    break;
                case POSTGRESQL:
                    createPostgresqlTables();
                    break;
                case ORACLE:
                    createOracleTables();
                    break;
                case MSSQL:
                    createMssqlTables();
                    break;
                default:
                    LOG.info("Invalid Database Type");
            }

        } catch (InstantiationException e) {
            LOG.error(e);
        } catch (IllegalAccessException e) {
            LOG.error(e);
        } catch (MalformedURLException e) {
            LOG.error(String.format("Error occurred while opening url, %s", e));
        } catch (ClassNotFoundException e) {
            LOG.error(String.format("Error occurred while loading driver class, %s", e));
            System.exit(1);
        } catch (SQLException e) {
            LOG.error(String.format("Error occurred while making connection to the database, %s", e));
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOG.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOG.error(e);
                }
            }
        }
    }

    /**
     * Main class of the programme.
     *
     * @param args List of command line arguments.
     */
    public static void main(String[] args) {

        DatabaseConnection connection = new DatabaseConnection(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        connection.connect();
    }
}
