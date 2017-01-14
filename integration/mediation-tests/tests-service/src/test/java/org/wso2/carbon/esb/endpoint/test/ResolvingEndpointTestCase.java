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

package org.wso2.carbon.esb.endpoint.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Resolving Endpoints test class
 */
public class ResolvingEndpointTestCase extends ESBIntegrationTest {
	private final String ENDPOINT_NAME = "resolvingEP";
	private EndPointAdminClient endPointAdminClient;

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init();
		endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
		addResolvingEndpoint();
		loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator +
		                                  "endpoint" + File.separator + "resolvingEndpointConfig" + File.separator +
		                                  "synapse.xml");

	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, description = "Sending a Message to a dynamically resolved endpoint")
	public void testSendingToDynamicallyResolvedEndpoint() throws Exception {

		OMElement response = axis2Client.sendSimpleStockQuoteRequest(
				"http://localhost:8480/services/StockQuoteProxy?myKey=resolvingEP", null, "WSO2");
		Assert.assertNotNull(response);
		Assert.assertTrue(response.toString().contains("WSO2 Company"));
	}

	@AfterClass(alwaysRun = true)
	public void close() throws Exception {
		super.cleanup();
	}

	private void addResolvingEndpoint() throws Exception {
		addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		                                 "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"resolvingEP\">\n" +
		                                 "    <address " +
		                                 "uri=\"http://localhost:9000/services/SimpleStockQuoteService\">\n" +
		                                 "        <suspendOnFailure>\n" +
		                                 "            <progressionFactor>1.0</progressionFactor>\n" +
		                                 "        </suspendOnFailure>\n" +
		                                 "        <markForSuspension>\n" +
		                                 "            <retriesBeforeSuspension>0</retriesBeforeSuspension>\n" +
		                                 "            <retryDelay>0</retryDelay>\n" +
		                                 "        </markForSuspension>\n" +
		                                 "    </address>\n" +
		                                 "</endpoint>"));

		String[] endpoints = endPointAdminClient.getEndpointNames();
		if (endpoints != null && endpoints.length > 0 && endpoints[0] != null) {
			List endpointList = Arrays.asList(endpoints);
			assertTrue(endpointList.contains(ENDPOINT_NAME));
		} else {
			fail("Endpoint has not been added to the system properly");
		}

	}

}

