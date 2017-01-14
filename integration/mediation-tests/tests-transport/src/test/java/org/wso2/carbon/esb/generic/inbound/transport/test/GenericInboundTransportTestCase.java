/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.generic.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.clients.inbound.endpoint.InboundAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;

import java.io.File;

public class GenericInboundTransportTestCase extends ESBIntegrationTest {
	private LogViewerClient logViewerClient = null;
    //TODO: Add this jar to correct location
	private final String CLASS_JAR = "org.wso2.carbon.inbound.endpoint.test-1.0.jar";
	private final String JAR_LOCATION = "/artifacts/ESB/jar";
	private InboundAdminClient inboundAdminClient;
	private ServerConfigurationManager serverConfigurationManager;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		init();
		serverConfigurationManager = new ServerConfigurationManager(context);
		serverConfigurationManager.copyToComponentLib(new File(getClass().getResource(JAR_LOCATION + File.separator +
		                                                                              CLASS_JAR)
		                                                                 .toURI()));
		OMElement synapse =
				esbUtils.loadResource("/artifacts/ESB/generic/inbound/transport/generic_inbound_transport_config.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
		serverConfigurationManager.restartGracefully();

		init();
		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

	}

	@Test(groups = { "wso2.esb" }, description = "Test Adding Generic Inbound End point")
	public void testAddingGenericInboundEndpoints() throws Exception {
		int beforeLogCount = logViewerClient.getAllSystemLogs().length;
		addInboundEndpoint(addEndpoint1());
		Thread.sleep(5000);
		LogEvent[] logs = logViewerClient.getAllSystemLogs();
		boolean status = false;
		for (int i = 0; i < (logs.length - beforeLogCount); i++) {
			if (logs[i].getMessage().contains("Generic Polling Consumer Invoked")) {
				status = true;
				break;
			}
		}

		Assert.assertTrue(status, "There is no Generic Inbound Endpoint.");

		deleteInboundEndpoints();

	}

	@Test(groups = { "wso2.esb" }, description = "Test creating Generic Inbound EP without sequence")
	public void testInjectingInvalidSequence() throws Exception {
		int beforeLogCount = logViewerClient.getAllSystemLogs().length;
		addInboundEndpoint(addEndpoint2());
		LogEvent[] logs = logViewerClient.getAllSystemLogs();
		boolean status = false;
		for (int i = 0; i < (logs.length - beforeLogCount); i++) {
			if (logs[i].getMessage().contains("Sequence name not specified")) {
				status = true;
				break;
			}
		}

		Assert.assertTrue(status, "There is no Generic Inbound Endpoint.");

		deleteInboundEndpoints();

	}

	@Test(groups = { "wso2.esb" }, description = "Test creating Generic Inbound EP without implementation class")
	public void testWithoutImplementationClass() throws Exception {
		int beforeLogCount = logViewerClient.getAllSystemLogs().length;
		addInboundEndpoint(addEndpoint3());
		LogEvent[] logs = logViewerClient.getAllSystemLogs();
		boolean status = false;
		for (int i = 0; i < (logs.length - beforeLogCount); i++) {
			if (logs[i].getMessage().contains("Please check the required class is added to the classpath")) {
				status = true;
				break;
			}
		}

		Assert.assertTrue(status, "There is no Generic Inbound Endpoint.");

		deleteInboundEndpoints();

	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
		serverConfigurationManager.removeFromComponentLib(CLASS_JAR);
		serverConfigurationManager.restartGracefully();

		serverConfigurationManager = null;
	}

	private OMElement addEndpoint1() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"Test1\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 class=\"org.wso2.carbon.inbound.endpoint.test.GenericConsumer\"\n" +
				            "                 suspend=\"false\">\n" +
				            "   <parameters>\n" +
				            "      <parameter name=\"interval\">1000</parameter>\n" +
				            "   </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint2() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"Test2\"\n" +
				            "                 sequence=\"\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 class=\"org.wso2.carbon.inbound.endpoint.test.GenericConsumer\"\n" +
				            "                 suspend=\"false\">\n" +
				            "   <parameters>\n" +
				            "      <parameter name=\"interval\">1000</parameter>\n" +
				            "   </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint3() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"Test4\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 class=\"\"\n" +
				            "                 suspend=\"false\">\n" +
				            "   <parameters>\n" +
				            "      <parameter name=\"interval\">1000</parameter>\n" +
				            "   </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}
}
