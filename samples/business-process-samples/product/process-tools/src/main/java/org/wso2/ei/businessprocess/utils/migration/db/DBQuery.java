/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.businessprocess.utils.migration.db;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class used to trigger the relevant SQL query according to the DB type
 */
public class DBQuery {
    String databaseURL;
    //DB queries for each operation

    //Insert queries
    private String INSERT_VERSION;
    private String INSERT_DEPLOYMENT_UNIT;

    //Update queries
    private String UPDATE_TASKS;
    private String UPDATE_VERSION;
    //Select queries
    private String DEPLOYMENT_UNIT_ID;
    private String VERSION;

    //Create Table queries
    private String HT_DEPLOYMENT_UNIT;
    private String HT_VERSIONS;

    //Alter queries
    private String ALTER_PACKAGE_NAME;
    private String ALTER_TASK_DEF_NAME;
    private String ALTER_TASK_VERSION;

    //Initialize queries for Operation with constructor
    public DBQuery(String databaseURL) {
        this.databaseURL = databaseURL;
        //Get the database type using the database url configured in the properties file
        INSERT_VERSION = "INSERT INTO HT_VERSIONS(id,TASK_VERSION) VALUES(1,0)";
        UPDATE_VERSION = " UPDATE HT_VERSIONS SET TASK_VERSION=TASK_VERSION+1";
        VERSION = "SELECT TASK_VERSION from HT_VERSIONS";
        DEPLOYMENT_UNIT_ID = "SELECT id FROM HT_DEPLOYMENT_UNIT";

        ALTER_PACKAGE_NAME = "ALTER TABLE HT_TASK ADD COLUMN PACKAGE_NAME VARCHAR(255) ";
        ALTER_TASK_DEF_NAME = "ALTER TABLE HT_TASK ADD COLUMN TASK_DEF_NAME VARCHAR(255) ";
        ALTER_TASK_VERSION = "ALTER TABLE HT_TASK ADD COLUMN TASK_VERSION BIGINT";

        if (databaseURL.contains("mysql") || databaseURL.contains("sqlserver")) {

            HT_DEPLOYMENT_UNIT = "CREATE TABLE HT_DEPLOYMENT_UNIT(id BIGINT NOT NULL, CHECKSUM VARCHAR(255)NOT NULL, " + "DEPLOYED_ON DATETIME, DEPLOY_DIR VARCHAR(255) NOT NULL, NAME VARCHAR(255) NOT NULL, " + "PACKAGE_NAME VARCHAR(255) NOT NULL, STATUS VARCHAR(255) NOT NULL, TENANT_ID BIGINT NOT NULL," + " VERSION BIGINT NOT NULL, PRIMARY KEY (id))";
            HT_VERSIONS = "CREATE TABLE HT_VERSIONS (id BIGINT NOT NULL, TASK_VERSION BIGINT NOT NULL, " +
                    "PRIMARY KEY (id))";

        } else if (databaseURL.contains("oracle")) {

            HT_DEPLOYMENT_UNIT = "CREATE TABLE HT_DEPLOYMENT_UNIT (id NUMBER NOT NULL, CHECKSUM VARCHAR2(255) NOT NULL, " + "DEPLOYED_ON TIMESTAMP, DEPLOY_DIR VARCHAR2(255) NOT NULL, NAME VARCHAR2(255) NOT NULL, " + "PACKAGE_NAME VARCHAR2(255) NOT NULL, STATUS VARCHAR2(255) NOT NULL, TENANT_ID NUMBER NOT NULL, VERSION NUMBER NOT NULL, PRIMARY KEY (id))";
            HT_VERSIONS = "CREATE TABLE HT_VERSIONS (id NUMBER NOT NULL, TASK_VERSION NUMBER NOT NULL, PRIMARY KEY (id)) ";
            ALTER_TASK_VERSION = "ALTER TABLE HT_TASK ADD TASK_VERSION NUMBER";
            ALTER_PACKAGE_NAME = "ALTER TABLE HT_TASK ADD PACKAGE_NAME VARCHAR(255) ";
            ALTER_TASK_DEF_NAME = "ALTER TABLE HT_TASK ADD TASK_DEF_NAME VARCHAR(255)";

        } else if (databaseURL.contains("h2")) {

            HT_DEPLOYMENT_UNIT = "CREATE TABLE HT_DEPLOYMENT_UNIT (id BIGINT NOT NULL, CHECKSUM VARCHAR(255) NOT NULL, DEPLOYED_ON TIMESTAMP, DEPLOY_DIR VARCHAR(255) NOT NULL, NAME VARCHAR(255) NOT NULL, PACKAGE_NAME VARCHAR(255) NOT NULL, STATUS VARCHAR(255) NOT NULL, TENANT_ID BIGINT NOT NULL, VERSION BIGINT NOT NULL, PRIMARY KEY (id))";
            HT_VERSIONS = "CREATE TABLE HT_VERSIONS (id BIGINT NOT NULL, TASK_VERSION BIGINT NOT NULL, PRIMARY KEY (id))";

        } else if (databaseURL.contains("postgresql")) {

            HT_DEPLOYMENT_UNIT = "CREATE TABLE HT_DEPLOYMENT_UNIT (id BIGINT NOT NULL, CHECKSUM VARCHAR(255) NOT NULL, DEPLOYED_ON TIMESTAMP, DEPLOY_DIR VARCHAR(255) NOT NULL, NAME VARCHAR(255) NOT NULL, PACKAGE_NAME VARCHAR(255) NOT NULL, STATUS VARCHAR(255) NOT NULL, TENANT_ID BIGINT NOT NULL, VERSION BIGINT NOT NULL, PRIMARY KEY (id))";
            HT_VERSIONS = "CREATE TABLE HT_VERSIONS (id BIGINT NOT NULL, TASK_VERSION BIGINT NOT NULL, PRIMARY KEY (id))";

        } else if (databaseURL.contains("derby")) {

            HT_DEPLOYMENT_UNIT = "CREATE TABLE HT_DEPLOYMENT_UNIT(id BIGINT NOT NULL, CHECKSUM VARCHAR(255) NOT NULL, DEPLOYED_ON TIMESTAMP, DEPLOY_DIR VARCHAR(255) NOT NULL, NAME VARCHAR(255) NOT NULL, PACKAGE_NAME VARCHAR(255) NOT NULL, STATUS VARCHAR(255) NOT NULL, TENANT_ID BIGINT NOT NULL, VERSION BIGINT NOT NULL, PRIMARY KEY (id))";
            HT_VERSIONS = "CREATE TABLE HT_VERSIONS(id BIGINT NOT NULL,TASK_VERSION BIGINT NOT NULL, PRIMARY KEY (id))";

        } else {
            System.out.println("Unsupported DB Type \n" +
                    "or Invalid Driver Name!");

        }

    }

