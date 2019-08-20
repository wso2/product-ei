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

package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class ESBJAVA3656MSMPTestCase extends ESBIntegrationTest {

	private ServerConfigurationManager serverManager = null;

	@BeforeClass(alwaysRun = true)
	protected void init() throws Exception {
		// START THE ESB
		super.init();
		OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/ESBJAVA-3656_MessageStore.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

		context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
		serverManager = new ServerConfigurationManager(context);
	}

	@Test(groups = { "wso2.esb" }, description = "Test if Message Processor is bound to the server")
	public void testMPBindingToServer() throws Exception {

		String url = getProxyServiceURLHttp("ESBJAVA3656Proxy");
		Map<String, String> headers = new HashMap<String, String>(1);
		headers.put("Content-Type", "application/xml");

		String payload ="<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">\n"+
		                " <soapenv:Body>\n"+
		                " <m0:getQuote xmlns:m0=\"http://services.samples\">\n"+
		                " <m0:request>\n"+
		                " <m0:symbol>IBM</m0:symbol>\n"+
		                " </m0:request>\n"+
		                " </m0:getQuote>\n"+
		                " </soapenv:Body>\n"+
		                "</soapenv:Envelope>";

		SimpleHttpClient httpClient = new SimpleHttpClient();
		httpClient.doPost(url, headers, payload, "application/xml");

		LogViewerClient logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
		logViewerClient.clearLogs();

		serverManager.applyConfiguration(new File(
				getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator +
				                       "jms/transport/axis2config/activemq/custom_server_name/axis2.xml").getPath()));
		super.init();

		OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/ESBJAVA-3656_MessageProcessor.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
		LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
		boolean logFound = false;
		for (LogEvent item : logs) {
			if (item.getPriority().equals("INFO")) {
				String message = item.getMessage();
				if (message.contains("***IN*****_BProxy_inSequence")) {
					logFound = true;
					break;
				}
			}
		}
		assertTrue(logFound);
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}
}
