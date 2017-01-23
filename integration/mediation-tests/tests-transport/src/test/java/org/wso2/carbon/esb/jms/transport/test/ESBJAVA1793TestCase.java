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

package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.Utils;

/**
 * https://wso2.org/jira/browse/ESBJAVA-1793
 * Message Processor ignoring endpoint format when getting endpoint from target.endpoint property
 */

public class ESBJAVA1793TestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/ESBJAVA-1793MessageStore-targetEndPointFormat-pox.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

    }

    @Test(groups = {"wso2.esb"}, description = "Test Message Store Target Endpoint format as POX" , enabled = false)
    public void testMessageProcessorTargetEndpointFormatPox() throws Exception {
        AxisServiceClient client = new AxisServiceClient();

        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect("messageStoreTargetEndpointQueue");
            //sending messages to proxy service
            for (int i = 0; i < 3; i++) {
                client.sendRobust(Utils.getStockQuoteRequest("JMS"), getProxyServiceURLHttp("messageStoreTargetEndpointFormatTestProxy"), "getQuote");
            }
            //wait for messages to reach the destination queue
            Thread.sleep(5000);
            for (int i = 0; i < 3; i++) {
                String message = consumer.popMessage();
                Assert.assertNotNull(message, "Message not found. message sent by proxy service not reached to the destination Queue");
                Assert.assertEquals(message, "<ns:getQuote xmlns:ns=\"http://services.samples\"><" +
                                             "ns:request><ns:symbol>JMS</ns:symbol></ns:request></ns:getQuote>"
                        , "Message Contains mismatched");
            }
        } finally {
            consumer.disconnect();
        }

    }


    @AfterClass(alwaysRun = true)
    public void UndeployService() throws Exception {
        super.cleanup();
    }
}
