package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.esb.jms.transport.test.utills.JMS2QueueMessageConsumer;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class JMSSenderDelayedDeliveryTestCase extends ESBIntegrationTest {

	private static final String logLine0 =
			"org.wso2.carbon.proxyadmin.service.ProxyServiceAdmin is not an admin service. Service name ";
	private ServerConfigurationManager serverConfigurationManager;
	private LogViewerClient logViewer;
	private static final String queueName = "queue/mySampleQueue";

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		serverConfigurationManager =
				new ServerConfigurationManager(
						new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		//Load libs to ESB instance
		serverConfigurationManager.copyToComponentLib(new File(
				TestConfigurationProvider.getResourceLocation() + "artifacts" +
				File.separator + "ESB" + File.separator + "jar" + File.separator +
				"hornetq-all-new.jar"));
		serverConfigurationManager.copyToComponentLib(new File(
				TestConfigurationProvider.getResourceLocation() + "artifacts" +
				File.separator + "ESB" + File.separator + "jar" + File.separator +
				"javax.jms-api-2.0.1.jar"));

		serverConfigurationManager
				.applyConfiguration(new File(
						TestConfigurationProvider.getResourceLocation() +
						"artifacts" + File.separator + "ESB" + File.separator +
						"jms" + File.separator + "transport" + File.separator +
						"axis2config" + File.separator + "hornetq" +
						File.separator + "axis2.xml"));

		super.init(); // After restarting, this will establish the sessions.
		uploadSynapseConfig();
		logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
	}

	private void uploadSynapseConfig() throws Exception {
		OMElement synapse =
				esbUtils.loadResource("artifacts" +
				                      File.separator + "ESB" + File.separator +
				                      "jms" + File.separator + "transport" +
				                      File.separator + "delayed_delivery_proxy_synapse.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
		//		loadESBConfigurationFromClasspath( "artifacts" +
		//				File.separator + "ESB" + File.separator +
		//				"jms" + File.separator + "transport" +
		//				File.separator + "delayed_delivery_proxy_synapse.xml");
	}

	@Test(groups = { "wso2.esb" }, description = "Testing JMS 2.0 feature, Message Delivery Delay")
	public void testDelayedDelivery() throws Exception {
		boolean isTestPassed = false;
		JMS2QueueMessageConsumer consumer = new JMS2QueueMessageConsumer(
				org.wso2.carbon.esb.jms.inbound.transport.test.utills.HornetQBrokerConfigurationProvider
						.getInstance().getBrokerConfiguration(), queueName);
		String jmsMessage;
		consumer.connect();
		Thread.sleep(5000);
		AxisServiceClient client = new AxisServiceClient();
		//		client.sendRobust(Utils.getStockQuoteRequest("JMS"),
		//		                  getProxyServiceURLHttp("JMSDeliveryDelayed"),
		//		                  "placeorder");
		client.sendRobust(Utils.getStockQuoteRequest("JMS"),
		                  getProxyServiceURLHttp("JMSDeliveryDelayed"), "getQuote");
		//Time parameters
		long timeSent = System.currentTimeMillis();
		long timeLimit = timeSent + TimeUnit.SECONDS.toMillis(20);
		long timeReceived = 0;

		while (System.currentTimeMillis() < timeLimit) {
			jmsMessage = consumer.popMessage();
			if (jmsMessage != null) {
				timeReceived = System.currentTimeMillis();
				if (timeReceived > timeSent + TimeUnit.SECONDS.toMillis(10))
					isTestPassed = true;
				break;
			}
		}
		Assert.assertTrue(isTestPassed, "JMS 2.0 Delayed delivery test failed");
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {

		// Restore the axis2 configuration altered by this test case
		super.cleanup();
		//serverConfigurationManager.restoreToLastConfiguration();
	}

}
