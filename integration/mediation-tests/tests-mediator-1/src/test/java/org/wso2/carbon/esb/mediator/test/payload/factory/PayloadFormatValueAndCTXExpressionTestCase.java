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
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * This class can be used to nativesupportforjson 'Native Support for JSON' scenarios using
 * Payload format value and expression both
 */
public class PayloadFormatValueAndCTXExpressionTestCase extends ESBIntegrationTest {

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		// applying changes to esb - source view
		loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/payloadmediatype/" +
				"ctxExpression.xml");
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}

	@Test(groups = "wso2.esb", description = "invoke service - operation placeOrder")
	public void invokeServiceFromXmlRequest() throws AxisFault {

		ServiceClient sender;
		Options options;

		sender = new ServiceClient();
		options = new Options();
		options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
		options.setAction("urn:placeOrder");

		options.setProperty(Constants.Configuration.TRANSPORT_URL, getProxyServiceURLHttps("ProxyPF"));

		sender.setOptions(options);

		sender.sendRobust(createPayload());
	}

	private OMElement createPayload() {   // creation of payload for placeOrder

		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();

		OMNamespace omXsdNs = fac.createOMNamespace("http://services.samples", "xsd");
		OMNamespace omSerNs = fac.createOMNamespace("http://services.samples", "ser");

		OMElement operation = fac.createOMElement("placeOrder", omSerNs);
		OMElement method = fac.createOMElement("order", omSerNs);

		OMElement getPrice = fac.createOMElement("price", omXsdNs);
		OMElement getQuantity = fac.createOMElement("quantity", omXsdNs);
		OMElement getSymbol = fac.createOMElement("symbol", omXsdNs);

		method.addChild(fac.createOMText(getPrice, "123.32"));
		method.addChild(fac.createOMText(getQuantity, "4"));
		method.addChild(fac.createOMText(getSymbol, "IBM"));

		operation.addChild(method);

		return operation;
	}
}
