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

package org.wso2.mb.integration.tests.server.mgt;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.andes.metrics.MetricsConstants;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.metrics.common.MetricsConfigException;
import org.wso2.carbon.metrics.data.common.Metric;
import org.wso2.carbon.metrics.data.common.MetricAttribute;
import org.wso2.carbon.metrics.data.common.MetricList;
import org.wso2.carbon.metrics.data.common.MetricType;
import org.wso2.carbon.metrics.impl.MetricsLevelConfigException;
import org.wso2.carbon.metrics.manager.jmx.MetricManagerMXBean;
import org.wso2.carbon.metrics.view.ui.MetricDataWrapper;
import org.wso2.carbon.metrics.view.ui.MetricsViewClient;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * These test will validate metrics functionality in message broker.
 * This class has 4 test cases.
 * 1. Metrics reporting functionality test case
 * 2. Validate queue subscribers count metric
 * 3. Validate topic subscribers count metric
 */
public class MetricsTestCase extends MBIntegrationBaseTest {

	/**
	 * Session cookie is used maintain communication session with metrics view client
	 */
	private String sessionCookie;

	/**
	 * Initializing test case
	 *
	 * @throws XPathExpressionException
	 */
	@BeforeClass(alwaysRun = true) public void init()
			throws XPathExpressionException, MalformedURLException, AutomationUtilException,
			       MetricsConfigException, MetricsLevelConfigException, RemoteException,
			       LoginAuthenticationExceptionException {
		super.init(TestUserMode.SUPER_TENANT_USER);

		LoginLogoutClient loginLogoutClientForAdmin =
				new LoginLogoutClient(super.automationContext);
		sessionCookie = loginLogoutClientForAdmin.login();
	}


	/**
	 * This test case will validate queue subscriber count metric.
	 * Steps -
	 * 1. Create 5 subscribers and start sending messages.
	 * 2. Report metric.
	 * 3. Validate metric.
	 * @throws AndesClientConfigurationException
	 * @throws JMSException
	 * @throws NamingException
	 * @throws IOException
	 * @throws AndesClientException
	 */
	@Test(groups = "wso2.mb", description = "Queue subscribers count metric test case")
	public void performQueueSubscriberCountTestCase()
			throws AndesClientConfigurationException, JMSException, NamingException, IOException,
			       AndesClientException, InterruptedException, AutomationUtilException,
			       XPathExpressionException, MalformedObjectNameException {

		long msgCount = 1000L;
		int subscriberCount = 5;

		// Creating a consumer client configuration
		AndesJMSConsumerClientConfiguration consumerConfig =
				new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueue");
		consumerConfig.setMaximumMessagesToReceived(msgCount * 2);
		consumerConfig.setPrintsPerMessageCount(msgCount / 10L);

		// Creating a publisher client configuration
		AndesJMSPublisherClientConfiguration publisherConfig =
				new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueue");
		publisherConfig.setNumberOfMessagesToSend(msgCount);
		publisherConfig.setPrintsPerMessageCount(msgCount / 10L);

		// Creating subscribers
		AndesClient consumerClient = null;
		for (int i = 0; i < subscriberCount; i++) {
			consumerClient = new AndesClient(consumerConfig, true);
			consumerClient.startClient();
		}

		// Creating publishers
		AndesClient publisherClient = new AndesClient(publisherConfig, true);
		publisherClient.startClient();

		//Waiting for clients to start
		Thread.sleep(5000);
		long fromTime = System.currentTimeMillis();
		// Submit metrics data
		invokeJMXReportOperation();

		// Get metrics data from database

		MetricsViewClient metricsViewClient;
		MetricList metricList = new MetricList();
		MetricDataWrapper metricData;

		metricsViewClient = new MetricsViewClient(sessionCookie, backendURL, null);
		String source = metricsViewClient.getAllSources()[0];
		ArrayList<Metric> metrics = new ArrayList<>();
		metrics.add(new Metric(MetricType.GAUGE, "org.wso2.mb.queue.subscribers.count",
		                       "Total Queue Subscribers", MetricAttribute.VALUE, null));
		metricList.setMetric(metrics.toArray(new Metric[metrics.size()]));
		metricData =
				metricsViewClient.findLastMetrics(metricList, source, String.valueOf(fromTime));

		// Stopping subscribers and publishers
		consumerClient.stopClient();
		publisherClient.stopClient();

		// Evaluating metrics data
		Assert.assertEquals(metricData.getData()[0][1].intValue(), subscriberCount,
		                    metricData.getData()[0][1].intValue() + " subscribers found.");

	}

