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
package org.wso2.carbon.esb.endpoint.test;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * SYNAPSE-820
 */
public class FailoverEndpointsForSingleLeafEndpointTestCase extends ESBIntegrationTest {


	@BeforeClass(alwaysRun = true)
	public void uploadSynapse() throws Exception {
        super.init();
		uploadSynapseConfig();
	}

	@Test(groups = { "wso2.esb" }, description = "Patch:SYNAPSE-820:Failover Endpoint does not work" +
                                                 " properly when using a single endpoint",
          expectedExceptions = AxisFault.class)
	public void testFailoverEndpoints() throws AxisFault {
		try {
			axis2Client.sendSimpleStockQuoteRequest("http://localhost:8280/services/test_failover",
			                                        "http://localhost:9001/services/SimpleStockQuoteService",
			                                        "IBM");

		} catch (AxisFault expected) {
			// TODO Check ESB WARN log for
			// "endpoint ...... for is marked as TIMEOUT and will be retried : 0 more time/s after ....."
			throw expected;
		}
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
		super.cleanup();
	}

	private void uploadSynapseConfig() throws Exception {
		loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/patch_automation/failover_endpoint_for_single_leaf_endpoint_synapse.xml");
	}

}
