/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.message.processor.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.tcpmon.client.TCPMonListener;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.rmi.RemoteException;

/**
 * https://wso2.org/jira/browse/ESBJAVA-3650
 * Test whether HTTP headers are not preserved when the message goes through Message store > Message processor
 */
public class ESBJAVA3650_CustomHeaderPreserved_MessageProcessorOutMessage_TestCase extends ESBIntegrationTest {

	private static final String PROXY_SERVICE_NAME = "testProxy";
	private TCPMonListener tcpMonListener;

	@BeforeClass(alwaysRun = true)
	public void deployeService() throws Exception {
		super.init();
		loadESBConfigurationFromClasspath(
				"/artifacts/ESB/messageProcessorConfig/CustomHeaderPreserved_MessageProcessorOutMessage.xml");
		isProxyDeployed(PROXY_SERVICE_NAME);
		tcpMonListener = new TCPMonListener(8999, "localhost", 9000);
		tcpMonListener.start();
	}

	@Test(groups = { "wso2.esb" },
	      description = "Test whether HTTP headers are not preserved when the message goes through Message store > Message processor")
	public void testCustomHeaderPreserved_MessageProcessorOutMessage()
			throws RemoteException,
			       InterruptedException {

		axis2Client.sendPlaceOrderRequest(getProxyServiceURLHttp(PROXY_SERVICE_NAME), null, "IBM");
		Thread.sleep(5000);

		String inputText = tcpMonListener.getConnectionData().get(1).getInputText().toString();
		Assert.assertTrue(inputText.contains("customHeader: customHeadervalue"),
		                  "customHeader:customHeaderValue does not exist in out message of ESB");
	}

	@AfterClass(alwaysRun = true)
	public void UndeployeService() throws Exception {
		tcpMonListener.stop();
		super.cleanup();
	}
}
