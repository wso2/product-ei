/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.mediator.test.fault;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;

public class ForceErrorOnSoapFaultPropertyTest extends ESBIntegrationTest {
	@BeforeClass(alwaysRun = true) public void uploadSynapseConfig() throws Exception {
		super.init();
		loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fault/force_error_soap_fault.xml");
	}

	@Test(groups = {
			"wso2.esb" }, description = "Checking Force Error Property") public void testForceErrorOnSoapFault()
			throws AxisFault {
		try {
			axis2Client.send(getProxyServiceURLHttp("ForceErrorTestProxy"), null, "getQuote", createFaultRequest("IBM"));
		} catch (AxisFault expected) {
			assertEquals(expected.getFaultCode().getLocalPart(), "500000", "Fault code value mismatched");
		}
	}

	@AfterClass(alwaysRun = true) private void destroy() throws Exception {
		super.cleanup();
	}

	private OMElement createFaultRequest(String symbol) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
		OMElement method = fac.createOMElement("getQuote", omNs);
		OMElement value1 = fac.createOMElement("req", omNs);
		OMElement value2 = fac.createOMElement("symbol", omNs);

		value2.addChild(fac.createOMText(value1, symbol));
		value1.addChild(value2);
		method.addChild(value1);

		return method;
	}
}
