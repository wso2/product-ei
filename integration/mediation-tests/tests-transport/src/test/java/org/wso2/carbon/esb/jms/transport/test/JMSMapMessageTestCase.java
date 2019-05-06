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
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.jms.MapMessage;
import javax.jms.Message;

public class JMSMapMessageTestCase extends ESBIntegrationTest {

    private List<Message> messages = new ArrayList<>();
    private int NUM_OF_MESSAGES = 3;
    private static final String IN_QUEUE_NAME = "JMSMapMessageTestIn";
    private static final String OUT_QUEUE_NAME = "JMSMapMessageTestOut";
    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/jms_map_message_proxy_service.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

        Awaitility.await()
                  .pollInterval(50, TimeUnit.MILLISECONDS)
                  .atMost(60, TimeUnit.SECONDS)
                  .until(isServiceDeployed(IN_QUEUE_NAME));
    }

    @Test(groups = {"wso2.esb"}, description = "Test proxy service with jms transport")
    public void testJMSProxy() throws Exception {

        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        String queueName = IN_QUEUE_NAME;
        try {
            sender.connect(queueName);
            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
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

        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect(OUT_QUEUE_NAME);
            Awaitility.await()
                      .pollInterval(50, TimeUnit.MILLISECONDS)
                      .atMost(60, TimeUnit.SECONDS)
                      .until(isMessagesConsumed(consumer));
            for (int i = 0; messages.size() < NUM_OF_MESSAGES; i++) {
                Message msg  = messages.get(i);
                if (msg != null) {
                    log.info("Message in queue " + msg);
                    if(!(msg instanceof MapMessage)){
                        Assert.fail("Message is not a Map message");
                    }
                } else {
                    Assert.fail("No message found in target queue");
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

    private Callable<Boolean> isMessagesConsumed(final JMSQueueMessageConsumer consumer) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Message msg = consumer.popRawMessage();
                if (msg instanceof MapMessage) {
                    messages.add(msg);
                }
                return messages.size() == NUM_OF_MESSAGES;
            }
        };
    }

    private Callable<Boolean> isServiceDeployed(final String proxyName) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isProxySuccesfullyDeployed(proxyName);
            }
        };
    }
}
