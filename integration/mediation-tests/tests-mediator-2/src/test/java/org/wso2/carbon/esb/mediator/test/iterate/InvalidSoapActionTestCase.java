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

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import static org.testng.Assert.assertTrue;

/**
 * This will test the iterator Mediator, when an invalid "soapAction" in clone mediator has been
 * specified. Here if the first element attribute to check the given attribute name is available
 * as operation in WSDL if this gets satisfied then the invocation works properly,If payload first
 * element name and the the target soap action is different and none of them are matched in the WSDL
 * contract operations, then an soap fault message will be occur.
 */
public class InvalidSoapActionTestCase extends ESBIntegrationTest {
    private AxisServiceClient axisServiceClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        axisServiceClient = new AxisServiceClient();
    }

    /**
     * This is a negative test under iterator mediator.It will specify an invalid Soap Action and a payload first element
     * which does not match the WSDL contract operations in the clone target and mediate the message. The message would
     * not get mediated due to the inaccessibility of the endpoint reference.
     */

    @Test(groups = {"wso2.esb"}, description = "Tests an invalid 'soapAction' in the iterate mediator")
    public void testInvalidSoapActionInvalidPayloadFirstElement() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/invalid_soapaction.xml");

        OMElement response;
        try {
            response = axisServiceClient.sendReceive(createCustomQuoteRequest("WSO2"), getMainSequenceURL(), "");
            Assert.fail("This Request must throw AxisFault"); // This will execute when the exception is not thrown as expected
        } catch (AxisFault message) {
            assertTrue(message.getReason().contains("AxisFault while getting response"),
                       "Iterator mediator worked even with a invalid 'SoapAction' in the clone target");
        }

    }


    /**
     * It will specify an invalid Soap Action and a payload first element which matches the WSDL contract operations
     * in the clone target and mediate the message. The message would get mediated because If the payload body "first
     * tag element" which is identified as the operation to be invoked, and if at the iterator mediator target soapAction
     * specified with INCORRECT soap action, the stack will be working as ,first the axis2 engine looks the soapAction
     * specified method existing in the given wsdl contract, if not it will use the SOAP payload first element attribute
     * to check the given attribute name is available as operation in WSDL if this gets satisfied then the invocation
     * works properly . Therefore in this test case we get the mediated message.
     */
    @Test(groups = {"wso2.esb"}, description = "Tests an invalid 'soapAction' in the iterate mediator")
    public void testInvalidSoapActionValidPayloadFirstElement() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/invalid_soapaction_valid_payload.xml");

        OMElement response;
        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }


    private OMElement createCustomQuoteRequest(String symbol) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "ns");
        OMElement chkPrice = factory.createOMElement("CheckPriceRequest", ns);
        OMElement code = factory.createOMElement("Code", ns);
        chkPrice.addChild(code);
        code.setText(symbol);
        return chkPrice;
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        axisServiceClient = null;
        super.cleanup();
    }

}
