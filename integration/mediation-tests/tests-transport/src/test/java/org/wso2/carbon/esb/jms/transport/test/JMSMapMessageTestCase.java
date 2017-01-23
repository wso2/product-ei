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
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;

import javax.jms.MapMessage;
import javax.jms.Message;

public class JMSMapMessageTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/jms_map_message_proxy_service.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
    }

    @Test(groups = {"wso2.esb"}, description = "Test proxy service with jms transport")
    public void testJMSProxy() throws Exception {

        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        Thread.sleep(10000);
        String queueName = "JmsProxy";
        try {
            sender.connect(queueName);
            for (int i = 0; i < 3; i++) {
                sender.pushMessage("<?xml version='1.0' encoding='UTF-8'?>" +
                                   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                                   " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">" +
                                   "   <soapenv:Header/>" +
                                   "   <soapenv:Body>" +
                                   "      <ser:placeOrder>" +
                                   "         <ser:order>" +
                                   "            <xsd:price>100</xsd:price>" +
                                   "            <xsd:quantity>2000</xsd:quantity>" +
                                   "            <xsd:symbol>JMSTransport</xsd:symbol>" +
                                   "         </ser:order>" +
                                   "      </ser:placeOrder>" +
                                   "   </soapenv:Body>" +
                                   "</soapenv:Envelope>");
            }
        } finally {
            sender.disconnect();
        }

        Thread.sleep(10000);
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect("target");
            for (int i = 0; i < 3; i++) {
                Message msg  = consumer.popRawMessage();
                if (msg != null) {
                    log.info("Message in queue " + msg);
                    if(!(msg instanceof MapMessage)){
                        Assert.fail("Message is not a Map message");
                    }
                } else {
                    Assert.fail("No message found in target queue");
                }
//                if (consumer.popMessage() != null) {
//                    Assert.fail("JMS Proxy service failed to pick the messages from Queue");
//                }
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