    /**
     * @return Query for create HT_DEPLOYMENT_UNIT table
     */
    public String getVERSION() {
        return VERSION;
    }

    /**
     * @return Query for increment version
     */
    public String getUPDATE_VERSION() {
        return UPDATE_VERSION;
    }

    /**
     * @return Query for one version row. this will be increment during new package deployment.
     */
    public String getINSERT_VERSION() {
        return INSERT_VERSION;
    }

    /**
     * Update Existing Tasks with Version
     */
    public String getUPDATE_TASKS(String QName, int tenantID, String packageName, long version) {

        setUPDATE_TASKS("UPDATE HT_TASK SET TASK_NAME ='" + QName + "-" + version + "',TASK_VERSION= " + version + "," +
                "PACKAGE_NAME='" + packageName + "' , TASK_DEF_NAME='" + QName + "' " +
                "WHERE TASK_NAME='" + QName + "' AND " + "TENANT_ID=" + tenantID);
        return UPDATE_TASKS;
    }

    public String getDEPLOYMENT_UNIT_ID() {
        return DEPLOYMENT_UNIT_ID;
    }
    /**
     * Insert HT package data to Deployment Unit Table
     */
    public String getINSERT_DEPLOYMENT_UNIT(long id, String checkSum, Date deployOn, String deployPath,
                                            String name,
                                            String packageName, int tenantID, long version) {
        if (databaseURL.contains("oracle")) {
            setINSERT_DEPLOYMENT_UNIT("INSERT INTO HT_DEPLOYMENT_UNIT VALUES(" + id + ",'" + checkSum + "'," +
                    "sysdate,'" + deployPath + "'," +
                    "" + "'" + name + "'," +
                    "" + "'" + packageName + "','ACTIVE'," + tenantID + "," + version + ")");
        } else {
            setINSERT_DEPLOYMENT_UNIT("INSERT INTO HT_DEPLOYMENT_UNIT VALUES(" + id + ",'" + checkSum + "'," +
                    "'" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(deployOn) + "','" + deployPath + "'," +
                    "" + "'" + name + "'," +
                    "" + "'" + packageName + "','ACTIVE'," + tenantID + "," + version + ")");
        }
        return INSERT_DEPLOYMENT_UNIT;
    }

    public void setINSERT_DEPLOYMENT_UNIT(String INSERT_DEPLOYMENT_UNIT) {
        this.INSERT_DEPLOYMENT_UNIT = INSERT_DEPLOYMENT_UNIT;
    }

    public String getUPDATE_TASKS() {

        return UPDATE_TASKS;
    }

    public void setUPDATE_TASKS(String UPDATE_TASKS) {

        this.UPDATE_TASKS = UPDATE_TASKS;
    }


    /**
     * @return Query for Alter HT_TASk table with task version
     */
    public String getALTER_TASK_VERSION() {
        return ALTER_TASK_VERSION;
    }

    /**
     * @return Query for Alter HT_TASk table with task def name
     */
    public String getALTER_TASK_DEF_NAME() {
        return ALTER_TASK_DEF_NAME;
    }

    /**
     * @return Query for Alter HT_TASk table with package name
     */
    public String getALTER_PACKAGE_NAME() {
        return ALTER_PACKAGE_NAME;
    }

    /**
     * @return Query for Create Deployment Unit table
     */
    public String getHT_DEPLOYMENT_UNIT() {
        return HT_DEPLOYMENT_UNIT;
    }

    /**
     * @return Query for Create HT_VERSIONS table
     */
    public String getHT_VERSIONS() {
        return HT_VERSIONS;
    }
}
