/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.platform.tests.clustering.durable.topic;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.clients.AndesAdminClient;
import org.wso2.mb.integration.common.clients.operations.clients.TopicAdminClient;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.platform.common.utils.MBPlatformBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This class holds set of test cases to verify if durable topic
 * subscriptions cluster wise happen according to specification.
 */
public class DurableTopicSubscriptionOnlyTestCase extends MBPlatformBaseTest {
    private String hostNode1;
    private String hostNode2;
    private int portInNode1;
    private int portInNode2;
    private TopicAdminClient topicAdminClient;

    /**
     * Prepare environment for tests.
     *
     * @throws XPathExpressionException
     * @throws LoginAuthenticationExceptionException
     * @throws IOException
     * @throws XMLStreamException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, LoginAuthenticationExceptionException, IOException,
            XMLStreamException, URISyntaxException, SAXException, AutomationUtilException {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);

        AutomationContext automationContext1 = getAutomationContextWithKey("mb002");
        AutomationContext automationContext2 = getAutomationContextWithKey("mb003");
        hostNode1 = automationContext1.getInstance().getHosts().get("default");
        hostNode2 = automationContext2.getInstance().getHosts().get("default");
        portInNode1 = Integer.parseInt(automationContext1.getInstance().getPorts().get("amqp"));
        portInNode2 = Integer.parseInt(automationContext2.getInstance().getPorts().get("amqp"));
        topicAdminClient = new TopicAdminClient(automationContext1.getContextUrls().getBackEndUrl(),
                super.login(automationContext1));

        super.initAndesAdminClients();
    }

    /**
     * Create with sub id= x topic=y. Disconnect and try to connect again from a different node.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Reconnect to topic with same sub ID after " +
                                            "disconnecting", enabled = true)
    public void subscribeDisconnectAndSubscribeAgainTest()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   AndesClientException {
        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "durableTopic1");
        consumerConfig.setDurable(true, "durableTopic1");

        AndesJMSConsumerClientConfiguration secondConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode2, portInNode2, ExchangeType.TOPIC, "durableTopic1");
        secondConsumerConfig.setDurable(true, "durableTopic1");

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        initialConsumerClient.getConsumers().get(0).unSubscribe(false);

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient secondaryConsumerClient = new AndesClient(secondConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        secondaryConsumerClient.getConsumers().get(0).unSubscribe(false);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();
    }

    /**
     * Create with sub id= x topic=y. try another subscription from a different node with same
     * params.Should rejects the subscription
     *
     * @throws AndesClientConfigurationException
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Try to connect to a topic with same subscription ID " +
                                            "which is already a subscription", enabled = true,
            expectedExceptions = JMSException.class, expectedExceptionsMessageRegExp = ".*it already has an existing exclusive consumer.*")
    public void multipleSubsWithSameIdTest()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException {
        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "durableTopic2");
        consumerConfig.setDurable(true, "durableTopic2Sub");

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesJMSConsumerClientConfiguration secondConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode2, portInNode2, ExchangeType.TOPIC, "durableTopic2");
        secondConsumerConfig.setDurable(true, "durableTopic2Sub");

        AndesClient secondaryConsumerClient = new AndesClient(secondConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * Create with sub id= x topic=y. try another subscription from a different node with a
     * different subscription ID.Should allow the subscription
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Try to connect to same topic with different " +
                                            "subscription IDs", enabled = true)
    public void multipleSubToSameTopicTest()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   AndesClientException {
        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "durableTopic3");
        consumerConfig.setDurable(true, "durableTopic3Sub1");

        AndesJMSConsumerClientConfiguration secondaryConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode2, portInNode2, ExchangeType.TOPIC, "durableTopic3");
        secondaryConsumerConfig.setDurable(true, "durableTopic3Sub2");

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient secondaryConsumerClient = new AndesClient(secondaryConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * Create with sub id= x topic=y. Unsubscribe and try to connect another subscription for the
     * same topic from a different node.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Reconnect to topic with different sub ID after " +
                                            "unsubscribing", enabled = true)
    public void subscribeUnsubscribeWithDifferentIDsTest()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   AndesClientException {
        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "durableTopic5");
        consumerConfig.setDurable(true, "durableTopic5Sub1");

        AndesJMSConsumerClientConfiguration secondConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode2, portInNode2, ExchangeType.TOPIC, "durableTopic5");
        secondConsumerConfig.setDurable(true, "durableTopic5Sub2");

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        initialConsumerClient.stopClient();


        AndesClient secondaryConsumerClient = new AndesClient(secondConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * Create with sub id= x topic=y. Unsubscribe. Then try to connect with the same subscription
     * to a different topic from another node
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Connect to a different topic with same sub ID after " +
                                            "unsubscribing", enabled = true)
    public void sameIdDifferentTopicsTest()
            throws JMSException, NamingException, IOException, AndesClientConfigurationException,
                   AndesClientException {
        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "durableTopic6");
        consumerConfig.setDurable(true, "durableTopic6Sub1");

        AndesJMSConsumerClientConfiguration secondConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode2, portInNode2, ExchangeType.TOPIC, "durableTopic6");
        secondConsumerConfig.setDurable(true, "durableTopic7");

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient secondaryConsumerClient = new AndesClient(secondConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * Create with sub id= x topic=y
     * Create with sub id= z topic=y
     * Create a normal topic subscriber topic=y
     * Create a normal queue subscriber queue=y
     *
     * @throws JMSException
     * @throws NamingException
     * @throws XPathExpressionException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = "wso2.mb", description = "Create all kinds of subscriptions for same " +
                                            "topic/queue name", enabled = true)
    public void allKindOfSubscriptionsTest()
            throws JMSException, NamingException, XPathExpressionException,
                   AndesClientConfigurationException, IOException, AndesClientException {

        // Creating configurations
        AndesJMSConsumerClientConfiguration firstConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "wso2");
        firstConsumerConfig.setDurable(true, "wso2Sub1");

        AndesJMSConsumerClientConfiguration secondConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode2, portInNode2, ExchangeType.TOPIC, "wso2");
        secondConsumerConfig.setDurable(true, "wso2Sub2");

        AndesJMSConsumerClientConfiguration thirdConsumerConfig =
                new AndesJMSConsumerClientConfiguration(hostNode1, portInNode1, ExchangeType.TOPIC, "wso2");

        String randomInstanceKey = getRandomMBInstance();
        AutomationContext tempContext = getAutomationContextWithKey(randomInstanceKey);

        AndesJMSConsumerClientConfiguration forthConsumerConfig =
                new AndesJMSConsumerClientConfiguration(tempContext.getInstance().getHosts().get("default"),
                              Integer.parseInt(tempContext.getInstance().getPorts().get("amqp")), ExchangeType.QUEUE, "wso2");

        // Creating clients
        AndesClient firstConsumerClient = new AndesClient(firstConsumerConfig, true);
        firstConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient secondConsumerClient = new AndesClient(secondConsumerConfig, true);
        secondConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient thirdConsumerClient = new AndesClient(thirdConsumerConfig, true);
        thirdConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient forthConsumerClient = new AndesClient(forthConsumerConfig, true);
        forthConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        // Stopping the clients
        firstConsumerClient.stopClient();
        secondConsumerClient.stopClient();
        thirdConsumerClient.stopClient();
        forthConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * Cleanup after running tests.
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        //deleting the topics created
        topicAdminClient.removeTopic("durableTopic1");
        topicAdminClient.removeTopic("durableTopic2");
        topicAdminClient.removeTopic("durableTopic3");
        topicAdminClient.removeTopic("durableTopic4");
        topicAdminClient.removeTopic("durableTopic5");
        topicAdminClient.removeTopic("durableTopic6");
        topicAdminClient.removeTopic("durableTopic7");
        topicAdminClient.removeTopic("wso2");

        //deleting the queue created
        String randomInstanceKey = getRandomMBInstance();
        AndesAdminClient tempAndesAdminClient = getAndesAdminClientWithKey(randomInstanceKey);
        if (null != tempAndesAdminClient.getQueueByName("wso2")) {
            tempAndesAdminClient.deleteQueue("wso2");
        }
    }
}