	/**
	 * This test case will validate topic subscriber count metric.
	 * Steps -
	 * 1. Create 5 subscribers and start sending messages.
	 * 2. Report metric.
	 * 3. Validate metric.
	 * @throws AndesClientConfigurationException
	 * @throws JMSException
	 * @throws NamingException
	 * @throws IOException
	 * @throws AndesClientException
	 * @throws InterruptedException
	 * @throws AutomationUtilException
	 * @throws XPathExpressionException
	 */
	@Test(groups = "wso2.mb", description = "Topic subscribers count metric test case")
	public void performTopicSubscriberCountTestCase()
			throws AndesClientConfigurationException, JMSException, NamingException, IOException,
			       AndesClientException, InterruptedException, AutomationUtilException,
			       XPathExpressionException, MalformedObjectNameException {

		long msgCount = 1000L;
		int subscriberCount = 5;

		// Creating a consumer client configuration
		AndesJMSConsumerClientConfiguration consumerConfig =
				new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "singleTopic");
		consumerConfig.setMaximumMessagesToReceived(msgCount * 2);
		consumerConfig.setPrintsPerMessageCount(msgCount / 10L);

		// Creating a publisher client configuration
		AndesJMSPublisherClientConfiguration publisherConfig =
				new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.TOPIC, "singleTopic");
		publisherConfig.setNumberOfMessagesToSend(msgCount);
		publisherConfig.setPrintsPerMessageCount(msgCount / 10L);

		// Creating subscribers
		AndesClient consumerClient = null;
		for (int i = 0; i < subscriberCount; i++) {
			consumerClient = new AndesClient(consumerConfig, true);
			consumerClient.startClient();
		}

		// Creating publishers
		AndesClient publisherClient = new AndesClient(publisherConfig, true);
		publisherClient.startClient();

		//Waiting for clients to start
		Thread.sleep(5000);
		long fromTime = System.currentTimeMillis();
		// Submit metrics data
		invokeJMXReportOperation();

		// Get metrics data from database

		MetricsViewClient metricsViewClient;
		MetricList metricList = new MetricList();
		MetricDataWrapper metricData;

		metricsViewClient = new MetricsViewClient(sessionCookie, backendURL, null);
		String source = metricsViewClient.getAllSources()[0];
		ArrayList<Metric> metrics = new ArrayList<>();
		metrics.add(new Metric(MetricType.GAUGE, "org.wso2.mb.topic.subscribers.count",
		                       "Total Topic Subscribers", MetricAttribute.VALUE, null));
		metricList.setMetric(metrics.toArray(new Metric[metrics.size()]));
		metricData =
				metricsViewClient.findLastMetrics(metricList, source, String.valueOf(fromTime));

		// Stopping subscribers and publishers

		consumerClient.stopClient();
		publisherClient.stopClient();

		// Evaluating metrics data

		Assert.assertEquals(metricData.getData()[0][1].intValue(), subscriberCount,
		                    metricData.getData()[0][1].intValue() + " subscribers found.");


	}

	/**
	 * This test case will run through basic message life cycle and will check if the relevant
	 * metrics are reported.
	 * @throws AndesClientConfigurationException
	 * @throws JMSException
	 * @throws NamingException
	 * @throws IOException
	 * @throws AndesClientException
	 */
	@Test(groups = "wso2.mb", description = "Metrics report test case")
	public void performMetricsReportTestCase()
            throws AndesClientConfigurationException, JMSException, NamingException, IOException,
                   AndesClientException, InterruptedException, MalformedObjectNameException,
                   XPathExpressionException {
		long msgCount = 1000L;

		// Creating a consumer client configuration
		AndesJMSConsumerClientConfiguration consumerConfig =
				new AndesJMSConsumerClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueue");
		consumerConfig.setMaximumMessagesToReceived(msgCount * 2);
		consumerConfig.setPrintsPerMessageCount(msgCount / 10L);

		// Creating a publisher client configuration
		AndesJMSPublisherClientConfiguration publisherConfig =
				new AndesJMSPublisherClientConfiguration(getAMQPPort(), ExchangeType.QUEUE, "singleQueue");
		publisherConfig.setNumberOfMessagesToSend(msgCount);
		publisherConfig.setPrintsPerMessageCount(msgCount / 10L);

		// Creating subscriber
		AndesClient consumerClient = new AndesClient(consumerConfig, true);
		consumerClient.startClient();

		// Creating publisher
		AndesClient publisherClient = new AndesClient(publisherConfig, true);
		publisherClient.startClient();

		//Waiting for clients to start
		Thread.sleep(5000);
		long fromTime = System.currentTimeMillis();
		// Submit metrics data
		invokeJMXReportOperation();

		// Get metrics data from database

		MetricsViewClient metricsViewClient;
		MetricList metricList = new MetricList();
		MetricDataWrapper metricData;

		metricsViewClient = new MetricsViewClient(sessionCookie, backendURL, null);
		String source = metricsViewClient.getAllSources()[0];
		ArrayList<Metric> metrics = new ArrayList<>();

		// Adding metrics to be retrieved from database

		// Disruptor metrics

		metrics.add(new Metric(MetricType.GAUGE, MetricsConstants.DISRUPTOR_INBOUND_RING,
		                       "Total Messages in Inbound Disruptor", MetricAttribute.VALUE,
		                       null));
		metrics.add(new Metric(MetricType.GAUGE, MetricsConstants.DISRUPTOR_MESSAGE_ACK,
		                       "Total Acks in Inbound Disruptor", MetricAttribute.VALUE, null));
		metrics.add(new Metric(MetricType.GAUGE, MetricsConstants.DISRUPTOR_OUTBOUND_RING,
		                       "Total Messages in Outbound Disruptor", MetricAttribute.VALUE,
		                       null));

		// Publisher and subscriber metrics

		metrics.add(new Metric(MetricType.GAUGE, MetricsConstants.QUEUE_SUBSCRIBERS,
		                       "Total Queue Subscribers", MetricAttribute.VALUE, null));
		metrics.add(new Metric(MetricType.GAUGE, MetricsConstants.ACTIVE_CHANNELS,
		                       "Total Channels", MetricAttribute.VALUE, null));

		// Message and ack rate metrics

		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_RECEIVE_RATE,
		                       "Received Messages Mean Rate", MetricAttribute.MEAN_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_RECEIVE_RATE,
		                       "Received Messages Last Minute Rate", MetricAttribute.M1_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_RECEIVE_RATE,
		                       "Received Messages Last 5 Minutes Rate", MetricAttribute.M5_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_RECEIVE_RATE,
		                       "Received Messages Last 15 Minutes Rate", MetricAttribute.M15_RATE, null));

		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_RECEIVE_RATE,
		                       "Received Acknowledgements Mean Rate", MetricAttribute.MEAN_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_RECEIVE_RATE,
		                       "Received Acknowledgements Last Minute Rate", MetricAttribute.M1_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_RECEIVE_RATE,
		                       "Received Acknowledgements Last 5 Minutes Rate", MetricAttribute.M5_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_RECEIVE_RATE,
		                       "Received Acknowledgements Last 15 Minutes Rate", MetricAttribute.M15_RATE, null));

		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_SENT_RATE, "Sent Messages Mean Rate",
		                       MetricAttribute.MEAN_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_SENT_RATE,
		                       "Sent Messages Last Minute Rate", MetricAttribute.M1_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_SENT_RATE,
		                       "Sent Messages Last 5 Minutes Rate", MetricAttribute.M5_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.MSG_SENT_RATE,
		                       "Sent Messages Last 15 Minutes Rate", MetricAttribute.M15_RATE, null));

		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_SENT_RATE, "Sent Acknowledgements Mean Rate",
		                       MetricAttribute.M1_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_SENT_RATE,
		                       "Sent Acknowledgements Last Minute Rate", MetricAttribute.M1_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_SENT_RATE,
		                       "Sent Acknowledgements Last 5 Minutes Rate", MetricAttribute.M5_RATE, null));
		metrics.add(new Metric(MetricType.METER, MetricsConstants.ACK_SENT_RATE,
		                       "Sent Acknowledgements Last 15 Minutes Rate", MetricAttribute.M15_RATE, null));

		// Database metrics

		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Minimum",
		                       MetricAttribute.MIN, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Mean",
		                       MetricAttribute.MEAN, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Maximum",
		                       MetricAttribute.MAX, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Standard Deviation",
		                       MetricAttribute.STDDEV, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, " 50th Percentile",
		                       MetricAttribute.P50, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, " 75th Percentile",
		                       MetricAttribute.P75, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "95th Percentile",
		                       MetricAttribute.P95, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "98th Percentile",
		                       MetricAttribute.P98, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "99th Percentile",
		                       MetricAttribute.P99, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "999th Percentile",
		                       MetricAttribute.P999, null));

		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Mean Rate",
		                       MetricAttribute.MEAN_RATE, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Last Minute Rate",
		                       MetricAttribute.M1_RATE, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Last 5 Minutes Rate",
		                       MetricAttribute.M5_RATE, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_READ, "Last 15 Minutes Rate",
		                       MetricAttribute.M15_RATE, null));

		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Minimum",
		                       MetricAttribute.MIN, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Mean",
		                       MetricAttribute.MEAN, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Maximum",
		                       MetricAttribute.MAX, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Standard Deviation",
		                       MetricAttribute.STDDEV, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "50th Percentile",
		                       MetricAttribute.P50, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "75th Percentile",
		                       MetricAttribute.P75, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "95th Percentile",
		                       MetricAttribute.P95, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "98th Percentile",
		                       MetricAttribute.P98, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "99th Percentile",
		                       MetricAttribute.P99, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "999th Percentile",
		                       MetricAttribute.P999, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Mean Rate",
		                       MetricAttribute.MEAN_RATE, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Last Minute Rate",
		                       MetricAttribute.M1_RATE, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Last 5 Minutes Rate",
		                       MetricAttribute.M5_RATE, null));
		metrics.add(new Metric(MetricType.TIMER, MetricsConstants.DB_WRITE, "Last 15 Minutes Rate",
		                       MetricAttribute.M15_RATE, null));

		metricList.setMetric(metrics.toArray(new Metric[metrics.size()]));
		metricData =
				metricsViewClient.findLastMetrics(metricList, source, String.valueOf(fromTime));

		// Stopping subscribers and publishers

		consumerClient.stopClient();
		publisherClient.stopClient();

		// Evaluating metrics data
		BigDecimal value;
		for (int i = 0; i < metricData.getMetadata().getNames().length; i++) {
			value = metricData.getData()[0][i];
			Assert.assertTrue(null != value,
			                  "Metric [" + metricData.getMetadata().getNames()[i] +
			                  "] value was not reported.");
		}



	}

	/**
	 * This method will force metric manager to collect metrics by invoking report() method
	 * using remote jmx
	 * @throws IOException
	 * @throws MalformedObjectNameException
	 */
	private void invokeJMXReportOperation()
            throws IOException, MalformedObjectNameException, XPathExpressionException {

        int JMXServicePort = getJMXServerPort();
        int RMIRegistryPort = getRMIRegistryPort();

		JMXServiceURL url =
				new JMXServiceURL("service:jmx:rmi://localhost:" + JMXServicePort +
                                  "/jndi/rmi://localhost:" + RMIRegistryPort + "/jmxrmi");
		Map<String, String[]> env = new HashMap<>();
		String[] credentials = {"admin", "admin"};
		env.put(JMXConnector.CREDENTIALS, credentials);
		JMXConnector jmxConnector = JMXConnectorFactory.connect(url, env);
		MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
		ObjectName mbeanName = new ObjectName("org.wso2.carbon:type=MetricManager");
		MetricManagerMXBean mbeanProxy =
				MBeanServerInvocationHandler.newProxyInstance(
						mbeanServerConnection, mbeanName, MetricManagerMXBean.class, true);
		mbeanProxy.report();
		jmxConnector.close();
	}
}
