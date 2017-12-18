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
package org.wso2.carbon.esb.jms.transport.test.utills;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

/**
 * Used to create a queue message consumer for JMS brokers
 * session.createQueue() doesn't work with hornetQ
 * */
public class JMS2QueueMessageConsumer {
	private Connection connection = null;
	private Session session = null;
	private QueueConnectionFactory connectionFactory = null;
	private Queue destination = null;
	private MessageConsumer consumer = null;

	public JMS2QueueMessageConsumer(JMSBrokerConfiguration brokerConfiguration, String queue) throws NamingException {
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
		this.consumer = this.session.createConsumer(this.destination);
	}

	public void disconnect() {
		if(this.consumer != null) {
			try {
				this.consumer.close();
			} catch (JMSException var4) {
				;
			}
		}

		if(this.session != null) {
			try {
				this.session.close();
			} catch (JMSException var3) {
				;
			}
		}

		if(this.connection != null) {
			try {
				this.connection.close();
			} catch (JMSException var2) {
				;
			}
		}

	}

	public String popMessage() throws Exception {
		if(this.consumer == null) {
			throw new Exception("No Consumer with Queue. Please connect");
		} else {
			Message message = this.consumer.receive(10000L);
			if(message != null) {
				if(message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage)message;
					return textMessage.getText();
				} else {
					throw new Exception("Test Framework Exception. Message Type is not a TextMessage");
				}
			} else {
				return null;
			}
		}
	}

	public <T> T popMessage(Class<T> clzz) throws Exception {
		if(this.consumer == null) {
			throw new Exception("No Consumer with Queue. Please connect");
		} else {
			Message message = this.consumer.receive(10000L);
			if(message != null) {
				if(clzz.isInstance(message)) {
					return clzz.cast(message);
				} else {
					throw new Exception("Test Framework Exception. Unexpected Message Type to cast");
				}
			} else {
				return null;
			}
		}
	}

	public Message popRawMessage() throws Exception {
		if(this.consumer == null) {
			throw new Exception("No Consumer with Queue. Please connect");
		} else {
			return this.consumer.receive(10000L);
		}
	}

	public List<String> getMessages() throws Exception {
		if(this.session == null) {
			throw new Exception("No Connection with Queue. Please connect");
		} else {
			ArrayList list = new ArrayList();
			QueueBrowser browser = this.session.createBrowser((Queue)this.destination);
			Enumeration enu = browser.getEnumeration();

			while(enu.hasMoreElements()) {
				TextMessage message = (TextMessage)enu.nextElement();
				list.add(message.getText());
			}

			browser.close();
			return list;
		}
	}
}
