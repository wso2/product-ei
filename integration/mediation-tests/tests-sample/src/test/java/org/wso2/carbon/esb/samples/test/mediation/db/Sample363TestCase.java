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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.common.SqlDataSourceUtil;

import java.io.*;
import java.util.ArrayList;

public class Sample363TestCase extends ESBIntegrationTest {

    private SqlDataSourceUtil sqlDataSourceUtilLookup = null;
    private SqlDataSourceUtil sqlDataSourceUtilReport = null;
    private File datasource_original = null;
    private File datasource_backup = null;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {

        datasource_original = new File(FrameworkPathUtil.getCarbonHome() + File.separator + "repository" +
                File.separator + "conf" + File.separator + "datasources.properties");

        datasource_backup = new File(FrameworkPathUtil.getCarbonHome() + File.separator + "repository" +
                File.separator + "conf" + File.separator + "datasources.properties_backup");

        FileUtils.moveFile(datasource_original, datasource_backup);

        super.init();
        sqlDataSourceUtilReport = new SqlDataSourceUtil(getSessionCookie(), contextUrls.getBackEndUrl());
        sqlDataSourceUtilLookup = new SqlDataSourceUtil(getSessionCookie(), contextUrls.getBackEndUrl());
        addDataSources();

    }

    public void addDataSources() throws Exception {
        ArrayList<File> sqlFileList = new ArrayList<File>();

        File h2file = new File(FrameworkPathUtil.getCarbonHome() + File.separator
                + "dbscripts" + File.separator + "h2.sql");

        File stock = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB" + File.separator + "sql" + File.separator
                + "stock.sql");

        sqlFileList.add(h2file);
        sqlFileList.add(stock);

        sqlDataSourceUtilReport.createDataSource("reportdb", sqlFileList);

        sqlDataSourceUtilLookup.createDataSource("lookupdb", sqlFileList);

        String dataSourceConfig = "#\n" +
                "################################################################################\n" +
                "## DataSources Configuration\n" +
                "################################################################################\n" +
                "synapse.datasources=lookupds,reportds\n" +
                "synapse.datasources.icFactory=com.sun.jndi.rmi.registry.RegistryContextFactory\n" +
                "synapse.datasources.providerPort=9999\n" +
                "## If following property is present , then assumes that there is an external JNDI provider and will not start a RMI registry\n" +
                "#synapse.datasources.providerUrl=rmi://localhost:9999\n" +
                "#\n" +
                "synapse.datasources.lookupds.registry=Memory\n" +
                "synapse.datasources.lookupds.type=BasicDataSource\n" +
                "synapse.datasources.lookupds.driverClassName=" + sqlDataSourceUtilLookup.getDriver() + "\n" +
                "synapse.datasources.lookupds.url=" + sqlDataSourceUtilLookup.getJdbcUrl() + "\n" +
                "## Optionally you can specifiy a specific password provider implementation which overrides any globally configured provider\n" +
                "#synapse.datasources.lookupds.secretProvider=org.apache.synapse.commons.security.secret.handler.SharedSecretCallbackHandler\n" +
                "synapse.datasources.lookupds.username=" + sqlDataSourceUtilLookup.getDatabaseUser() + "\n" +
                "## Depending on the password provider used, you may have to use an encrypted password here!\n" +
                "synapse.datasources.lookupds.password=" + sqlDataSourceUtilLookup.getDatabasePassword() + "\n" +
                "synapse.datasources.lookupds.dsName=" + sqlDataSourceUtilLookup.getDatabaseName() + "\n" +
                "synapse.datasources.lookupds.maxActive=100\n" +
                "synapse.datasources.lookupds.maxIdle=20\n" +
                "synapse.datasources.lookupds.maxWait=10000\n" +
                "#\n" +
                /*"synapse.datasources.reportds.registry=JNDI\n" +
                "synapse.datasources.reportds.type=PerUserPoolDataSource\n" +
                "synapse.datasources.reportds.cpdsadapter.factory=org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS\n" +
                "synapse.datasources.reportds.cpdsadapter.className=org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS\n" +
                "synapse.datasources.reportds.cpdsadapter.name=cpds\n" +
                "synapse.datasources.reportds.dsName=" + sqlDataSourceUtilReport.getDatabaseName() + "\n" +
                "synapse.datasources.reportds.driverClassName=" + sqlDataSourceUtilReport.getDriver() + "\n" +
                "synapse.datasources.reportds.url=" + sqlDataSourceUtilReport.getJdbcUrlForProxy() + "\n" +
                "## Optionally you can specifiy a specific password provider implementation which overrides any globally configured provider\n" +
                "#synapse.datasources.reportds.secretProvider=org.apache.synapse.commons.security.secret.handler.SharedSecretCallbackHandler\n" +
                "synapse.datasources.reportds.username=" + sqlDataSourceUtilReport.getDatabaseUser() + "\n" +
                "## Depending on the password provider used, you may have to use an encrypted password here!\n" +
                "synapse.datasources.reportds.password=" + sqlDataSourceUtilReport.getDatabasePassword() + "\n" +
                "synapse.datasources.reportds.maxActive=100\n" +
                "synapse.datasources.reportds.maxIdle=20\n" +
                "synapse.datasources.reportds.maxWait=10000";*/

