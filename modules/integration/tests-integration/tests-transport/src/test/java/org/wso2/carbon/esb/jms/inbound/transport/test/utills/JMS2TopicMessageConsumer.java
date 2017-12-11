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
package org.wso2.carbon.esb.jms.inbound.transport.test.utills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JMS2TopicMessageConsumer implements Runnable {
	private Log log = LogFactory.getLog(this.getClass());
	private Connection connection = null;
	private Session session = null;
	private MessageConsumer consumer = null;
	private TopicConnectionFactory connectionFactory = null;
	private Topic destination = null;
	private volatile boolean stopFlag;
	private int msgCount;

	public JMS2TopicMessageConsumer(JMSBrokerConfiguration brokerConfiguration)
			throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial",
		                  brokerConfiguration.getInitialNamingFactory());
		props.setProperty("java.naming.provider.url", brokerConfiguration.getProviderURL());
		InitialContext ctx = new InitialContext(props);
		this.connectionFactory = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
		this.destination = (Topic) ctx.lookup("/topic/exampleTopic");
	}

	public void consume() throws JMSException, NamingException {
		setStopFlag(false);
		msgCount = 0;
		this.connection = this.connectionFactory.createConnection();
		this.connection.start();
		this.session = this.connection.createSession(false, 1);
		this.consumer = this.session.createSharedConsumer(destination, "mySubscription");
		TextMessage messageReceived;
		while (!stopFlag) {
			messageReceived = (TextMessage) consumer.receive(1);
			if (messageReceived != null) {
				msgCount++;
			}
		}
		disconnect();
	}

	public void setStopFlag(boolean state) {
		this.stopFlag = state;
	}

	public int getMessageCount() {
		return msgCount;
	}

	public void disconnect() {
		if (this.consumer != null) {
			try {
				this.consumer.close();
			} catch (JMSException var4) {
				;
			}
		}

		if (this.session != null) {
			try {
				this.session.close();
			} catch (JMSException var3) {
				;
			}
		}

		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (JMSException var2) {
				;
			}
		}

	}

	@Override
	public void run() {
		try {
			consume();
		} catch (JMSException e) {
			log.info(e + ", Can not create sample JMS Consumer");
		} catch (NamingException e) {
			log.info(e + ", Can not create sample JMS Consumer");
		}
	}
}
