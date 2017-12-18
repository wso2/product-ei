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

import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JMS2QueueMessageProducer {
	private Connection connection = null;
	private Session session = null;
	private MessageProducer producer = null;
	private QueueConnectionFactory connectionFactory = null;
	private Queue destination = null;

	public JMS2QueueMessageProducer(JMSBrokerConfiguration brokerConfiguration, String queue)
			throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial",
		                  brokerConfiguration.getInitialNamingFactory());
		props.setProperty("java.naming.provider.url", brokerConfiguration.getProviderURL());
		InitialContext ctx = new InitialContext(props);
		this.connectionFactory = (QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");
		this.destination = (Queue)ctx.lookup(queue);
	}

	public void connect() throws JMSException, NamingException {
		this.connection = this.connectionFactory.createConnection();
		this.connection.start();
		this.session = this.connection.createSession(false, 1);
		this.producer = this.session.createProducer(destination);
		this.producer.setDeliveryMode(1);
	}

	public void disconnect() {
		if (this.producer != null) {
			try {
				this.producer.close();
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

	public void pushMessage(String messageContent) throws Exception {
		if (this.producer == null) {
			throw new Exception("No Connection with Queue. Please connect");
		} else {
			TextMessage message = this.session.createTextMessage(messageContent);
			this.producer.send(message);
		}
	}

	public void sendBytesMessage(byte[] payload) throws Exception {
		BytesMessage bm = this.session.createBytesMessage();
		bm.writeBytes(payload);
		this.producer.send(bm);
	}
}

