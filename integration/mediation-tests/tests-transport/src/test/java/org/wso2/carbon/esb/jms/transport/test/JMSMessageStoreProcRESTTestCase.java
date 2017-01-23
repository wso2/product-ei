/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class JMSMessageStoreProcRESTTestCase extends ESBIntegrationTest {
    private static final String url = "http://localhost:8480/services/RESTProxy/new/add?name=jms";
    private static final String logLine0 =
            "MESSAGE = ************RESTProxy IN, IN-Content-Type = application/json, IN-Test-Header-Field = TestHeaderValue";
    private static final String logLine1 =
            "MESSAGE = ************SamplingSeq IN, IN-Content-Type = application/json, IN-Test-Header-Field = TestHeaderValue";

    private final SimpleHttpClient httpClient = new SimpleHttpClient();
    private final Map<String, String> headers = new HashMap<String, String>(1);
    private final String payload =  "{\n" +
                              "  \"email\" : \"jms@yomail.com\",\n" +
                              "  \"firstName\" : \"Jms\",\n" +
                              "  \"lastName\" : \"Broker\",\n" +
                              "  \"id\" : 10\n" +
                              "}";
    private LogViewerClient logViewer;


    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        headers.put("Test-Header-Field", "TestHeaderValue");
        //headers.put("Content-Type", "application/json");
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/JMSMessageStoreREST.xml");
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            Thread.sleep(2000);
            consumer.connect("RESTMessageStore");
        } finally {
            consumer.disconnect();
        }
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
        Thread.sleep(1000);
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "JMS Message store/processor support for RESTful services.")
    public void testJMSMessageStoreAndProcessor() throws Exception {
        HttpResponse response = httpClient.doPost(url, headers, payload, "application/json");
        Thread.sleep(10000);
        assertEquals(response.getStatusLine().getStatusCode(), 202);
        LogEvent[] logs = logViewer.getAllSystemLogs();
        int i = 1;
        for (LogEvent log : logs) {
            if (log.getMessage().contains(logLine0)) {
                ++i;
            }
            if (log.getMessage().contains(logLine1)) {
                ++i;
            }
        }
        if (i == 3) {
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false);
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}

