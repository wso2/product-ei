/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.configuration.enums.AndesConfiguration;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.clients.operations.utils.JMSAcknowledgeMode;
import org.wso2.mb.integration.common.utils.backend.ConfigurationEditor;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * This class bares test cases with "DISCARD_ALLOWED" topic message delivery strategy
 */
public class DiscardAllowedMsgDeliveryStrategyTestCase extends MBIntegrationBaseTest {

	private static final String DISCARD_ALLOWED_TOPIC = "discardAllowedTopic";


	/**
	 * Initializes test case
	 *
	 * @throws javax.xml.xpath.XPathExpressionException
	 */
	@BeforeClass()
	public void init() throws XPathExpressionException, MalformedURLException {
		super.init(TestUserMode.SUPER_TENANT_USER);
	}

	/**
	 * Set topicMessageDeliveryStrategy to DISCARD_ALLOWED so that broker will simulate an acknowledgement
	 * if some subscribers are slow to acknowledge the message
	 *
	 * @throws XPathExpressionException
	 * @throws IOException
	 * @throws ConfigurationException
	 * @throws SAXException
	 * @throws XMLStreamException
	 * @throws LoginAuthenticationExceptionException
	 * @throws URISyntaxException
	 * @throws AutomationUtilException
	 */
	@BeforeClass
	public void setupConfiguration() throws XPathExpressionException, IOException,
			ConfigurationException, SAXException, XMLStreamException, LoginAuthenticationExceptionException,
			URISyntaxException, AutomationUtilException {

		super.serverManager = new ServerConfigurationManager(automationContext);
		String defaultMBConfigurationPath = ServerConfigurationManager.getCarbonHome() +
				File.separator + "repository" + File.separator + "conf" + File.separator + "broker.xml";

		ConfigurationEditor configurationEditor = new ConfigurationEditor(defaultMBConfigurationPath);

		configurationEditor.updateProperty(AndesConfiguration
				.PERFORMANCE_TUNING_TOPIC_MESSAGE_DELIVERY_STRATEGY, "DISCARD_ALLOWED");

		configurationEditor.updateProperty(AndesConfiguration.PERFORMANCE_TUNING_ACK_HANDLING_MAX_UNACKED_MESSAGES,
				"200");

		configurationEditor.applyUpdatedConfigurationAndRestartServer(serverManager);

	}

	/**
	 * 1. place subscriber A for DISCARD_ALLOWED_TOPIC with delay 0
	 * 2. place subscriber B for DISCARD_ALLOWED_TOPIC with delay 0
	 * 3. place subscriber C for DISCARD_ALLOWED_TOPIC with delay 200 milliseconds
	 * 4. place subscriber D for DISCARD_ALLOWED_TOPIC with delay 300 milliseconds
	 *
	 * verify all messages are received at the end. In long running this strategy prevents
	 * Out of Memory issues. Some acks are simulated flow slow subscribers.
	 *
	 * @throws AndesClientConfigurationException
	 * @throws JMSException
	 * @throws NamingException
	 * @throws IOException
	 * @throws AndesClientException
	 * @throws XPathExpressionException
	 * @throws CloneNotSupportedException
	 */
	@Test(groups = "wso2.mb", description = "Test server with DISCARD_ALLOWED topic message delivery strategy")
	public void performDiscardAllowedTopicMessageDelivery() throws AndesClientConfigurationException, JMSException, NamingException, IOException,
			AndesClientException, XPathExpressionException, CloneNotSupportedException {

		//Setting values for the sent and received message counts
		long sendToDiscardAllowedTopicCount = 2000;
		long expectedMessageCountPerSubscriber = sendToDiscardAllowedTopicCount;

		//setting up subscriber A
		AndesJMSConsumerClientConfiguration consumerConfig1 =
				new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, DISCARD_ALLOWED_TOPIC);
		consumerConfig1.setAcknowledgeMode(JMSAcknowledgeMode.AUTO_ACKNOWLEDGE);
		consumerConfig1.setMaximumMessagesToReceived(expectedMessageCountPerSubscriber);
		consumerConfig1.setPrintsPerMessageCount(expectedMessageCountPerSubscriber / 10L);
		consumerConfig1.setAsync(false);


		//setting up subscriber B
		AndesJMSConsumerClientConfiguration consumerConfig2 = consumerConfig1.clone();


		//setting up subscriber C
		AndesJMSConsumerClientConfiguration consumerConfig3 = consumerConfig1.clone();
		consumerConfig3.setRunningDelay(100);

		//setting up subscriber D
		AndesJMSConsumerClientConfiguration consumerConfig4 = consumerConfig1.clone();
		consumerConfig4.setRunningDelay(200);

		//starting consumers
		AndesClient consumerClient1 = new AndesClient(consumerConfig1, true);
		consumerClient1.startClient();
		AndesClient consumerClient2 = new AndesClient(consumerConfig2, true);
		consumerClient2.startClient();
		AndesClient consumerClient3 = new AndesClient(consumerConfig3, true);
		consumerClient3.startClient();
		AndesClient consumerClient4 = new AndesClient(consumerConfig4, true);
		consumerClient4.startClient();

		// Creating publisher configuration
		AndesJMSPublisherClientConfiguration publisherConfig1 =
				new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, DISCARD_ALLOWED_TOPIC);
		publisherConfig1.setNumberOfMessagesToSend(sendToDiscardAllowedTopicCount);
		publisherConfig1.setRunningDelay(50);
		publisherConfig1.setPrintsPerMessageCount(sendToDiscardAllowedTopicCount / 5L);

		//start publisher
		AndesClient publisherClient1 = new AndesClient(publisherConfig1, true);
		publisherClient1.startClient();

		//Receiving messages until message count gets stagnant and
		//Once done, stop client
		AndesClientUtils.waitForMessagesAndShutdown(consumerClient1, AndesClientConstants.DEFAULT_RUN_TIME);
		AndesClientUtils.waitForMessagesAndShutdown(consumerClient2, AndesClientConstants.DEFAULT_RUN_TIME);

		//these are very slow subscribers
		AndesClientUtils.waitForMessagesAndShutdown(consumerClient3, AndesClientConstants.DEFAULT_RUN_TIME);
		AndesClientUtils.waitForMessagesAndShutdown(consumerClient4, AndesClientConstants.DEFAULT_RUN_TIME );

		//verify all messages are published
		Assert.assertEquals(sendToDiscardAllowedTopicCount, publisherClient1.getSentMessageCount());

		//verify all messages are received
		org.testng.Assert.assertEquals(consumerClient1.getReceivedMessageCount(), expectedMessageCountPerSubscriber, "Did not "
				+ "receive expected message count for consumerClient1");
		org.testng.Assert.assertEquals(consumerClient2.getReceivedMessageCount(), expectedMessageCountPerSubscriber, "Did not "
				+ "receive expected message count for consumerClient2");
		org.testng.Assert.assertEquals(consumerClient3.getReceivedMessageCount(), expectedMessageCountPerSubscriber, "Did not "
				+ "receive expected message count for consumerClient3");
		org.testng.Assert.assertEquals(consumerClient4.getReceivedMessageCount(), expectedMessageCountPerSubscriber, "Did not "
				+ "receive expected message count for consumerClient4");

	}

	/**
	 * Restore to the previous configurations when the shared subscription test is complete.
	 *
	 * @throws IOException
	 * @throws AutomationUtilException
	 */
	@AfterClass
	public void tearDown() throws IOException, AutomationUtilException {
		super.serverManager.restoreToLastConfiguration(true);
	}
}
