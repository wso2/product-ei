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
package org.wso2.carbon.esb.mediator.test.db.dblookup;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.test.utils.dbutils.MySqlDatabaseManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;

public class DBlookupMediatorTestCase extends ESBIntegrationTest {
    private MySqlDatabaseManager mySqlDatabaseManager;
    private ServerConfigurationManager serverConfigurationManager;
//    private final DataSource dbConfig = new EnvironmentBuilder().getFrameworkSettings()
//            .getDataSource();
//    private H2DataBaseManager h2DatabaseManager;
    private String JDBC_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private String DATASOURCE_NAME;


    private final double WSO2_PRICE = 200.0;
    private final String MYSQL_JAR = "mysql-connector-java-5.1.6.jar";
//    private final String H2_JAR ="h2-database-engine_1.2.140.wso2v3.jar";


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        AutomationContext automationContext = new AutomationContext();
        DATASOURCE_NAME = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_NAME);
        DB_PASSWORD = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        JDBC_URL = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        DB_USER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);
//        automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        serverConfigurationManager = new ServerConfigurationManager(context);
        copyJDBCDriverToClassPath();
        mySqlDatabaseManager = new MySqlDatabaseManager(JDBC_URL, DB_USER, DB_PASSWORD);
        mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS SampleDBForAutomation");

//        mySqlDatabaseManager = new MySqlDatabaseManager(JDBC_URL, DB_USER, DB_PASSWORD);
//        mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS SampleDBForAutomation");


        super.init();


    }

    /*
        entries under columns 'price' & 'name'. Insert a row with "WSO2" as a value in 'name' &
        respective price as "200"
    */

    @BeforeMethod(alwaysRun = true)
    public void createDatabase() throws SQLException {
        mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("Create DATABASE SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("USE SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("CREATE TABLE company(price double, name varchar(20))");

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test  with more than one result")
    public void dbLookupMediatorTestWithMultipleResults() throws Exception {

        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(0,'WSO2')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        mySqlDatabaseManager.executeUpdate("CREATE PROCEDURE `getId` (" +
                                           "IN nameVar VARCHAR(20))" +
                                           "BEGIN " +
                                           "select * from company where name =nameVar;" +
                                           "END");
        //first row of the result set should be taken into account
        URL url =
                getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/sample_360_multiple_results_test.xml");
        String s = FileUtils.readFileToString(new File(url.toURI()));
        s = updateDatabaseInfo(s);
        updateESBConfiguration(AXIOMUtil.stringToOM(s));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test with multiple SQL statements")
    public void dbLookupMediatorTestMultipleStatements() throws Exception {
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'IBM')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");

        URL url =
                getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/sample_360_multiple_SQL_statements.xml");
        String s = FileUtils.readFileToString(new File(url.toURI()));
        s = updateDatabaseInfo(s);
        updateESBConfiguration(AXIOMUtil.stringToOM(s));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Select rows from DB table while mediating messages.")
    public void dbLookupTestSelectRows() throws Exception {
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        URL url =
                getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/sample_360.xml");
        String s = FileUtils.readFileToString(new File(url.toURI()));
        s = updateDatabaseInfo(s);
        updateESBConfiguration(AXIOMUtil.stringToOM(s));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test  with stored finctions")
    public void dbLookupTestStoredFunctions() throws Exception {
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        mySqlDatabaseManager.executeUpdate("CREATE FUNCTION getID(s VARCHAR(20))" + " RETURNS VARCHAR(50)"
                                           + " RETURN CONCAT('Hello, ',s,'!');");

        URL url =
                getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/sample_360_stored_function_test.xml");
        String s = FileUtils.readFileToString(new File(url.toURI()));
        s = updateDatabaseInfo(s);
        updateESBConfiguration(AXIOMUtil.stringToOM(s));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test  with stored procedures")
    public void dbLookupTestStoredProcedures() throws Exception {
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        mySqlDatabaseManager.executeUpdate("CREATE PROCEDURE `getId` (" +
                                           "IN nameVar VARCHAR(20))" +
                                           "BEGIN " +
                                           "select * from company where name =nameVar;" +
                                           "END");
        URL url =
                getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/sample_360_stored_procedure.xml");
        String s = FileUtils.readFileToString(new File(url.toURI()));
        s = updateDatabaseInfo(s);
        updateESBConfiguration(AXIOMUtil.stringToOM(s));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {

        try {
            mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS SampleDBForAutomation");
        } finally {
            mySqlDatabaseManager.disconnect();

        }
        mySqlDatabaseManager = null;
        super.cleanup();
        super.init();
        loadSampleESBConfiguration(0);
        serverConfigurationManager.removeFromComponentLib(MYSQL_JAR);
        serverConfigurationManager.restartGracefully();
        serverConfigurationManager = null;
    }


    private void copyJDBCDriverToClassPath() throws Exception {
        File jarFile;
        jarFile = new File(getClass().getResource("/artifacts/ESB/jar/" + MYSQL_JAR + "").getPath());
        System.out.println(jarFile.getName());
        serverConfigurationManager.copyToComponentLib(jarFile);
        serverConfigurationManager.restartGracefully();
    }

    private String updateDatabaseInfo(String synapseConfig) {
        synapseConfig = synapseConfig.replace("$SampleDBForAutomation", JDBC_URL + "/SampleDBForAutomation");
        synapseConfig = synapseConfig.replace("####", DB_USER);
        synapseConfig = synapseConfig.replace("$$$$", DB_PASSWORD);
        return synapseConfig;
    }
}
