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

package org.wso2.carbon.esb.mediator.test.clone;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

/**
 * 
 * Tests SOAP Action property of the clone mediator
 *
 */
public class CloneSOAPActionTestCase extends ESBIntegrationTest {

	private SampleAxis2Server axis2Server1;
	private SampleAxis2Server axis2Server2;
	private AxisServiceClient axisServiceClient;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		init();
		axisServiceClient = new AxisServiceClient();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/clone/clone_SOAP_Action.xml");
		axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
		axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");

		axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
		axis2Server1.start();
		axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
		axis2Server2.start();
	}

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
	@Test(groups = "wso2.esb", description = "Tests SOAP Action")
	public void testSOAPAction() throws Exception {

		OMElement response =
		                     axisServiceClient.sendReceive(createSimpleQuoteRequestBody("WSO2"),
		                                                   getMainSequenceURL(), "");
		Assert.assertTrue(response.toString().contains("WSO2"));
	}

	@AfterClass(alwaysRun = true)
	public void close() throws Exception {
		axis2Server1.stop();
		axis2Server2.stop();
        axis2Server2 = null;
        axis2Server1 = null;
		super.cleanup();
	}

	private OMElement createSimpleQuoteRequestBody(String symbol) {
		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
		OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
		OMElement method = fac.createOMElement("getQuote", omNs);
		OMElement value1 = fac.createOMElement("request", omNs);
		OMElement value2 = fac.createOMElement("symbol", omNs);
		value2.addChild(fac.createOMText(value1, symbol));
		value1.addChild(value2);
		method.addChild(value1);
		return method;
	}

}
