/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.message.store.jdbc.test;

import org.apache.axiom.om.OMElement;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.automation.test.utils.dbutils.MySqlDatabaseManager;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class JDBCMessageStoreProcRESTTestCase extends ESBIntegrationTest{
    private static final String url = "http://localhost:8280/jdbc/store";
    private final SimpleHttpClient httpClient = new SimpleHttpClient();

    private final Map<String, String> headers = new HashMap<String, String>(1);

    private final String payload =  "{\n" +
                                    "  \"email\" : \"jms@yomail.com\",\n" +
                                    "  \"firstName\" : \"Jms\",\n" +
                                    "  \"lastName\" : \"Broker\",\n" +
                                    "  \"id\" : 10\n" +
                                    "}";

    private LogViewerClient logViewer;

    private static final String logLine0 =
            "MESSAGE = ************RESTProxy IN, IN-Content-Type = application/json, IN-Test-Header-Field = TestHeaderValue";
    private static final String logLine1 =
            "MESSAGE = ************SamplingSeq IN, IN-Content-Type = application/json, IN-Test-Header-Field = TestHeaderValue";

    private final String MYSQL_JAR = "mysql-connector-java-5.1.6.jar";
    private ServerConfigurationManager serverConfigurationManager;
    private MySqlDatabaseManager mySqlDatabaseManager;

    private String JDBC_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private String DATASOURCE_NAME;
    private String JDBC_DRIVER;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        AutomationContext automationContext = new AutomationContext();
        DATASOURCE_NAME = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_NAME);
        DB_PASSWORD = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        JDBC_URL = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        DB_USER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);
        JDBC_DRIVER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        serverConfigurationManager = new ServerConfigurationManager(context);
        copyJDBCDriverToClassPath();
        mySqlDatabaseManager = new MySqlDatabaseManager(JDBC_URL, DB_USER, DB_PASSWORD);
        mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS WSO2SampleDBForAutomation");

        super.init();

        headers.put("Test-Header-Field", "TestHeaderValue");

        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
    }


    @BeforeMethod(alwaysRun = true)
    public void createDatabase() throws SQLException {
        mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS WSO2SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("Create DATABASE WSO2SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("USE WSO2SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("CREATE TABLE IF NOT EXISTS jdbc_store_table(\n" +
                                           "indexId BIGINT( 20 ) NOT NULL auto_increment ,\n" +
                                           "msg_id VARCHAR( 200 ) NOT NULL ,\n" +
                                           "message BLOB NOT NULL, \n" +
                                           "PRIMARY KEY ( indexId )\n" +
                                           ")");

    }

    @Test(groups = {"wso2.esb"}, description = "JDBC Message store support for RESTful services." )
    public void testJMSMessageStoreAndProcessor() throws Exception {
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jdbc/JDBCMessageStoreREST.xml");
        updateESBConfiguration(synapse);

        HttpResponse response = httpClient.doPost(url, headers, payload, "application/json");
        assertEquals(response.getStatusLine().getStatusCode(), 202);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int i = 1;
        for (LogEvent log : logs) {
            if (log.getMessage().contains(logLine0)) {
                ++i;
            }
            if (log.getMessage().contains(logLine1)) {
                ++i;
            }
        }

        if (i == 2) {
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false);
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            mySqlDatabaseManager.executeUpdate("DROP DATABASE WSO2SampleDBForAutomation");
        } finally {
            mySqlDatabaseManager.disconnect();
        }

        super.cleanup();
        super.init();
        loadSampleESBConfiguration(0);
        serverConfigurationManager.removeFromComponentLib(MYSQL_JAR);
        serverConfigurationManager.restartGracefully();
    }

    private void copyJDBCDriverToClassPath() throws Exception {
        File jarFile;
        jarFile = new File(getClass().getResource("/artifacts/ESB/jar/" + MYSQL_JAR + "").getPath());
        System.out.println(jarFile.getName());
        serverConfigurationManager.copyToComponentLib(jarFile);
        serverConfigurationManager.restartGracefully();
    }
}
