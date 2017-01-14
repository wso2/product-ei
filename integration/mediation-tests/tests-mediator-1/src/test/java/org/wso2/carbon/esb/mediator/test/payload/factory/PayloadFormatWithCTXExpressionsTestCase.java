/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.esb.mediator.test.payload.factory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used to nativesupportforjson 'Native Support for JSON' scenarios using
 * Payload Format with argument mode
 */
public class PayloadFormatWithCTXExpressionsTestCase extends ESBIntegrationTest {

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		// applying changes to esb - source view
		loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/payloadmediatype/" +
				"value_ctxExpression.xml");
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}

	@Test(groups = "wso2.esb", description = "invoke service - operation echoString")
	public void invokeServiceFromXmlRequest() throws AxisFault {

		AxisServiceClient axisServiceClient = new AxisServiceClient();
		OMElement response = axisServiceClient.sendReceive(createPayload(),
				contextUrls.getServiceUrl() + "/ProxyPF", "echoString");
		assertTrue(response.toString().contains("Wso2 Test Automation"),"Response mismatch. " +
				"Actual Response "+response.toString());
	}

	private OMElement createPayload() {   // creation of payload for echoString

		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();

		OMNamespace omNs = fac.createOMNamespace("http://service.carbon.wso2.org", "ns");
		OMElement operation = fac.createOMElement("echoString", omNs);
		OMElement getName = fac.createOMElement("s", omNs);

		getName.addChild(fac.createOMText(getName, "name"));
		operation.addChild(getName);

		return operation;
	}
}
