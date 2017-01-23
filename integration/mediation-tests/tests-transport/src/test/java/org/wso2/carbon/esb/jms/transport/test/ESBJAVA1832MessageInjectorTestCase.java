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

import java.util.concurrent.TimeUnit;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;

/**
 * https://wso2.org/jira/browse/ESBJAVA-1832
 * Unable to store messages with out setting axis2 service instance in a JMS message store.
 */

public class ESBJAVA1832MessageInjectorTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement msgProessor = esbUtils.loadResource("/artifacts/ESB/jms/transport/msgInjection/msg_store.xml");
        OMElement task = esbUtils.loadResource("/artifacts/ESB/jms/transport/msgInjection/msg_injecting_task.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(msgProessor));
        esbUtils.addScheduleTask(contextUrls.getBackEndUrl(),getSessionCookie(), task);
    }

    @Test(groups = {"wso2.esb"}, description = "Test proxy service with jms transport")
    public void testMessageInjection() throws Exception {
        String queueName = "jmsQueue";
        int numberOfMsgToExpect = 10;
        TimeUnit.SECONDS.sleep(15);
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect(queueName);
            for (int i = 0; i < numberOfMsgToExpect; i++) {
                if (consumer.popMessage(javax.jms.Message.class) == null) {
                    Assert.fail("Unable to pop the expected number of message in the queue " + queueName);
                }
            }
        } finally {
            consumer.disconnect();
        }
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        esbUtils.deleteScheduleTask(contextUrls.getBackEndUrl(),getSessionCookie(),"TheTask", "synapse.simple.quartz");
        super.cleanup();
    }
}
