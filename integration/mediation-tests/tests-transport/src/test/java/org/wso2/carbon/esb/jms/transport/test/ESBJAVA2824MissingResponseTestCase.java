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

package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.apache.axis2.AxisFault;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;


/**
 * Fix for missing response / Axis Fault on read timeout in JMSSender.
 * public Jira - https://wso2.org/jira/browse/ESBJAVA-2824
 * 
 */
public class ESBJAVA2824MissingResponseTestCase extends ESBIntegrationTest {	

	private static final String logLine0 = "Did not receive a JMS response within 30000 ms to destination : queue://bip_reply";
		
	@BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/jms_wait_response.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
    }	
	
	
	@Test(groups = { "wso2.esb" }, description = "Test Sending message to a jms endpoint and check for response")
	public void testJmsResponse() throws Exception {
		try {
			axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
					null, "IBM");
			
		} catch (AxisFault fault) {
			String errMsg=fault.getMessage();						
			Assert.assertEquals(errMsg,"Send timeout", "JMS Client did not receive Send timeout");			
			
			Thread.sleep(10000);
			LogViewerClient cli = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
			LogEvent[] logs = cli.getAllSystemLogs();
			Assert.assertNotNull(logs, "No logs found");
			Assert.assertTrue(logs.length > 0, "No logs found");
			
			boolean errorMsgTrue=false;
			for (LogEvent logEvent : logs) {
				String msg = logEvent.getMessage();												
				if (msg.contains(logLine0) && logEvent.getPriority().equals("ERROR")) {					
					errorMsgTrue = true;	
					break;
				}				
			}
			Assert.assertTrue(errorMsgTrue, "Axis Fault Did not receive");
		}
	}

	@AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }	
}