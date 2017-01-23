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
package org.wso2.carbon.esb.mediator.test.switchMediator;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.xpath.XPathExpressionException;

public class SOAPNSBasedSwitching extends ESBIntegrationTest {

	@BeforeClass(alwaysRun = true)
	public void beforeClass() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/switchMediator/SOAP11_SOAP12_XPath.xml");
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
        super.cleanup();
	}

	@Test(groups = { "wso2.esb" }, description = "Switch Mediator:Write xpath expression using SOAP 1.1/1.2 NS Send SOAP 1.1/1.2 response and assert switch")
	public void testXPathOnDifferentSOAPNS() throws AxisFault, XPathExpressionException {
		OMElement response;

		response =
		           axis2Client.sendSimpleStockQuoteSoap11(getProxyServiceURLHttp("switchSoap11Sample"),
                                                          getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
		                                                  "IBM");
		Assert.assertTrue(response.toString().contains("IBM"), "Asserting for IBM in response");

		response =
		           axis2Client.sendSimpleStockQuoteSoap12(getProxyServiceURLHttp("switchSoap11Sample"),
                                                          getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
		                                                  "MSFT");

		Assert.assertTrue(response.toString().contains("MSFT"), "Asserting for MSTF in response");
	}

}
