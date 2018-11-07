/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.mb.platform.tests.clustering;

import com.google.common.net.HostAndPort;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.platform.common.utils.DataAccessUtil;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
import org.wso2.mb.platform.common.utils.exceptions.DataAccessUtilException;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Perform tests for message expiry and expired message deletion
 */
public class MessageExpirationTestCase extends MBPlatformBaseTest{

    /**
     * To check the database content
     */
    private DataAccessUtil dataAccessUtil = new DataAccessUtil();

    /**
     * Prepare environment for tests.
     *
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws LoginAuthenticationExceptionException, IOException, XPathExpressionException,
            URISyntaxException, SAXException, XMLStreamException, AutomationUtilException{
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        super.initAndesAdminClients();
    }


    /**
     * Publish 4000 messages when there is no subscription. Default safe slot buffer count is 3 and slot window size
     * is 1000. So 3000 (3*1000) messages should not be interpreted by the periodic deletion task as those can be
     * allocated to slots in the near future. Periodic deletion task should delete the remaining 1000 (4000-3000)
     * expired messages. Then create a subscription. The messages allocated into slots and put into delivery path and
     * eventually get caught at the flusher as expired messages. Messages caught by the flusher is accumulated for a
     * batch delete. Then they should be deleted by the pre-delivery expiry message deletion task.
     *
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws NamingException
     * @throws JMSException
     * @throws AndesClientException
     * @throws IOException
     * @throws InterruptedException
     * @throws DataAccessUtilException
     */
    @Test(groups = "wso2.mb", description = "Message expiration and Expired message deletion")
    public void testMessageExpiry()
            throws XPathExpressionException, AndesClientConfigurationException, NamingException, JMSException,
            AndesClientException, IOException, InterruptedException, DataAccessUtilException,
            CloneNotSupportedException {

        long sendCount = 1000L;
        String queueName = "clusterExpiryCheckQueue1";

        String randomInstanceKey = getRandomMBInstance();

        AutomationContext tempContext = getAutomationContextWithKey(randomInstanceKey);

        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(tempContext.getInstance().getHosts().get("default"),
                        Integer.parseInt(tempContext.getInstance().getPorts().get("amqp")),
                        ExchangeType.QUEUE, queueName);

        //To create the queue, start the subscription and disconnect it.
        AndesClient consumerClient = new AndesClient(consumerConfig, true);
        consumerClient.startClient();
        AndesClientUtils.waitForMessagesAndShutdown(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        AndesJMSPublisherClientConfiguration publisherConfig =
                new AndesJMSPublisherClientConfiguration(tempContext.getInstance().getHosts().get("default"),
                        Integer.parseInt(tempContext.getInstance().getPorts().get("amqp")),
                        ExchangeType.QUEUE, queueName);
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setJMSMessageExpiryTime(1000);

        AndesClient publisherClient = new AndesClient(publisherConfig, true);
        publisherClient.startClient();
        //2 seconds wait for all messages got expired
        Thread.sleep(2000);

        //Creating a consumer
        AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig.clone();
        HostAndPort randomAMQPBrokerAddress = getRandomAMQPBrokerAddress();
        consumerConfig2.setHostName(randomAMQPBrokerAddress.getHostText());
        consumerConfig2.setPort(randomAMQPBrokerAddress.getPort());
        AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
        consumerClient2.startClient();

        //since all the messages get expired in 1 sec and caught at Message flusher. So there should not be any messages
        // to deliver
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), 0,"Message receiving failed.");
        //30 seconds sleep for pre delivery expiry message deletion task  to delete the messages captured at flusher
        Thread.sleep(30000);

        //Evaluate messages left in database
        Assert.assertEquals(dataAccessUtil.getMessageCountForQueue(queueName), 0, "Expired message deletion failed.");

    }

}
