/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.proxyservice.test.customProxy;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * 
 * Tests ESBs stability against malformed http requests
 * Tests by referring correct urls mixed with malformed urls
 * public Jira- https://wso2.org/jira/browse/ESBJAVA-620
 * 
 */

public class MalformedHTTPRequestTest extends ESBIntegrationTest {

	private HttpURLConnectionClient httpClientUtil;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		init();
		httpClientUtil = new HttpURLConnectionClient();
	}

	@Test(groups = "wso2.esb", description = "test with different kinds of malformed urls")
	public void testMalformedURL() throws Exception {
		loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/customProxy/simple_proxy.xml");
        //Send three malformed requests
		for (int i = 0; i < 3; i++) {
			try {
				//tests a url with a space
				httpClientUtil.sendGetRequest(getProxyServiceURLHttp("simpleProxy").replace("services", " services")+"?WSO2", null);
			} catch (Exception e) {
			}
		}
        //check whether ESB is still stable by sending a correct request
		OMElement response =
		                     axis2Client.sendSimpleQuoteRequest(getProxyServiceURLHttp("simpleProxy"), null,
		                                                   "WSO2");
		Assert.assertTrue(response.toString().contains("WSO2"));
        //Send three malformed requests
		for (int i = 0; i < 3; i++) {
			try {
				//tests a url with double slash
				httpClientUtil.sendGetRequest(getProxyServiceURLHttp("simpleProxy").replace("services/", "services//") + "?WSO2", null);
			} catch (Exception e) {
			}
		}
        //check whether ESB is still stable by sending a correct request
		OMElement response2 =
		                      axis2Client.sendSimpleQuoteRequest(getProxyServiceURLHttp("simpleProxy"),
		                                                    null, "WSO2");
		Assert.assertTrue(response2.toString().contains("WSO2"));
        //Send three malformed requests
		for (int i = 0; i < 3; i++) {
			try {
				//tests a url with invalid characters
				httpClientUtil.sendGetRequest(getMainSequenceURL() + "services/$$simpleProxy?WSO2", null);
			} catch (Exception e) {
			}
		}
        //check whether ESB is still stable by sending a correct request
		OMElement response3 =
		                      axis2Client.sendSimpleQuoteRequest(getProxyServiceURLHttp("simpleProxy"),
		                                                    null, "WSO2");
		Assert.assertTrue(response3.toString().contains("WSO2"));
	}

	@AfterClass(alwaysRun = true)
	public void close() throws Exception {
        httpClientUtil=null;
		super.cleanup();
	}

}
