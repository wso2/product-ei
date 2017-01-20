/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.esb.template;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.tests.CarbonTestServerManager;
import org.wso2.carbon.integration.common.utils.ClientConnectionUtil;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class HttpEpTemplateWithSystemPropsTestCase {

	private AutomationContext context;

	private HashMap<String, String> startupParameterMap = new HashMap<String, String>();
	private final static String HOST = "localhost";
	private final static int OFFSET = 10;
	private final static int SERVER_PORT = 9443 + OFFSET;
	private final static String BACKEND_URL = "https://localhost:" + SERVER_PORT + "/services/";
	private final static String API_URL = "http://localhost:" + (8280 + OFFSET)
			+ "/HttpTemplateTestAPI/SimpleStockQuoteService";
	ESBTestCaseUtils esbUtils;
	CarbonTestServerManager server2;
	
	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@BeforeClass(groups = { "wso2.esb" })
	public void init() throws Exception {
		context = new AutomationContext();
		esbUtils = new ESBTestCaseUtils();
		startupParameterMap.put("-DportOffset", String.valueOf(OFFSET));
		startupParameterMap.put("-Dhname", HOST);
		startupParameterMap.put("-Dhport", "9000");
		server2 = new CarbonTestServerManager(context, System.getProperty("carbon.zip"),
				startupParameterMap);
		server2.startServer();
		deploySynapseConfig();
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.ALL })
	@Test(groups = { "wso2.esb" } , enabled = true)
	public void testHttpEpTemplateWithSystemProps() throws AxisFault {
		StockQuoteClient axis2Client = new StockQuoteClient();
		OMElement response = axis2Client.sendSimpleStockQuoteRequestREST(API_URL, null, "WSO2");
		// (API_URL, null, "WSO2");
		Assert.assertNotNull(response);
		Assert.assertTrue(response.toString().contains("WSO2 Company"));
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.ALL })
	@AfterClass
	public void clean() throws Exception {
		server2.stopServer();
	}

	private void deploySynapseConfig() throws Exception {
		AuthenticatorClient authenticatorClient = new AuthenticatorClient(BACKEND_URL);
		ClientConnectionUtil.waitForPort(SERVER_PORT, HOST);
		ClientConnectionUtil.waitForLogin(context);// (SERVER_PORT, HOST,
		// BACKEND_URL);
        TimeUnit.SECONDS.sleep(15);
		String sessionCookie = authenticatorClient.login("admin", "admin",
				new URL(BACKEND_URL).getHost());

		String relativeFilePath = File.separator + "artifacts" + File.separator + "ESB"
				+ File.separator + "template" + File.separator + "httpEpTemplateTest.xml";
		relativeFilePath = relativeFilePath.replaceAll("[\\\\/]", File.separator);
		OMElement synapseConfig = esbUtils.loadResource(relativeFilePath);
		esbUtils.updateESBConfiguration(synapseConfig, BACKEND_URL, sessionCookie);
	}

}

