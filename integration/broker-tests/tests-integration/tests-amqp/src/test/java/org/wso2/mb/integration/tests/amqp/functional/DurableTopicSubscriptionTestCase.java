/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mb.integration.tests.amqp.functional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

/**
 * This class holds set of test cases to verify if durable topic subscriptions happen according to
 * spec.
 */
public class DurableTopicSubscriptionTestCase extends MBIntegrationBaseTest {


    /**
     * Initializing test case
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass
    public void prepare() throws XPathExpressionException {
        init(TestUserMode.SUPER_TENANT_ADMIN);
    }

    /**
     * Creating a client with a subscription ID and unSubscribe it and create another client with
     * the same subscription ID.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void subscribeDisconnectAndSubscribeAgainTest()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic1");
        consumerConfig.setDurable(true, "durableSub1");
        consumerConfig.setAsync(false);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        initialConsumerClient.getConsumers().get(0).unSubscribe(false);

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient secondaryConsumerClient = new AndesClient(consumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        secondaryConsumerClient.getConsumers().get(0).unSubscribe(false);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();
    }

    /**
     * Create with sub id=x topic=y. Try another subscription with same params. should rejects the
     * subscription.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"}, expectedExceptions = javax.jms.JMSException.class,
            expectedExceptionsMessageRegExp = ".*Cannot subscribe to queue .* as it already has an existing exclusive consumer.*")
    public void multipleSubsWithSameIdTest()
            throws JMSException, NamingException, IOException, AndesClientConfigurationException,
                   AndesClientException, XPathExpressionException {
        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic2");
        consumerConfig.setDurable(true, "sriLanka");
        consumerConfig.setAsync(false);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient secondaryConsumerClient = new AndesClient(consumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * Create with sub id=x topic=y. Try another with sub id=z topic=y. Should be allowed.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void multipleSubsWithDifferentIdTest()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   CloneNotSupportedException, AndesClientException, XPathExpressionException {

        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic3");
        consumerConfig.setDurable(true, "test1");

        AndesJMSConsumerClientConfiguration secondaryConsumerConfig = consumerConfig.clone();
        secondaryConsumerConfig.setSubscriptionID("test2");
        secondaryConsumerConfig.setAsync(false);

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
     * 1. Create with sub id= x topic=y.
     * 2. Close it.
     * 3. Then try with sub id= x topic=z.
     * 4. Should reject the subscription.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws CloneNotSupportedException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"}, expectedExceptions = javax.jms.JMSException.class, expectedExceptionsMessageRegExp = ".*An Exclusive Bindings already exists for different topic.*")
    public void multipleSubsToDifferentTopicsWithSameSubIdTest()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   CloneNotSupportedException, AndesClientException, XPathExpressionException {

        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic4");
        consumerConfig.setDurable(true, "test3");
        consumerConfig.setAsync(false);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        initialConsumerClient.stopClient();

        AndesJMSConsumerClientConfiguration secondConsumerConfig = consumerConfig.clone();
        secondConsumerConfig.setDestinationName("myTopic5");
        secondConsumerConfig.setAsync(false);

        AndesClient secondaryConsumerClient = new AndesClient(secondConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * 1. Create with sub id=x topic=y.
     * 2. Create a normal topic subscription topic=y.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void durableTopicWithNormalTopicTest()
            throws JMSException, NamingException, AndesClientConfigurationException, IOException,
                   AndesClientException, XPathExpressionException {

        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic5");
        consumerConfig.setDurable(true, "test5");
        consumerConfig.setAsync(false);

        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        AndesJMSConsumerClientConfiguration secondConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic5");
        secondConsumerConfig.setAsync(false);
        AndesClient secondaryConsumerClient = new AndesClient(secondConsumerConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }

    /**
     * 1. Create with sub id=x topic=y.
     * 2. UnSubscribe.
     * 3. Now try sub id=z topic=y.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws AndesClientConfigurationException
     * @throws CloneNotSupportedException
     * @throws IOException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void multipleSubsWithDiffIDsToSameTopicTest()
            throws JMSException, NamingException, AndesClientConfigurationException,
                   CloneNotSupportedException, IOException, AndesClientException,
                   XPathExpressionException {
        // Creating configurations
        AndesJMSConsumerClientConfiguration firstConsumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "multiSubTopic");
        firstConsumerConfig.setAsync(false);
        firstConsumerConfig.setDurable(true, "new1");

        AndesJMSConsumerClientConfiguration secondConsumerConfig = firstConsumerConfig.clone();
        secondConsumerConfig.setSubscriptionID("new2");

        AndesJMSConsumerClientConfiguration thirdConsumerConfig = firstConsumerConfig.clone();
        thirdConsumerConfig.setSubscriptionID("new3");

        AndesJMSConsumerClientConfiguration forthConsumerConfig = firstConsumerConfig.clone();
        forthConsumerConfig.setSubscriptionID("new4");

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
     * 1. Create with sub id= x topic=y.
     * 2. UnSubscribe.
     * 3. Now try sub id= x topic=z.
     *
     * @throws JMSException
     * @throws NamingException
     * @throws IOException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void subscribeUnSubscribeAndTryDifferentTopicTest()
            throws JMSException, NamingException, IOException, AndesClientConfigurationException,
                   AndesClientException, XPathExpressionException {

        // Creating configurations
        AndesJMSConsumerClientConfiguration consumerConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic8");
        consumerConfig.setAsync(false);
        consumerConfig.setDurable(true, "test8");

        AndesJMSConsumerClientConfiguration secondaryConfig =
                new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "myTopic9");
        secondaryConfig.setAsync(false);
        secondaryConfig.setDurable(true, "test8");


        // Creating clients
        AndesClient initialConsumerClient = new AndesClient(consumerConfig, true);
        initialConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        initialConsumerClient.getConsumers().get(0).unSubscribe(false);

        AndesClientUtils.sleepForInterval(2000L);

        AndesClient secondaryConsumerClient = new AndesClient(secondaryConfig, true);
        secondaryConsumerClient.startClient();

        AndesClientUtils.sleepForInterval(2000L);

        secondaryConsumerClient.getConsumers().get(0).unSubscribe(false);

        // Stopping the clients
        initialConsumerClient.stopClient();
        secondaryConsumerClient.stopClient();

        AndesClientUtils.sleepForInterval(2000L);
    }
}
