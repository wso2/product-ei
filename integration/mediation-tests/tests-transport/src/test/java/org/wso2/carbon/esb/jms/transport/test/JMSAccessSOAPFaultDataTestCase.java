/*
 *Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.jms.transport.test;

import java.rmi.RemoteException;

import junit.framework.Assert;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

/**
 * This test case is written to track the issue reported in
 * soap fault when doing a JMS transport
 * */
public class JMSAccessSOAPFaultDataTestCase extends ESBIntegrationTest {
	private LogViewerClient logViewerClient = null;
	private SampleAxis2Server axisServer = null;

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@BeforeClass(alwaysRun = true)
	protected void init() throws Exception {
		super.init();

		axisServer = new SampleAxis2Server("test_axis2_server_9017.xml");
		axisServer.start();
		axisServer.deployService(ESBTestConstant.ECHO_SERVICE);

		OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/JMSAXISFault.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
		logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

	}

	@Test(groups = { "wso2.esb" }, description = "Test whether the fault data can be retrieved by properties such as ERROR_CODE,ERROR_MESSAGE,ERROR_DETAIL  when soap fault received from backend" ,
			   enabled = false
	)
	public void readSOAPFaultDetails() {
		int beforeLogCount = 0;
		try {
			beforeLogCount = logViewerClient.getAllSystemLogs().length;
			AxisServiceClient client = new AxisServiceClient();

			client.fireAndForget(Utils.getIncorrectRequest("Invalid"),
					getProxyServiceURLHttp("FaultTestProxy"), "echoInt");
		} catch (Exception e) {
		} finally {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			String allLogs = "";
			LogEvent[] logs;
			try {
				logs = logViewerClient.getAllSystemLogs();

				for (int i = 0; i < logs.length - beforeLogCount; i++) {
					allLogs += logs[i].getMessage();
				}
			} catch (RemoteException e) {
			}
			Assert.assertTrue(allLogs
					.contains("ERROR_MESSAGE = Invalid value \"Invalid\" for element in"));
			Assert.assertTrue(allLogs.contains("ERROR_CODE = Client"));

		}

	}

	@AfterClass(alwaysRun = true)
	protected void cleanup() throws Exception {
		super.cleanup();

	}
}
