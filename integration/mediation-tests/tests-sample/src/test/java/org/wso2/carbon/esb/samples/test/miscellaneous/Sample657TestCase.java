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

package org.wso2.carbon.esb.samples.test.miscellaneous;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.NDataSourceAdminServiceClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo_WSDataSourceDefinition;
import org.wso2.carbon.ndatasource.ui.stub.core.xsd.JNDIConfig;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;

public class Sample657TestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager = null;
    private final String MYSQL_LIB = "mysql-connector-java-5.1.26.jar";
    private Connection mysqlConnection = null;
    private NDataSourceAdminServiceClient dataSourceAdminServiceClient = null;

    private String url = null;
    private String driver = null;
    private String userName = null;
    private String password = null;
    private String datasource1 = "MySqlDS1";
    private String datasource2 = "MySqlDS2";

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {

        super.init();
        serverManager = new ServerConfigurationManager(context);

        NodeList nodeList =  context.getConfigurationNodeList("//datasource");

        mysqlConnection = createMySqlConnection(nodeList);
        addPhysicalDBonMySql(datasource1);
        addPhysicalDBonMySql(datasource2);

        //copping dependency jms jar files to component/lib
        serverManager.copyToComponentLib(new File(FrameworkPathUtil.getSystemResourceLocation()
                + File.separator + "artifacts" + File.separator + "ESB" + File.separator + "jar" +
                File.separator + MYSQL_LIB));

        serverManager.restartGracefully();

        super.init();

        dataSourceAdminServiceClient =
                new NDataSourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        addDataSources(datasource1);
        addDataSources(datasource2);

        loadSampleESBConfiguration(657);
    }

    public void addPhysicalDBonMySql(String dataSource) throws Exception {

        File mysqlfile = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB" + File.separator + "sql" + File.separator
                + "system.sql");

        File stock = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB" + File.separator + "sql" + File.separator
                + "mysqldata.sql");

        Statement statement = mysqlConnection.createStatement();

        statement.execute("DROP DATABASE IF EXISTS " + dataSource);
        statement.execute("CREATE DATABASE IF NOT EXISTS " + dataSource);
        statement.execute("USE " + dataSource);

        ScriptRunner scriptRunner;
        scriptRunner = new ScriptRunner(mysqlConnection);

        // Give the input file to Reader
        Reader readerSystemData = new BufferedReader(
                new FileReader(mysqlfile));

        Reader readerUserData = new BufferedReader(
                new FileReader(stock));

        // Execute script
        scriptRunner.runScript(readerSystemData);
        scriptRunner.runScript(readerUserData);
    }

    public void addDataSources(String datasource) throws Exception {

        JNDIConfig jndiConfig = new JNDIConfig();
        jndiConfig.setName("jdbc/" + datasource);

        WSDataSourceMetaInfo wsDataSourceMetaInfo = new WSDataSourceMetaInfo();
        wsDataSourceMetaInfo.setJndiConfig(jndiConfig);

        WSDataSourceMetaInfo_WSDataSourceDefinition wsDataSourceDefinition =
                new WSDataSourceMetaInfo_WSDataSourceDefinition();
        wsDataSourceDefinition.setDsXMLConfiguration(
                "<configuration xmlns:svns=\"http://org.wso2.securevault/configuration\"" +
                        " xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">\n" +
                        "    <url>" + url + datasource + "</url>\n" +
                        "    <username>" + userName +"</username>\n" +
                        "    <password>" + password + "</password>\n" +
                        "    <driverClassName>" + driver + "</driverClassName>\n" +
                        "    <maxActive>50</maxActive>\n" +
                        "    <maxWait>60000</maxWait>\n" +
                        "    <testOnBorrow>true</testOnBorrow>\n" +
                        "    <validationQuery>SELECT 1</validationQuery>\n" +
                        "    <validationInterval>30000</validationInterval>\n" +
                        "</configuration>");
        wsDataSourceDefinition.setType("RDBMS");
        wsDataSourceMetaInfo.setDefinition(wsDataSourceDefinition);
        wsDataSourceMetaInfo.setName(datasource);
        dataSourceAdminServiceClient.addDataSource(wsDataSourceMetaInfo);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "testDtaSources " ,enabled=false)
    public void testDtaSources() throws Exception {

        AxisServiceClient client = new AxisServiceClient();

        client.sendRobust(Utils.getStockQuoteRequest("IBM")
                , getMainSequenceURL(), "getQuote");

        Thread.sleep(5000);

        Statement statement = mysqlConnection.createStatement();

        statement.execute("SELECT * FROM " + datasource2 + ".company");

        ResultSet resultSet = statement.getResultSet();

        boolean bWSO2Found = false;

        while (resultSet.next()) {
            if (resultSet.getString(1).contains("WSO2")) {
                bWSO2Found = true;
                break;
            }
        }

        Assert.assertTrue(bWSO2Found, "Insert record failed to database");
    }

    public Connection createMySqlConnection(NodeList nodeList) throws
            ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {

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

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {

        mysqlConnection.close();
        super.cleanup();
        Thread.sleep(5000);
        serverManager.removeFromComponentLib(MYSQL_LIB);
        serverManager.restoreToLastConfiguration();
    }
}
