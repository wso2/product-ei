/*
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

public class JMSMessageProcessorTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/jms_message_store_and_processor_service.xml");
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            //to create a subscription for WSO2 MB. then JMSEndPoint queue is created in MB
            consumer.connect("JMSProcessorEndPoint");
        } finally {
            consumer.disconnect();
        }
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
    }

    @Test(groups = {"wso2.esb"}, description = "Test proxy service with jms transport")
    public void testJMSMessageStoreAndProcessor() throws Exception {
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());

        AxisServiceClient client = new AxisServiceClient();
        for (int i = 0; i < 5; i++) {
            client.sendRobust(Utils.getStockQuoteRequest("JMS"), getProxyServiceURLHttp("JMSStoreAndProcessorTestCaseProxy"), "getQuote");
        }

        Thread.sleep(10000);
        try {
            consumer.connect("JMSProcessorEndPoint");
            for (int i = 0; i < 10; i++) {
                if (i < 5) {
                    //first 5 messages should be in the queue
                    Assert.assertNotNull(consumer.popMessage(), "JMS Message Processor not send message to endpoint");

                } else {
                    //after 5 no messages should be in the queue
                    Assert.assertNull(consumer.popMessage(), "JMS Message Processor sends same message more than once ");
                }
            }
        } finally {
            consumer.disconnect();
        }
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