                "synapse.datasources.reportds.registry=Memory\n" +
                "synapse.datasources.reportds.type=BasicDataSource\n" +
                "synapse.datasources.reportds.dsName=" + sqlDataSourceUtilReport.getDatabaseName() + "\n" +
                "synapse.datasources.reportds.driverClassName=" + sqlDataSourceUtilReport.getDriver() + "\n" +
                "synapse.datasources.reportds.url=" + sqlDataSourceUtilReport.getJdbcUrl() + "\n" +
                "## Optionally you can specifiy a specific password provider implementation which overrides any globally configured provider\n" +
                "#synapse.datasources.reportds.secretProvider=org.apache.synapse.commons.security.secret.handler.SharedSecretCallbackHandler\n" +
                "synapse.datasources.reportds.username=" + sqlDataSourceUtilReport.getDatabaseUser() + "\n" +
                "## Depending on the password provider used, you may have to use an encrypted password here!\n" +
                "synapse.datasources.reportds.password=" + sqlDataSourceUtilReport.getDatabasePassword() + "\n" +
                "synapse.datasources.reportds.maxActive=100\n" +
                "synapse.datasources.reportds.maxIdle=20\n" +
                "synapse.datasources.reportds.maxWait=10000";


        File updatedFile = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator + "artifacts" +
                File.separator + "ESB" + File.separator + "datasources.properties");
        FileOutputStream is = new FileOutputStream(updatedFile);
        OutputStreamWriter osw = new OutputStreamWriter(is);
        Writer w = new BufferedWriter(osw);
        w.write(dataSourceConfig);
        w.close();

        FileUtils.copyFile(updatedFile, new File(FrameworkPathUtil.getCarbonHome() + File.separator + "repository" +
                File.separator + "conf" + File.separator + "datasources.properties"));

        ServerConfigurationManager serverManager = new ServerConfigurationManager(context);
        serverManager.restartGracefully();

        super.init();

        OMElement synapseConfig = AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://ws.apache.org/ns/synapse\">\n" +
                "\n" +
                "    <sequence name=\"myFaultHandler\">\n" +
                "        <makefault response=\"true\">\n" +
                "            <code xmlns:tns=\"http://www.w3.org/2003/05/soap-envelope\" value=\"tns:Receiver\"/>\n" +
                "            <reason expression=\"get-property('ERROR_MESSAGE')\"/>\n" +
                "        </makefault>\n" +
                "        <send/>\n" +
                "        <drop/>\n" +
                "    </sequence>\n" +
                "\n" +
                "    <sequence name=\"main\" onError=\"myFaultHandler\">\n" +
                "        <in>\n" +
                "            <log level=\"custom\">\n" +
                "                <property name=\"text\" value=\"** Looking up from the Database **\"/>\n" +
                "            </log>\n" +
                "            <dblookup>\n" +
                "                <connection>\n" +
                "                    <pool>\n" +
                "                        <dsName>" + sqlDataSourceUtilLookup.getDatabaseName() +"</dsName>\n" +
                "                        <icClass>com.sun.jndi.rmi.registry.RegistryContextFactory</icClass>\n" +
                "                        <url>rmi://localhost:9999</url>\n" +
                "                        <user>"+ sqlDataSourceUtilLookup.getDatabaseUser() +"</user>\n" +
                "                        <password>"+ sqlDataSourceUtilLookup.getDatabasePassword() +"</password>\n" +
                "                    </pool>\n" +
                "                </connection>\n" +
                "                <statement>\n" +
                "                    <sql>select * from company where name =?</sql>\n" +
                "                    <parameter xmlns:m0=\"http://services.samples\" expression=\"//m0:getQuote/m0:request/m0:symbol\"\n" +
                "                               type=\"VARCHAR\"/>\n" +
                "                    <result name=\"company_id\" column=\"id\"/>\n" +
                "                </statement>\n" +
                "            </dblookup>\n" +
                "\n" +
                "            <switch source=\"get-property('company_id')\">\n" +
                "                <case regex=\"c1\">\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" expression=\"fn:concat('Company ID - ',get-property('company_id'))\"/>\n" +
                "                    </log>\n" +
                "                    <send>\n" +
                "                        <endpoint>\n" +
                "                            <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                "                        </endpoint>\n" +
                "                    </send>\n" +
                "                </case>\n" +
                "                <case regex=\"c2\">\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" expression=\"fn:concat('Company ID - ',get-property('company_id'))\"/>\n" +
                "                    </log>\n" +
                "                    <send>\n" +
                "                        <endpoint>\n" +
                "                            <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                "                        </endpoint>\n" +
                "                    </send>\n" +
                "                </case>\n" +
                "                <case regex=\"c3\">\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" expression=\"fn:concat('Company ID - ',get-property('company_id'))\"/>\n" +
                "                    </log>\n" +
                "                    <send>\n" +
                "                        <endpoint>\n" +
                "                            <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                "                        </endpoint>\n" +
                "                    </send>\n" +
                "                </case>\n" +
                "                <default>\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" value=\"** Unrecognized Company ID **\"/>\n" +
                "                    </log>\n" +
                "                    <makefault response=\"true\">\n" +
                "                        <code xmlns:tns=\"http://www.w3.org/2003/05/soap-envelope\" value=\"tns:Receiver\"/>\n" +
                "                        <reason value=\"** Unrecognized Company ID **\"/>\n" +
                "                    </makefault>\n" +
                "                    <send/>\n" +
                "                    <drop/>\n" +
                "                </default>\n" +
                "            </switch>\n" +
                "            <drop/>\n" +
                "        </in>\n" +
                "\n" +
                "        <out>\n" +
                "            <log level=\"custom\">\n" +
                "                <property name=\"text\" value=\"** Reporting to the Database **\"/>\n" +
                "            </log>\n" +
                "            <dbreport>\n" +
                "                <connection>\n" +
                "                    <pool>\n" +
                "                        <dsName>" + sqlDataSourceUtilLookup.getDatabaseName() +"</dsName>\n" +
                "                        <icClass>com.sun.jndi.rmi.registry.RegistryContextFactory</icClass>\n" +
                "                        <url>rmi://localhost:9999</url>\n" +
                "                        <user>" + sqlDataSourceUtilLookup.getDatabaseUser() + "</user>\n" +
                "                        <password>" + sqlDataSourceUtilLookup.getDatabasePassword() + "</password>\n" +
                "                    </pool>\n" +
                "                </connection>\n" +
                "                <statement>\n" +
                "                    <sql>update company set price=? where name =?</sql>\n" +
                "                    <parameter xmlns:m0=\"http://services.samples\" xmlns:m1=\"http://services.samples/xsd\"\n" +
                "                               expression=\"//m0:return/m1:last/child::text()\" type=\"DOUBLE\"/>\n" +
                "                    <parameter xmlns:m0=\"http://services.samples\" xmlns:m1=\"http://services.samples/xsd\"\n" +
                "                               expression=\"//m0:return/m1:symbol/child::text()\" type=\"VARCHAR\"/>\n" +
                "                </statement>\n" +
                "            </dbreport>\n" +
                "            <log level=\"custom\">\n" +
                "                <property name=\"text\" value=\"** Looking up from the Database **\"/>\n" +
                "            </log>\n" +
                "            <dblookup>\n" +
                "                <connection>\n" +
                "                    <pool>\n" +
                "                        <dsName>" + sqlDataSourceUtilReport.getDatabaseName() + "</dsName>\n" +
                "                        <icClass>com.sun.jndi.rmi.registry.RegistryContextFactory</icClass>\n" +
                "                        <url>rmi://localhost:9999</url>\n" +
                "                        <user>" + sqlDataSourceUtilReport.getDatabaseUser() + "</user>\n" +
                "                        <password>" + sqlDataSourceUtilReport.getDatabasePassword() + "</password>\n" +
                "                    </pool>\n" +
                "                </connection>\n" +
                "                <statement>\n" +
                "                    <sql>select * from company where name =?</sql>\n" +
                "                    <parameter xmlns:m0=\"http://services.samples\" xmlns:m1=\"http://services.samples/xsd\"\n" +
                "                               expression=\"//m0:return/m1:symbol/child::text()\" type=\"VARCHAR\"/>\n" +
                "                    <result name=\"stock_price\" column=\"price\"/>\n" +
                "                </statement>\n" +
                "            </dblookup>\n" +
                "            <log level=\"custom\">\n" +
                "                <property name=\"text\" expression=\"fn:concat('Stock price - ',get-property('stock_price'))\"/>\n" +
                "            </log>\n" +
                "            <send/>\n" +
                "\n" +
                "        </out>\n" +
                "\n" +
                "    </sequence>\n" +
                "\n" +
                "</definitions>\n");

        updateESBConfiguration(synapseConfig);
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = {"wso2.esb"}, description = "testDBMediator ")
    public void testDBMediator() throws Exception {

        LogViewerClient logViewerClient =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        logViewerClient.clearLogs();

        AxisServiceClient client = new AxisServiceClient();

        client.sendReceive(Utils.getStockQuoteRequest("IBM")
                , getMainSequenceURL(), "getQuote");

        LogEvent[] getLogsInfo = logViewerClient.getAllSystemLogs();
        boolean assertValue = false;
        for (LogEvent event : getLogsInfo) {
            if (event.getMessage().contains("Stock price")) {
                assertValue = true;
                break;
            }
        }
        Assert.assertTrue(assertValue,
                "db lookup failed");

    }

    @AfterClass(alwaysRun = true,  enabled = false)
    public void deleteService() throws Exception {

        super.cleanup();

        FileUtils.deleteQuietly(new File(FrameworkPathUtil.getCarbonHome() + File.separator + "repository" +
                File.separator + "conf" + File.separator + "datasources.properties"));

        FileUtils.moveFile(datasource_backup, new File(FrameworkPathUtil.getCarbonHome() + File.separator + "repository" +
                File.separator + "conf" + File.separator + "datasources.properties"));
    }
}
