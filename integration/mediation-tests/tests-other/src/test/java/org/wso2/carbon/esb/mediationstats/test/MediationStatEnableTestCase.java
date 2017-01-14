package org.wso2.carbon.esb.mediationstats.test;
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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

public class MediationStatEnableTestCase extends ESBIntegrationTest {
	private final String ENDPOINT_NAME = "addressEpTest";

	private final String resourceFolderPath = getESBResourceLocation() + File.separator + "mediationStatConfig" +
	                                          File.separator;
	private EndPointAdminClient endPointAdminClient;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		ServerConfigurationManager serverConfigurationManager =
				new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		serverConfigurationManager.applyConfigurationWithoutRestart(new File(resourceFolderPath + "carbon.xml"));
		serverConfigurationManager.applyConfiguration(new File(resourceFolderPath + "synapse.properties"));
		super.init();
		loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File
				.separator + "mediationStatConfig" + File.separator + "synapse.xml");
		endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
	}

	@Test(groups = { "wso2.esb" }, description = "Test Mediation Statistics")
	public void testEnablingMediationStatistics()
			throws Exception {
		cleanupEndpoints();
		addEndpoint();
		OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint")
				, getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
		Assert.assertNotNull(response);
		Assert.assertTrue(response.toString().contains("WSO2 Company"));
		checkStaticsEnabled();
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		endPointAdminClient = null;
		super.cleanup();
	}

	/**
	 * cleans up existing endpoints
	 *
	 * @throws RemoteException
	 * @throws EndpointAdminEndpointAdminException
	 */
	private void cleanupEndpoints() throws RemoteException, EndpointAdminEndpointAdminException {
		String[] endpointNames = endPointAdminClient.getEndpointNames();
		List endpointList;
		if (endpointNames != null && endpointNames.length > 0 && endpointNames[0] != null) {
			endpointList = Arrays.asList(endpointNames);
			if (endpointList.contains(ENDPOINT_NAME)) {
				endPointAdminClient.deleteEndpoint(ENDPOINT_NAME);
			}
		}
	}

	/**
	 * Adds new endpoint
	 *
	 * @throws Exception
	 */
	private void addEndpoint() throws Exception {
		int beforeCount = endPointAdminClient.getEndpointCount();

		addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		                                 "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + ENDPOINT_NAME +
		                                 "\">\n" +
		                                 "    <address uri=\"" +
		                                 getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE) + "\" />\n" +
		                                 "</endpoint>"));

		int afterCount = endPointAdminClient.getEndpointCount();
		assertEquals(1, afterCount - beforeCount);

		//Verify whether endpoint is added
		String[] endpoints = endPointAdminClient.getEndpointNames();
		if (endpoints != null && endpoints.length > 0 && endpoints[0] != null) {
			List endpointList = Arrays.asList(endpoints);
			assertTrue(endpointList.contains(ENDPOINT_NAME));
		} else {
			fail("Endpoint has not been added to the system properly");
		}
	}

	/**
	 * checks whether the mediation statistic enabled
	 *
	 * @throws RemoteException
	 * @throws EndpointAdminEndpointAdminException
	 */
	private void checkStaticsEnabled() throws RemoteException, EndpointAdminEndpointAdminException {
		endPointAdminClient.enableEndpointStatistics(ENDPOINT_NAME);
		String endpoint = endPointAdminClient.getEndpointConfiguration(ENDPOINT_NAME);
		assertTrue(endpoint.contains("statistics=\"enable"));
	}

}
