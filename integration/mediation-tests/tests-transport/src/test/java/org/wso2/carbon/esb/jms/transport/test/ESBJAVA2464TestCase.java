package org.wso2.carbon.esb.jms.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

public class ESBJAVA2464TestCase extends ESBIntegrationTest {

	private static final String logLine0 =
	                                       "org.wso2.carbon.proxyadmin.service.ProxyServiceAdmin is not an admin service. Service name ";

	private LogViewerClient logViewer;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
		uploadSynapseConfig();
	}

	private void uploadSynapseConfig() throws Exception {
		loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File.separator +
		                                  "synapseconfig" + File.separator + "nonBlockingHTTP" +
		                                  File.separator + "local_jms_proxy_synapse.xml");
	}
	
	@Test(groups = { "wso2.esb" }, description = "Test ESBJAVA2464 proxy service with jms and nonBlockingLocal transport")
	public void testMessageInjection() throws Exception {
		Thread.sleep(7000);

		JMSQueueMessageProducer sender =
		                                 new JMSQueueMessageProducer(
		                                                             JMSBrokerConfigurationProvider.getInstance()
		                                                                                           .getBrokerConfiguration());
		String message =
		                 "<?xml version='1.0' encoding='UTF-8'?>"
		                         + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:echo=\"http://echo.services.core.carbon.wso2.org\">"
		                         + "  <soapenv:Header/>" + "  <soapenv:Body>"
		                         + "     <echo:echoInt>" + "        <!--Optional:-->"
		                         + "       <in>1</in>" + "     </echo:echoInt>"
		                         + "  </soapenv:Body>" + "</soapenv:Envelope>";
		try {
			sender.connect("echoProxy");
			for (int i = 0; i < 3; i++) {
				sender.pushMessage(message);
			}
		} finally {
			sender.disconnect();
		}

		LogEvent[] logs = logViewer.getAllSystemLogs();
		for (LogEvent log : logs) {
			if (log.getMessage().contains(logLine0)) {
				Assert.fail(logLine0 + "is in log");
			}
		}
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		// Restore the axis2 configuration altered by this test case
		super.cleanup();
	}

}
