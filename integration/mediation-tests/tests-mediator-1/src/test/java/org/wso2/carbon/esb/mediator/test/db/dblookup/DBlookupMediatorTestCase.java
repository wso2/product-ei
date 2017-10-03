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
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.test.utils.dbutils.H2DataBaseManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.util.Random;

public class DBlookupMediatorTestCase extends ESBIntegrationTest {
    private H2DataBaseManager h2DatabaseManager;
    private String JDBC_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private final double WSO2_PRICE = 200.0;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        AutomationContext automationContext = new AutomationContext();
        DB_PASSWORD = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        JDBC_URL = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        DB_USER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);
        String databaseName = System.getProperty("basedir") + File.separator + "target" + File.separator +
                "testdb_dblookp" + new Random().nextInt();
        JDBC_URL = JDBC_URL + databaseName + ";AUTO_SERVER=TRUE";
        h2DatabaseManager = new H2DataBaseManager(JDBC_URL, DB_USER, DB_PASSWORD);
        h2DatabaseManager.executeUpdate("CREATE TABLE IF NOT EXISTS company(price double, name varchar(20))");
        super.init();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test dblookup mediator with more than one resultsets")
    public void dbLookupMediatorTestWithMultipleResults() throws Exception {
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(0,'WSO2')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        h2DatabaseManager.executeUpdate("DROP ALIAS IF EXISTS getId;");
        // in H2 the stored procedures has to be defined as an alias
        String storedProcStr = "CREATE ALIAS getId AS $$ " +
                "ResultSet query(Connection conn, String nameVal) throws SQLException { " +
                "String sql = \"select * from company where name = '\" + nameVal + \"'\";" +
                "return conn.createStatement().executeQuery(sql); " +
                "} $$;";
        h2DatabaseManager.executeUpdate(storedProcStr);
        //first row of the result set should be taken into account
        URL fileLocation = getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/" +
                "dbLookupMediatorMultipleResultsTestProxy.xml");
        String proxyContent = FileUtils.readFileToString(new File(fileLocation.toURI()));
        proxyContent = updateDatabaseInfo(proxyContent);
        addProxyService(AXIOMUtil.stringToOM(proxyContent));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp
                ("dbLookupMediatorMultipleResultsTestProxy"), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test dblookup mediator with multiple SQL statements")
    public void dbLookupMediatorTestMultipleStatements() throws Exception {
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'IBM')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        URL fileLocation = getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/" +
                "dbLookupMediatorMultipleSQLStatementsTestProxy.xml");
        String proxyContent = FileUtils.readFileToString(new File(fileLocation.toURI()));
        proxyContent = updateDatabaseInfo(proxyContent);
        addProxyService(AXIOMUtil.stringToOM(proxyContent));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp
                ("dbLookupMediatorMultipleSQLStatementsTestProxy"), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Select rows from DB table while mediating messages.")
    public void dbLookupTestSelectRows() throws Exception {
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        URL fileLocation = getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/dbLookupTestProxy.xml");
        String proxyContent = FileUtils.readFileToString(new File(fileLocation.toURI()));
        proxyContent = updateDatabaseInfo(proxyContent);
        addProxyService(AXIOMUtil.stringToOM(proxyContent));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("dbLookupTestProxy"),
                null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test dblookup mediator with stored functions")
    public void dbLookupTestStoredFunctions() throws Exception {
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        // in H2 the functions has to be defined as an alias
        h2DatabaseManager.executeUpdate("DROP ALIAS IF EXISTS getId;");
        String functionStr = "CREATE ALIAS getId AS $$ " +
                "String query(Connection conn, String nameVal) throws SQLException { " +
                "return \"Hello, \" + nameVal + \"!\"; " +
                "} $$;";
        h2DatabaseManager.executeUpdate(functionStr);
        URL fileLocation = getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/dbLookupMediatorStoredFunctionTestProxy.xml");
        String proxyContent = FileUtils.readFileToString(new File(fileLocation.toURI()));
        proxyContent = updateDatabaseInfo(proxyContent);
        addProxyService(AXIOMUtil.stringToOM(proxyContent));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp
                ("dbLookupMediatorStoredFunctionTestProxy"), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test dblookup mediator with stored procedures")
    public void dbLookupTestStoredProcedures() throws Exception {
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(" + WSO2_PRICE + ",'WSO2')");
        h2DatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");
        // in H2 the stored procedures has to be defined as an alias
        h2DatabaseManager.executeUpdate("DROP ALIAS IF EXISTS getId;");
        String storedProcStr = "CREATE ALIAS getId AS $$ " +
                                "ResultSet query(Connection conn, String nameVal) throws SQLException { " +
                                    "String sql = \"select * from company where name = '\" + nameVal + \"'\";" +
                                    "return conn.createStatement().executeQuery(sql); " +
                                "} $$;";
        h2DatabaseManager.executeUpdate(storedProcStr);
        URL fileLocation = getClass().getResource("/artifacts/ESB/mediatorconfig/dblookup/" +
                "dbLookupMediatorStoredProcedureTestProxy.xml");
        String proxyContent = FileUtils.readFileToString(new File(fileLocation.toURI()));
        proxyContent = updateDatabaseInfo(proxyContent);
        addProxyService(AXIOMUtil.stringToOM(proxyContent));
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp
                ("dbLookupMediatorStoredProcedureTestProxy"), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        h2DatabaseManager.disconnect();
        h2DatabaseManager = null;
        super.cleanup();
    }

    private String updateDatabaseInfo(String synapseConfig) {
        synapseConfig = synapseConfig.replace("$url", JDBC_URL);
        synapseConfig = synapseConfig.replace("$username", DB_USER);
        synapseConfig = synapseConfig.replace("$password", DB_PASSWORD);
        return synapseConfig;
    }
}
