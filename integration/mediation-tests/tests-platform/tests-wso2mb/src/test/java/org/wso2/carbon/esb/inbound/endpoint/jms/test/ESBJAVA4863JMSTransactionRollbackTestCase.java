/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.inbound.endpoint.jms.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSTopicMessagePublisher;
import org.wso2.carbon.esb.MessageBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 * https://wso2.org/jira/browse/ESBJAVA-4293
 * https://wso2.org/jira/browse/ESBJAVA-4863
 * https://wso2.org/jira/browse/ESBJAVA-4864
 */

public class ESBJAVA4863JMSTransactionRollbackTestCase extends ESBIntegrationTest {
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB"
                                          + File.separator + "synapseconfig" + File.separator + "ESBJAVA4863synapseconfig.xml");
        OMElement playGroundInboundEp = esbUtils.loadResource("artifacts" + File.separator + "ESB"
                                                              + File.separator + "synapseconfig"
                                                              + File.separator + "ESBJAVA4863playground-inbound.xml" );

        OMElement jmsInboundEp = esbUtils.loadResource("artifacts" + File.separator + "ESB"
                                                              + File.separator + "synapseconfig"
                                                              + File.separator + "ESBJAVA4863jmsinbound.xml" );
        addInboundEndpoint(playGroundInboundEp);
        addInboundEndpoint(jmsInboundEp);
    }

    @Test(groups = {"wso2.esb"}, description = "Test fault sequence on error : ESBJAVA-4864")
    public void faultSequenceOnErrorTest() throws Exception {
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();
        JMSQueueMessageProducer mbQueueMessageProducer = new JMSQueueMessageProducer(
                MessageBrokerConfigurationProvider.getBrokerConfig());
        mbQueueMessageProducer.connect("playground");
        mbQueueMessageProducer.pushMessage("{\n" +
                                           "   \"msg\": {\n" +
                                           "      \"getQuote1\": {\n" +
                                           "         \"request\": {\n" +
                                           "            \"symbol\": \"WSO2\"\n" +
                                           "         }\n" +
                                           "      }\n" +
                                           "   }\n" +
                                           "}");
        mbQueueMessageProducer.disconnect();

        boolean faultSequenceInvoked = false;
        long startTime = System.currentTimeMillis();

        while (!faultSequenceInvoked && (System.currentTimeMillis() - startTime) < 15000) {
            LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
            if (logs == null) {
                continue;
            }
            for (LogEvent event : logs) {
                String message = event.getMessage();
                if (message.contains("Fault sequence invoked")) {
                    faultSequenceInvoked = true;
                    break;
                }
            }

            Thread.sleep(1000);
        }

        Assert.assertTrue(faultSequenceInvoked, "Fault Sequence not invoked on error while building the message");
    }

    @Test(groups = {"wso2.esb"}, description = "Test transaction rollback : ESBJAVA-4863 and ESBJAVA-4293")
    public void transactionRolBackWhenErrorTest() throws Exception {
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();
        JMSTopicMessagePublisher mbTopicMessageProducer = new JMSTopicMessagePublisher(
                MessageBrokerConfigurationProvider.getBrokerConfig());
        mbTopicMessageProducer.connect("MyNewTopic");
        mbTopicMessageProducer.publish("<message>Hi</message>");
        mbTopicMessageProducer.disconnect();

        int rollbackCount = 0;
        long startTime = System.currentTimeMillis();

        while (rollbackCount < 10 && (System.currentTimeMillis() - startTime) < 30000) {
            LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
            if(logs == null){
                continue;
            }
            rollbackCount = 0;
            for (LogEvent event : logs) {
                if(log != null) {
                    String message = event.getMessage();
                    if (message.contains("### I am Event subscriber (inbound endpoint) ###")) {
                        rollbackCount++;
                    }
                }
            }

            Thread.sleep(1000);
        }
        //if the message failed to mediate ESB rollback the message. Then message broker try 10 times
        //to send the message again. So total count is 11 including the first try
        Assert.assertEquals(rollbackCount, 11,  "ESB does not process message again after rollback");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
