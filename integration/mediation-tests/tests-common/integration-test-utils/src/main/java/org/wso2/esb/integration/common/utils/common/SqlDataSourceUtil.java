/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.esb.integration.common.utils.common;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.automation.test.utils.dbutils.DatabaseFactory;
import org.wso2.carbon.automation.test.utils.dbutils.DatabaseManager;
import org.wso2.carbon.automation.test.utils.dbutils.H2DataBaseManager;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SqlDataSourceUtil {

    private static final Log log = LogFactory.getLog(SqlDataSourceUtil.class);

    private String jdbcUrl = null;
    private String jdbcDriver = null;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private AutomationContext automationContext;
    private String dssBackEndUrl;
    private String sessionCookie;
    //    private RSSManagerAdminServiceClient rssAdminClient;
    private String rssEnvironment;
    private String rssInstanceName;
    private final String userPrivilegeGroupName = "automation";

    public SqlDataSourceUtil(String sessionCookie, String backEndUrl) {
        this.sessionCookie = sessionCookie;
        this.dssBackEndUrl = backEndUrl;
    }

    protected void init() throws XPathExpressionException {
        automationContext = new AutomationContext();
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getDatabaseUser() {
        return this.databaseUser;
    }

    public String getDatabasePassword() {
        return this.databasePassword;
    }

    public int getDatabaseUserId() {
        return -1;
    }

    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    public String getJdbcUrlForProxy () throws XPathExpressionException {

        String url = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);

        String datasourceName = null;

        if (url.startsWith("jdbc:h2:")) {
            datasourceName = url.replace("jdbc:h2:", "");
        }

        if (url.endsWith(datasourceName)) {
            url = url.replace(datasourceName, "");
        }

        url += FrameworkPathUtil.getSystemSettingsLocation();

        if (url.endsWith("/src/test/resources/")) {
            url = url.replace("/src/test/resources/", "");
        }

        url += File.separator + "target" + File.separator + datasourceName + databaseName;

        return url;
    }

    public String getDriver() {
        return this.jdbcDriver;
    }

    public DataHandler createArtifact(String dbsFilePath)
            throws XMLStreamException, IOException, XPathExpressionException {

        if (automationContext == null) {
            init();
        }

        Assert.assertNotNull(jdbcUrl, "Initialize jdbcUrl");
        try {
            OMElement dbsFile = AXIOMUtil.stringToOM(FileManager.readFile(dbsFilePath));
            OMElement dbsConfig = dbsFile.getFirstChildWithName(new QName("config"));
            Iterator configElement1 = dbsConfig.getChildElements();
            while (configElement1.hasNext()) {
                OMElement property = (OMElement) configElement1.next();
                String value = property.getAttributeValue(new QName("name"));
                if ("org.wso2.ws.dataservice.protocol".equals(value)) {
                    property.setText(jdbcUrl);
                } else if ("org.wso2.ws.dataservice.driver".equals(value)) {
                    property.setText(jdbcDriver);
                } else if ("org.wso2.ws.dataservice.user".equals(value)) {
                    property.setText(databaseUser);
                } else if ("org.wso2.ws.dataservice.password".equals(value)) {
                    property.setText(databasePassword);
                }
            }
            log.debug(dbsFile);
            ByteArrayDataSource dbs = new ByteArrayDataSource(dbsFile.toString().getBytes());
            return new DataHandler(dbs);
        } catch (XMLStreamException e) {
            log.error("XMLStreamException when Reading Service File", e);
            throw new XMLStreamException("XMLStreamException when Reading Service File", e);
        } catch (IOException e) {
            log.error("IOException when Reading Service File", e);
            throw new IOException("IOException  when Reading Service File", e);
        }
    }


    private void createDataBase(String driver, String jdbc, String user, String password)
            throws ClassNotFoundException, SQLException, XPathExpressionException {

        if (automationContext == null) {
            init();
        }

        try {
            DatabaseManager dbm = DatabaseFactory.getDatabaseConnector(driver, jdbc, user, password);
            dbm.executeUpdate("DROP DATABASE IF EXISTS " + databaseName);
            dbm.executeUpdate("CREATE DATABASE " + databaseName);
            jdbcUrl = jdbc + "/" + databaseName;

            dbm.disconnect();
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
            throw new ClassNotFoundException("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
        } catch (SQLException e) {
            log.error("SQLException When executing SQL: ", e);
            throw new SQLException("SQLException When executing SQL: ", e);
        }
    }

    public void createDataSource(List<File> sqlFileList) throws Exception {

        if (automationContext == null) {
            init();
        }

//        databaseName = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_NAME);
        databaseName = "testdb";
        databasePassword = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        jdbcUrl = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        jdbcDriver = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        databaseUser = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);

        if (jdbcUrl.contains("h2") && jdbcDriver.contains("h2")) {
            //Random number appends to a database name to create new database for H2*//*
            databaseName = System.getProperty("basedir")+ File.separator + "target" + File.separator+ databaseName + new Random().nextInt();
            jdbcUrl = "jdbc:h2:" + databaseName;
            //create database on in-memory
            H2DataBaseManager h2 = null;
            try {
                h2 = new H2DataBaseManager(jdbcUrl, databaseUser, databasePassword);
                h2.executeUpdate("DROP ALL OBJECTS");
            } finally {
                if (h2 != null) {
                    h2.disconnect();
                }
            }
        } else {
            createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
        }
        executeUpdate(sqlFileList);
    }

    public void createDataSource(String dbName, List<File> sqlFileList) throws Exception {
        if (automationContext == null) {
            init();
        }

//        databaseName = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_NAME);
        databaseName = dbName + "_" + new Date().getTime();
        databasePassword = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        jdbcUrl = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        jdbcDriver = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        databaseUser = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);

        if (jdbcUrl.contains("h2") && jdbcDriver.contains("h2")) {
            //Random number appends to a database name to create new database for H2*//*
            databaseName = System.getProperty("basedir") + File.separator + "target" + File.separator+ databaseName ;
            jdbcUrl = "jdbc:h2:" + databaseName;
            //create database on in-memory
            H2DataBaseManager h2 = null;
            try {
                h2 = new H2DataBaseManager(jdbcUrl, databaseUser, databasePassword);
                h2.executeUpdate("DROP ALL OBJECTS");
            } finally {
                if (h2 != null) {
                    h2.disconnect();
                }
            }
        } else {
            createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
        }
        executeUpdate(sqlFileList);
    }



    private void executeUpdate(List<File> sqlFileList)
            throws IOException, ClassNotFoundException, SQLException, XPathExpressionException {

        DatabaseManager dbm = null;
        try {
            dbm = DatabaseFactory.getDatabaseConnector(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
            for (File sql : sqlFileList) {
                dbm.executeUpdate(sql);
            }
        } catch (IOException e) {
            log.error("IOException When reading SQL files: ", e);
            throw new IOException("IOException When reading SQL files: ", e);
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found. Check MySql-jdbc Driver in classpath: " + e);
            throw new ClassNotFoundException("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
        } catch (SQLException e) {
            log.error("SQLException When executing SQL: " + e);
            throw new SQLException("SQLException When executing SQL: ", e);
        } finally {
            if (dbm != null) {
                dbm.disconnect();
            }
        }
    }


    /*   public void createDataSource(List<File> sqlFileList) throws Exception {
databaseName = frameworkProperties.getDataSource().getDbName();
databaseUser = frameworkProperties.getDataSource().getDbUser();
databasePassword = frameworkProperties.getDataSource().getDbPassword();
jdbcUrl = frameworkProperties.getDataSource().getDbUrl();
jdbcDriver = frameworkProperties.getDataSource().get_dbDriverName();
databaseUser = frameworkProperties.getDataSource().getDbUser();
databasePassword = frameworkProperties.getDataSource().getDbPassword();
EnvironmentBuilder environmentBuilder = new EnvironmentBuilder();
String environment = environmentBuilder.getFrameworkSettings().getEnvironmentSettings()
    .executionEnvironment();
if (environment.equals(ExecutionEnvironment.stratos.name())) {
rssAdminClient = new RSSManagerAdminServiceClient(dssBackEndUrl, sessionCookie);
//rssAdminClient.
//            databaseUser = frameworkProperties.getDataSource().getRssDbUser();
//            databasePassword = frameworkProperties.getDataSource().getRssDbPassword();

RSSEnvironmentContext envCtx = setRssEnvironment();

Database rssInstance =
        rssAdminClient.getDatabaseInstance(envCtx, databaseName + "_" + userInfo.getDomain().replace(".", "_"));
if (rssInstance != null) {
    setPriConditions();
    createDataBase();
    createPrivilegeGroup();
    createUser();
} else {
    createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
}
} else {
jdbcUrl = frameworkProperties.getDataSource().getDbUrl();
jdbcDriver = frameworkProperties.getDataSource().get_dbDriverName();
if (jdbcUrl.contains("h2") && jdbcDriver.contains("h2")) {
    *//*Random number appends to a database name to create new database for H2*//*
                databaseName = databaseName + new Random().nextInt();
                jdbcUrl = jdbcUrl + databaseName;
                //create database on in-memory
                H2DataBaseManager h2 = null;
                try {
                    h2 = new H2DataBaseManager(jdbcUrl, databaseUser, databasePassword);
                    h2.executeUpdate("DROP ALL OBJECTS");
                } finally {
                    if (h2 != null) {
                        h2.disconnect();
                    }
                }
            } else {
                createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
            }
        }
        executeUpdate(sqlFileList);
    }

    private RSSEnvironmentContext setRssEnvironment() throws Exception {
        String[] environmentNames = rssAdminClient.getRSSEnvironmentNames();
        for (String environmentName : environmentNames) {
            if (environmentName.equals("DEFAULT")) {
                rssEnvironment = environmentName;
            } else {
                throw new Exception("Default rss environment not found");
            }
        }
        RSSEnvironmentContext envCtx = new RSSEnvironmentContext();
        envCtx.setEnvironmentName("DEFAULT");
        envCtx.setRssInstanceName(rssEnvironment);
        return envCtx;
    }

    public void createNonRandomDataSource(List<File> sqlFileList) throws Exception {
        databaseName = frameworkProperties.getDataSource().getDbName();
        databaseUser = frameworkProperties.getDataSource().getDbUser();
        databasePassword = frameworkProperties.getDataSource().getDbPassword();
        jdbcUrl = frameworkProperties.getDataSource().getDbUrl();
        jdbcDriver = frameworkProperties.getDataSource().get_dbDriverName();
        databaseUser = frameworkProperties.getDataSource().getDbUser();
        databasePassword = frameworkProperties.getDataSource().getDbPassword();
        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder();

        String environment = environmentBuilder.getFrameworkSettings().getEnvironmentSettings()
                .executionEnvironment();
        if (environment.equals(ExecutionEnvironment.stratos.name())) {
            rssAdminClient = new RSSManagerAdminServiceClient(dssBackEndUrl, sessionCookie);
            RSSEnvironmentContext envCtx = setRssEnvironment();
            Database rssInstance =
                    rssAdminClient.getDatabaseInstance(envCtx, databaseName + "_" + userInfo.getDomain().replace(".", "_"));
            if (rssInstance != null) {
                setPriConditions();
                createDataBase();
                createPrivilegeGroup();
                createUser();
            } else {
                createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
            }
        } else {
            jdbcUrl = frameworkProperties.getDataSource().getDbUrl();
            jdbcDriver = frameworkProperties.getDataSource().get_dbDriverName();
            if (jdbcUrl.contains("h2") && jdbcDriver.contains("h2")) {
                jdbcUrl = jdbcUrl + databaseName;
                //create database on in-memory
                H2DataBaseManager h2 = null;
                try {
                    h2 = new H2DataBaseManager(jdbcUrl, databaseUser, databasePassword);
                    h2.executeUpdate("DROP ALL OBJECTS");
                } finally {
                    if (h2 != null) {
                        h2.disconnect();
                    }
                }
            } else {
                createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
            }
        }
        executeUpdate(sqlFileList);
    }

    *//**
     * @param dbName
     * @param dbUser
     * @param dbPassword
     * @param sqlFileList
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     * @throws java.sql.SQLException
     *//*

    public void createDataSource(String dbName, String dbUser, String dbPassword,
                                 List<File> sqlFileList) throws Exception {
        databaseName = dbName;

        if (frameworkProperties.getEnvironmentSettings().is_runningOnStratos()) {
            rssAdminClient = new RSSManagerAdminServiceClient(dssBackEndUrl, sessionCookie);
            databaseUser = dbUser;
            databasePassword = dbPassword;
            setPriConditions();
            createDataBase();
            createPrivilegeGroup();
            createUser();
        } else {
            jdbcUrl = frameworkProperties.getDataSource().getDbUrl();
            jdbcDriver = frameworkProperties.getDataSource().get_dbDriverName();
            databaseUser = frameworkProperties.getDataSource().getDbUser();
            databasePassword = frameworkProperties.getDataSource().getDbPassword();

            if (jdbcUrl.contains("h2") && jdbcDriver.contains("h2")) {
                *//*Random number appends to a database name to create new database for H2*//*
                databaseName = databaseName + new Random().nextInt();
                jdbcUrl = jdbcUrl + databaseName;
                //create database on in-memory
                H2DataBaseManager h2 = null;
                try {
                    h2 = new H2DataBaseManager(jdbcUrl, databaseUser, databasePassword);
                    h2.executeUpdate("DROP ALL OBJECTS");
                } finally {
                    if (h2 != null) {
                        h2.disconnect();
                    }
                }
            } else {
                createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
            }
        }
        executeUpdate(sqlFileList);
    }

    private void createDataBase() throws Exception {
        RSSInstance rssInstance = null;

        rssInstanceName = "WSO2_RSS";
        log.info("RSS Instance Name :" + rssInstanceName);

        Database database = new Database();

        //creating database
        RSSEnvironmentContext envCtx = setRssEnvironment();
        rssAdminClient.createDatabase(envCtx, database);
        log.info("Database created");
        //set database full name
        databaseName = databaseName + "_" + userInfo.getDomain().replace(".", "_");
        log.info("Database name : " + databaseName);

        Database db = rssAdminClient.getDatabase(envCtx, databaseName);
        log.info("JDBC URL : " + db.getUrl());
    }

    private void createDataBase(String driver, String jdbc, String user, String password)
            throws ClassNotFoundException, SQLException {
        try {
            DatabaseManager dbm = DatabaseFactory.getDatabaseConnector(driver, jdbc, user, password);
            dbm.executeUpdate("DROP DATABASE IF EXISTS " + databaseName);
            dbm.executeUpdate("CREATE DATABASE " + databaseName);
            jdbcUrl = jdbc + "/" + databaseName;

            dbm.disconnect();
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
            throw new ClassNotFoundException("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
        } catch (SQLException e) {
            log.error("SQLException When executing SQL: ", e);
            throw new SQLException("SQLException When executing SQL: ", e);
        }
    }

    private void createPrivilegeGroup() throws Exception {
        RSSEnvironmentContext envCtx = setRssEnvironment();
        rssAdminClient.createPrivilegeGroup(envCtx, userPrivilegeGroupName);
        DatabasePrivilegeTemplate userPrivilegeGroup = rssAdminClient.getPrivilegeGroup(envCtx, userPrivilegeGroupName);
        log.info("privilege Group Created");
        log.debug("Privilege Group Name :" + userPrivilegeGroupName);
        Assert.assertNotSame(-1, userPrivilegeGroupName, "Privilege Group Not Found");
    }

    private void createUser() throws Exception {
        RSSEnvironmentContext envCtx = setRssEnvironment();
        DatabaseUser dbUser;
        rssAdminClient.createDatabaseUser(envCtx, databaseUser, databasePassword, rssInstanceName);
        log.info("Database User Created");

        dbUser = rssAdminClient.getDatabaseUser(envCtx, databaseUser);
        log.debug("Database Username :" + databaseUser);

        databaseUser = rssAdminClient.getFullyQualifiedUsername(databaseUser, userInfo.getDomain());
        log.info("Database User Name :" + databaseUser);
        Assert.assertEquals(dbUser.getName(), databaseUser, "Database UserName mismatched");
    }

    private void setPriConditions() throws Exception {
        Database dbInstance;
        DatabaseUser userEntry;
        DatabasePrivilegeTemplate privGroup;
        RSSEnvironmentContext envCtx = setRssEnvironment();
        log.info("Setting pre conditions");

        dbInstance =
                rssAdminClient.getDatabaseInstance(envCtx, databaseName + "_" +
                                                           userInfo.getDomain().replace(".", "_"));
        if (dbInstance != null) {
            log.info("Database name already in server");
            userEntry =
                    rssAdminClient.getDatabaseUser(envCtx,
                                                   rssAdminClient.getFullyQualifiedUsername(databaseUser, userInfo.getDomain()));
            if (userEntry != null) {

                log.info("User already in Database. deleting user");
                rssAdminClient.dropDatabaseUser(envCtx, userInfo.getUserName());
                log.info("User Deleted");
            }
            log.info("Dropping database");
            rssAdminClient.dropDatabase(envCtx, databaseName);
            log.info("database Dropped");
        }

        privGroup = rssAdminClient.getPrivilegeGroup(envCtx, userPrivilegeGroupName);
        if (privGroup != null) {
            log.info("Privilege Group name already in server");
            rssAdminClient.dropPrivilegeGroup(envCtx, privGroup.getName());
            log.info("Privilege Group Deleted");
        }
        log.info("pre conditions created");
    }
*/
}
