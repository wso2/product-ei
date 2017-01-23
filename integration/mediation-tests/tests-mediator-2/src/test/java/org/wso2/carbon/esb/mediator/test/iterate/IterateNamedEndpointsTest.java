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

package org.wso2.carbon.esb.mediator.test.iterate;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;

import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Create a sequence with a iterate mediator that picks a named sequence that is
 * already defined in ESB
 *
 */
public class IterateNamedEndpointsTest extends ESBIntegrationTest {

	private SampleAxis2Server axis2Server1;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_named_endpoints.xml");
		axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");

		axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
		axis2Server1.start();
	}

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
	@Test(groups = "wso2.esb", description = "Tests for named endpoints")
	public void testNamedEndpoints() throws Exception {

        IterateClient client = new IterateClient();
		String response = client.getMultipleResponse(getMainSequenceURL(), "WSO2", 2);
		Assert.assertNotNull(response);
		OMElement envelope = client.toOMElement(response);
		OMElement soapBody = envelope.getFirstElement();
		Iterator iterator =
		                    soapBody.getChildrenWithName(new QName("http://services.samples",
		                                                           "getQuoteResponse"));
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			OMElement getQuote = (OMElement) iterator.next();
			Assert.assertTrue(getQuote.toString().contains("WSO2"));
		}
		Assert.assertEquals(i , 2, "Child Element count mismatched");
	}

	@AfterClass(alwaysRun = true)
	public void close() throws Exception {
		axis2Server1.stop();
        axis2Server1 = null;
		super.cleanup();
	}

}
