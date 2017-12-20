/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.message.processor.test.forwarding;

import org.h2.tools.DeleteDbFiles;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.dbutils.H2DataBaseManager;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

/**
 * Testing whether the message processor is consuming the message from the JDBC store
 * when the message has no body.
 * Issue : https://github.com/wso2/product-ei/issues/1449
 */
public class StoreAndForwardWithEmptyMessageBodyTesCase extends ESBIntegrationTest {

    private H2DataBaseManager h2DatabaseManager;
    private static LogViewerClient logViewer;

    private String JDBC_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private String DB_PATH;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        DB_PATH = getESBResourceLocation();
        DB_PASSWORD = "wso2carbon";
        DB_USER = "wso2carbon";
        JDBC_URL = "jdbc:h2:file:/" + DB_PATH + "/testDB;MV_STORE=FALSE;MVCC=FALSE;FILE_LOCK=NO;TRACE_LEVEL_FILE=0;DB_CLOSE_DELAY=-1";

        h2DatabaseManager = new H2DataBaseManager(JDBC_URL, DB_USER, DB_PASSWORD);
        h2DatabaseManager.executeUpdate(
                "CREATE TABLE IF NOT EXISTS JDBC_MESSAGE_STORE(\n" + "indexId BIGINT( 20 ) NOT NULL auto_increment ,\n"
                        + "msg_id VARCHAR( 200 ) NOT NULL ,\n" + "message BLOB NOT NULL, \n"
                        + "PRIMARY KEY ( indexId )\n" + ")");
        loadESBConfigurationFromClasspath("artifacts/ESB/messageProcessorConfig/EmptyMsgBodyTest.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

    }

    @Test
    public void testWithEmptyMessage() throws Exception {
        logViewer.clearLogs();
        String proxyServiceUrl = getProxyServiceURLHttp("EmptyMsgBodyTestProxy");
        AxisServiceClient client = new AxisServiceClient();
        client.fireAndForget(null, proxyServiceUrl, "");
        Assert.assertTrue(Utils.checkForLog(logViewer, "REPLY = MESSAGE", 10),
                "Message with empty body not processed!");
    }

    @AfterClass
    public void cleanup() throws Exception {
        super.cleanup();
        DeleteDbFiles.execute(DB_PATH, "testDB", true);
        h2DatabaseManager.disconnect();
    }
}