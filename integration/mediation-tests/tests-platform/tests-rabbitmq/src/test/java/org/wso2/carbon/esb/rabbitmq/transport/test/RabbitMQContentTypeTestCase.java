/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.rabbitmq.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.rabbitmq.utils.RabbitMQServerInstance;
import org.wso2.carbon.esb.rabbitmq.utils.RabbitMQTestUtils;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.rabbitmqclient.RabbitMQProducerClient;

import java.io.File;

/**
 * Test RabbitMQ receiver with different content-types and with content type service parameter
 */
public class RabbitMQContentTypeTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;
    private RabbitMQProducerClient sender;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        sender = RabbitMQServerInstance.createProducerWithDeclaration("exchange2", "simple_consumer_test");
        //The consumer proxy cannot be pre-deployed since the queue declaration(which is done in 'initRabbitMQBroker')
        // must happen before deployment.
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator
                                          + "ESB" + File.separator + "rabbitmq" + File.separator +
                                          "transport" + File.separator + "rabbitmq_consumer_proxy.xml");
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = { "wso2.esb" }, description = "Test RabbitMQ consumer with no content type")
    public void testContentTypeEmpty() throws Exception {
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;
        String message = "<ser:placeOrder xmlns:ser=\"http://services.samples\">\n" + "<ser:order>\n"
                         + "<ser:price>100</ser:price>\n" + "<ser:quantity>2000</ser:quantity>\n"
                         + "<ser:symbol>RMQ</ser:symbol>\n" + "</ser:order>\n" + "</ser:placeOrder>";
        sender.sendMessage(message, null);
        RabbitMQTestUtils.waitForLogToGetUpdated();

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        boolean setDefaultContentType = false;
        int count = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String logMessage = logs[i].getMessage();
            if (logMessage.contains("Unable to determine content type for message")
                && logMessage.contains("setting to text/plain")) {
                setDefaultContentType = true;
            }
            if (logMessage.contains("received = true")) {
                count++;
            }
        }

        Assert.assertTrue(setDefaultContentType, "Default content type is not set to text/plain");
        Assert.assertEquals(count, 1, "All messages are not received from queue");
    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        sender.disconnect();
        super.cleanup();
    }
}
