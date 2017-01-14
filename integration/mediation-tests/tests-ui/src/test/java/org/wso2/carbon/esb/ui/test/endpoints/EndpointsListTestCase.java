/*
 * Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.ui.test.endpoints;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.esb.ui.test.ESBIntegrationUITest;
import org.wso2.carbon.automation.api.selenium.endpoint.EndpointsListPage;

public class EndpointsListTestCase extends ESBIntegrationUITest {

	private WebDriver driver;
	private ResourceAdminServiceClient resourceAdminServiceClient;
	private final String ENDPOINT_PATH = "_system/governance/endpoints";

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		super.init();
		driver = BrowserManager.getWebDriver();
		driver.get(getLoginURL(ProductConstant.ESB_SERVER_NAME));

		// add resource using resourceAdminServiceClient
		resourceAdminServiceClient = new ResourceAdminServiceClient(
				esbServer.getBackEndUrl(), userInfo.getUserName(),
				userInfo.getPassword());
		uploadResourcesToGovRegistry();
	}

	@Test(groups = "wso2.esb", description = "Verify that endpoints page renders when an invalid dynamic endpoint is present.")
	public void testEndpointsList() throws Exception {
		boolean isCloud = isRunningOnCloud();
		LoginPage test = new LoginPage(driver, isCloud);
		test.loginAs(userInfo.getUserName(), userInfo.getPassword());

		EndpointsListPage epPage = new EndpointsListPage(driver);
		epPage.testPageLoadFail();
		driver.close();
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {

		driver.quit();
		resourceAdminServiceClient.deleteResource(ENDPOINT_PATH
				+ "/testEP1.xml");
		resourceAdminServiceClient.deleteResource(ENDPOINT_PATH
				+ "/testEP2.xml");
		resourceAdminServiceClient.deleteResource(ENDPOINT_PATH
				+ "/testEP3.xml");
		resourceAdminServiceClient = null;
		super.cleanup();

	}

	private void uploadResourcesToGovRegistry() throws Exception {

		String validEndpointXml = "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\">\n"
				+ "   <default >\n"
				+ "      <suspendOnFailure>\n"
				+ "         <progressionFactor>1.0</progressionFactor>\n"
				+ "      </suspendOnFailure>\n"
				+ "      <markForSuspension>\n"
				+ "         <retriesBeforeSuspension>0</retriesBeforeSuspension>\n"
				+ "         <retryDelay>0</retryDelay>\n"
				+ "      </markForSuspension>\n"
				+ "   </default>\n"
				+ "</endpoint>";

		String invalidEndpointXml1 = "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\">\n"
				+ "   <default >\n"
				+ "      <suspendOnFailure>\n"
				+ "         <progressionFactor>1.0</progressionFactor>\n"
				+ "      </suspendOnFailure>\n"
				+ "      <markForSuspension>\n"
				+ "         <retriesBeforeSuspension>0</retriesBeforeSuspension>\n"
				+ "         <retryDelay>0</delay>\n"
				+ "      </markForSuspension>\n"
				+ "   </default>\n"
				+ "</endpoint>"; // Note that configuration is incorrect at
									// </delay>

		String invalidEndpointXml2 = "http://localhost:9000/services/SimpleStockQuoteService";

		resourceAdminServiceClient.addTextResource(ENDPOINT_PATH,
				"testEP1.xml", "application/vnd.wso2.esb.endpoint",
				"Valid endpoint", validEndpointXml);
		resourceAdminServiceClient.addTextResource(ENDPOINT_PATH,
				"testEP2.xml", "application/vnd.wso2.esb.endpoint",
				"Invalid endpoint 1", invalidEndpointXml1);
		resourceAdminServiceClient.addTextResource(ENDPOINT_PATH,
				"testEP3.xml", "application/vnd.wso2.esb.endpoint",
				"Invalid endpoint 2", invalidEndpointXml2);

		Thread.sleep(1000);
	}
}
