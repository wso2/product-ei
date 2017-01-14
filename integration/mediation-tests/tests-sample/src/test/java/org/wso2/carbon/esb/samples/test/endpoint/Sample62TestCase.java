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
package org.wso2.carbon.esb.samples.test.endpoint;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.tcpmon.client.ConnectionData;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

/**
 * Sample 62: Routing a Message to a Dynamic List of Recipients and Aggregating Responses
 */
public class Sample62TestCase extends ESBIntegrationTest {

	private SampleAxis2Server axis2Server1;
	private SampleAxis2Server axis2Server2;
	private SampleAxis2Server axis2Server3;

	private TCPMonListener listener1;
	private TCPMonListener listener2;
	private TCPMonListener listener3;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		loadSampleESBConfiguration(62);

		axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
		axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");
		axis2Server3 = new SampleAxis2Server("test_axis2_server_9003.xml");

		axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
		axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE_2);
		axis2Server3.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE_3);

		axis2Server1.start();
		axis2Server2.start();
		axis2Server3.start();

		listener1 = new TCPMonListener(9100, "localhost", 9001);
		listener2 = new TCPMonListener(9200, "localhost", 9002);
		listener3 = new TCPMonListener(9300, "localhost", 9003);

		SynapseConfigAdminClient synapseConfigAdminClient =
				new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
		String config = synapseConfigAdminClient.getConfiguration();
		config = config.replace("9001", "9100").replace("9002", "9200").replace("9003", "9300");
		config = config.replace("//m0:getQuoteResponse", "//m0:getSimpleQuoteResponse");
		synapseConfigAdminClient.updateConfiguration(config);

		listener1.start();
		listener2.start();
		listener3.start();

	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" },
	      description = "Routing a Message to a Dynamic List of Recipients and Aggregating Responses")
	public void testRoutingMessagesAndAggregatingResponses() throws Exception {

		OMElement response = axis2Client.sendSimpleQuoteRequest("http://localhost:8480/", null, "WSO2");
		System.out.println(response.toString());

		Assert.assertTrue(response.toString().contains("getSimpleQuoteResponse"),
		                  "GetSimpleQuoteResponse not found");
		Assert.assertTrue(response.toString().contains("WSO2 Company"), "WSO2 Company not found");

		Thread.sleep(3000);

		boolean is9001Called = isAxisServiceCalled(listener1);
		boolean is9002Called = isAxisServiceCalled(listener2);
		boolean is9003Called = isAxisServiceCalled(listener3);

		Assert.assertTrue(is9001Called, "Service 9001 not called");
		Assert.assertTrue(is9002Called, "Service 9002 not called");
		Assert.assertTrue(is9003Called, "Service 9003 not called");

		listener1.clear();
		listener2.clear();
		listener3.clear();
	}

	@AfterClass(alwaysRun = true)
	public void close() throws Exception {
		super.cleanup();

		if (axis2Server1.isStarted()) {
			axis2Server1.stop();
		}
		if (axis2Server2.isStarted()) {
			axis2Server2.stop();
		}
		if (axis2Server3.isStarted()) {
			axis2Server3.stop();
		}

		listener1.stop();
		listener2.stop();
		listener3.stop();

	}

	private boolean isAxisServiceCalled(TCPMonListener listener) throws Exception {
		for (ConnectionData connectionData : listener.getConnectionData().values()) {
			if (connectionData.getOutputText().toString().contains("WSO2 Company")) {
				return true;
			}
		}
		return false;
	}

}