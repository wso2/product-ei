/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.endpoint.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;
import org.wso2.esb.integration.common.utils.clients.LoadbalanceFailoverClient;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ESBJAVA4231EmptyPayloadInFailoverLoadBalanceEndpoint extends ESBIntegrationTest {

	private SampleAxis2Server axis2Server1;
	private SampleAxis2Server axis2Server2;
	private LoadbalanceFailoverClient lbClient;

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init();
		loadESBConfigurationFromClasspath(
				File.separator + "artifacts" + File.separator + "ESB" + File.separator + "endpoint" + File.separator +
				"EmptyPayloadInFailoverLoadBalanceEndpoint" + File.separator + "synapse.xml");
		startAxis2Server();
		TimeUnit.SECONDS.sleep(5);
		lbClient = new LoadbalanceFailoverClient();
	}



	@Test(groups = "wso2.esb", description = "Test sending request to LoadBalancing Endpoint")
	public void testSendingToLoaBalancingEndpoint()	throws IOException, EndpointAdminEndpointAdminException,
	                                                          LoginAuthenticationExceptionException, XMLStreamException {
		String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadbalanceproxy"), null);
		Assert.assertNotNull(response);
		Assert.assertTrue(response.toString().contains("Response from server: Server_2"));
	}

	@AfterClass(alwaysRun = true)
	public void cleanUp() throws Exception {
		axis2Server1.stop();
		axis2Server2.stop();
		axis2Server1 = null;
		axis2Server2 = null;
		lbClient = null;
		super.cleanup();
	}

	private void startAxis2Server() throws IOException {
		axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
		axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");
		axis2Server1.deployService("LBServiceWithSleep");
		axis2Server2.deployService(SampleAxis2Server.LB_SERVICE_2);
		axis2Server1.start();
		axis2Server2.start();
	}
}