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
package org.wso2.carbon.esb.jms.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.inbound.stub.types.carbon.InboundEndpointDTO;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.inbound.endpoint.InboundAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * class tests adding,updating and deleting inbound endpoints
 */
public class JMSInboundTransportTestCase extends ESBIntegrationTest {
	private ServerConfigurationManager serverConfigurationManager;
	private InboundAdminClient inboundAdminClient;
	private ActiveMQServer activeMQServer = new ActiveMQServer();

	@BeforeClass(alwaysRun = true)
	protected void init() throws Exception {
		activeMQServer.startJMSBrokerAndConfigureESB();
		super.init();
		serverConfigurationManager =
				new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		OMElement synapse =
				esbUtils.loadResource("/artifacts/ESB/jms/inbound/transport/jms_transport_proxy_service.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
		inboundAdminClient = new InboundAdminClient(context.getContextUrls().getBackEndUrl(),getSessionCookie());
	}

	@Test(groups = {"wso2.esb" }, description = "Adding New JMS Inbound End point")
	public void testAddingNewJMSInboundEndpoint()
			throws Exception {
		addInboundEndpoint(addEndpoint1());
	}

	@Test(groups = { "wso2.esb" }, description = "Updating Existing JMS Inbound End point",
			dependsOnMethods = "testAddingNewJMSInboundEndpoint")
	public void testUpdatingJMSInboundEndpoint() throws Exception {
		updateInboundEndpoint(addEndpoint2());
		InboundEndpointDTO inboundEndpoint =
				inboundAdminClient.getInboundEndpointbyName(addEndpoint1().getAttributeValue(new QName("name")));
		assertNotNull(inboundEndpoint, "JMS Inbound Endpoint is null");
		log.info("Inbound endpoint injected : " + inboundEndpoint.getInjectingSeq());
		assertEquals(inboundEndpoint.getInjectingSeq(), "main", "Inbound Endpoint has not been updated properly");
	}

	@Test(groups = {"wso2.esb" }, description = "Deleting an JMS Inbound End point", dependsOnMethods =
			"testUpdatingJMSInboundEndpoint")
	public void testDeletingJMSInboundEndpoint() throws Exception {
		deleteInboundEndpointFromName(addEndpoint1().getAttributeValue(new QName("name")));
		isInboundUndeployed(addEndpoint1().getAttributeValue(new QName("name")));
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
		activeMQServer.stopJMSBrokerRevertESBConfiguration();
	}

	private OMElement addEndpoint1() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">10000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">0</parameter>\n" +
				            "        <parameter name=\"transport.jms" +
				            ".ConnectionFactoryJNDIName\">QueueConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">queue</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint2() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS\"\n" +
				            "                 sequence=\"main\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">10000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">0</parameter>\n" +
				            "        <parameter name=\"transport.jms" +
				            ".ConnectionFactoryJNDIName\">QueueConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">queue</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

}
