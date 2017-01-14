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
package org.wso2.carbon.esb.jms.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import javax.jms.JMSException;

/**
 * Tests JMS transactions with inbound endpoints.
 */
public class JMSInboundTransactionTestCase extends ESBIntegrationTest {

	private LogViewerClient logViewerClient = null;
	private ActiveMQServer activeMQServer = new ActiveMQServer();
	String message;

	@BeforeClass(alwaysRun = true)
	protected void init() throws Exception {

		activeMQServer.startJMSBrokerAndConfigureESB();
		super.init();

		OMElement synapse =
				esbUtils.loadResource(
						"/artifacts/ESB/jms/inbound/transport/jms_inbound_transaction.xml");
		updateESBConfiguration(synapse);
		message = String.valueOf(
				esbUtils.loadResource("artifacts/ESB/jms/inbound/transport/message.xml"));
		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
	}

	@Test(groups = { "wso2.esb" }, description = "Successfully committing the message" , enabled = false)
	public void testTransactionCommit() throws Exception {

		int beforeLogCount;
		boolean successValue = false;
		String queueName = "localq";
		LogEvent[] logs;
		JMSQueueMessageProducer sender =
				new JMSQueueMessageProducer(
						JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());

		beforeLogCount = logViewerClient.getAllRemoteSystemLogs().length;
		try {
			sender.connect(queueName);
			sender.pushMessage(message);
		} finally {
			sender.disconnect();
		}
		try {
			addInboundEndpoint(esbUtils.loadResource(
					"artifacts/ESB/jms/inbound/transport/jms_commit_synapse.xml"));
			Thread.sleep(10000);
		} catch (JMSException e) {
			log.info(e + ", Error while adding the inbound endpoint");
		}
		logs = logViewerClient.getAllRemoteSystemLogs();
		for (int i = 0; i < (logs.length - beforeLogCount); i++) {
			if (logs[i].getMessage().contains("Committed")) {
				successValue = checkForQueue("Committed");
				break;
			}
		}
		Assert.assertTrue(successValue, "Error while performing commit");
		deleteInboundEndpoints();
		Thread.sleep(3000);
	}

	@Test(groups = { "wso2.esb" }, description = "Rolling back the failed message to the queue" , enabled = false)
	public void testTransactionRollBack() throws Exception {

		boolean successValue;
		String queueName = "localq";
		JMSQueueMessageProducer sender =
				new JMSQueueMessageProducer(
						JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		try {
			sender.connect(queueName);
			sender.pushMessage(message);
		} finally {
			sender.disconnect();
		}
		addInboundEndpoint(esbUtils.loadResource(
				"artifacts/ESB/jms/inbound/transport/jms_rollback_synapse.xml"));
		Thread.sleep(8000);
		successValue = checkForQueue("Rollbacked");
		Assert.assertTrue(successValue, "Error while performing rollback");
		deleteInboundEndpoints();
		Thread.sleep(3000);
	}

	private boolean checkForQueue(String status) throws Exception {

		String poppedMessage = null;
		JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(
				JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		try {
			consumer.connect("localq");
			poppedMessage = consumer.popMessage();
		} finally {
			consumer.disconnect();
		}
		if (status.equals("Committed") && poppedMessage == null) {
			return true;
		} else if (status.equals("Rollbacked") && poppedMessage != null) {
			return true;
		}
		return false;
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
		Thread.sleep(3000);
		activeMQServer.stopJMSBrokerRevertESBConfiguration();

	}

}
