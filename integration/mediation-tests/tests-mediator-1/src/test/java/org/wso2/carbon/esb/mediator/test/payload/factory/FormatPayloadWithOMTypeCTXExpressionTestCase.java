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

package org.wso2.carbon.esb.mediator.test.payload.factory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertFalse;

public class FormatPayloadWithOMTypeCTXExpressionTestCase extends ESBIntegrationTest {
	@BeforeClass(alwaysRun = true)
	public void uploadSynapseConfig() throws Exception {
		super.init();
		loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/payload/factory/om_ctx_payload_factory_synapse.xml");
	}


	@Test(groups = {"wso2.esb"}, description = "Do transformation with a Payload Format that has OM type properties")
	public void transformPayloadByCTXPropertyValue() throws AxisFault {
		sendRobust(getMainSequenceURL(), "IBM");
	}


	private void sendRobust(String trpUrl, String symbol)
			throws AxisFault {
		ServiceClient sender;
		Options options;

		sender = new ServiceClient();
		options = new Options();
		options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
		options.setAction("urn:placeOrder");

		if (trpUrl != null && !"null".equals(trpUrl)) {
			options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
		}

		sender.setOptions(options);

		OMElement response = sender.sendReceive(createRequest(symbol));
		assertFalse(response.toString().contains("&lt;"), "Transformed message contains &lt; instead of <");
	}

	private OMElement createRequest(String symbol) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
		OMElement method = fac.createOMElement("payload", omNs);
		OMElement value1 = fac.createOMElement("request", omNs);
		OMElement value2 = fac.createOMElement("symbol", omNs);

		value2.addChild(fac.createOMText(value1, symbol));
		value1.addChild(value2);
		method.addChild(value1);

		return method;
	}

	@AfterClass(alwaysRun = true)
	private void destroy() throws Exception {
		super.cleanup();
	}
}
