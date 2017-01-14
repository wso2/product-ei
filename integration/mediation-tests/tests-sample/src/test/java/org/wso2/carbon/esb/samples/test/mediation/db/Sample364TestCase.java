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

package org.wso2.carbon.esb.samples.test.mediation.db;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Sample364TestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager = null;
    private final String MYSQL_LIB = "mysql-connector-java-5.1.26.jar";
    private String datasourceOriginalPath = null;
    private String datasourceBkupPath = null;
    private Connection mysqlConnection = null;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {

        super.init();

        NodeList nodeList =  context.getConfigurationNodeList("//datasource");

        mysqlConnection = createMySqlConnection(nodeList);
        addDB();

        datasourceOriginalPath = FrameworkPathUtil.getCarbonHome() + File.separator +
                "repository" + File.separator + "conf"
                + File.separator + "datasources" + File.separator + "master-datasources.xml";

        datasourceBkupPath = FrameworkPathUtil.getCarbonHome() + File.separator +
                "repository" + File.separator + "conf"
                + File.separator + "datasources" + File.separator + "master-datasources.xml_bk";

        serverManager = new ServerConfigurationManager(context);

        //copping dependency jms jar files to component/lib
        serverManager.copyToComponentLib(new File(FrameworkPathUtil.getSystemResourceLocation()
                + File.separator + "artifacts" + File.separator + "ESB" + File.separator + "jar" +
                File.separator + MYSQL_LIB));

        FileUtils.moveFile(new File(datasourceOriginalPath), new File(datasourceBkupPath));

        FileUtils.copyFile(new File(FrameworkPathUtil.getSystemResourceLocation()
                + File.separator + "artifacts" + File.separator + "ESB" + File.separator + "other" +
                File.separator + "master-datasources.xml"), new File(datasourceOriginalPath));

        serverManager.restartGracefully();

        super.init();

        loadSampleESBConfiguration(364);
    }

    public void addDB() throws Exception {

        File mysqlfile = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB" + File.separator + "sql" + File.separator
                + "system.sql");

        File stock = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB" + File.separator + "sql" + File.separator
                + "mysqldata.sql");

        Statement statement = mysqlConnection.createStatement();

        statement.execute("DROP DATABASE IF EXISTS WSO2_CARBON_DB");
        statement.execute("CREATE DATABASE IF NOT EXISTS WSO2_CARBON_DB");
        statement.execute("USE WSO2_CARBON_DB");

        ScriptRunner scriptRunner;
        scriptRunner = new ScriptRunner(mysqlConnection);

        // Give the input file to Reader
        Reader readerSystemData = new BufferedReader(
                new FileReader(mysqlfile));

        // Exctute script
        scriptRunner.runScript(readerSystemData);

        Reader readerUserData = new BufferedReader(
                new FileReader(stock));

        scriptRunner.runScript(readerUserData);

    }
    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM })
    @Test(groups = {"wso2.esb"}, description = "testDBMediator ")
    public void testDBMediator() throws Exception {

        LogViewerClient logViewerClient =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        logViewerClient.clearLogs();

        AxisServiceClient client = new AxisServiceClient();

        client.sendRobust(Utils.getStockQuoteRequest("IBM")
                , getMainSequenceURL(), "getQuote");

        LogEvent[] getLogsInfo = logViewerClient.getAllSystemLogs();
        boolean assertValue = false;
        for (LogEvent event : getLogsInfo) {
            if (event.getMessage().contains("Stock Prize")) {
                assertValue = true;
                break;
            }
        }
        Assert.assertTrue(assertValue,
                "db lookup failed");

    }

    public Connection createMySqlConnection(NodeList nodeList) throws
            ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {

        String url = null;
        String driver = null;
        String userName = null;
        String password = null;

        for ( int i = 0; i < nodeList.getLength(); i++) {

            if (nodeList.item(i).getAttributes().item(0).getNodeValue().equalsIgnoreCase("mysql")) {

                NodeList mysqlNodeList = nodeList.item(i).getChildNodes();

                for ( int j = 0; j < mysqlNodeList.getLength(); j++) {

                    String nodeName = mysqlNodeList.item(j).getNodeName();
                    String nodeContent = mysqlNodeList.item(j).getTextContent();

                    if (nodeName.equalsIgnoreCase("url")) {
                        url = nodeContent;
                    }
                    else if (nodeName.equalsIgnoreCase("username")) {
                        userName = nodeContent;
                    }
                    else if (nodeName.equalsIgnoreCase("password")) {
                        password = nodeContent;
                    }
                    else if (nodeName.equalsIgnoreCase("driverClassName")) {
                        driver = nodeContent;
                    }
                    else {
                        log.debug("Unused config");
                    }
                }
            }
        }

        Class.forName(driver).newInstance();
        return DriverManager.getConnection(url, userName, password);

    }

    @AfterClass(alwaysRun = true,  enabled = false)
    public void deleteService() throws Exception {

        mysqlConnection.close();
        super.cleanup();
        serverManager.removeFromComponentLib(MYSQL_LIB);
        serverManager.restoreToLastConfiguration();serverManager.restoreToLastConfiguration();serverManager.restoreToLastConfiguration();
        FileUtils.moveFile(new File(datasourceBkupPath), new File(datasourceOriginalPath));
    }

}
