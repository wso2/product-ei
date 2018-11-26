/*
*Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertTrue;

/**
 * This tests that the Payload factory mediator works as expected when an XML payload with an array is processed.
 */
public class TransformPayloadXmlArrayTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
    }

    /**
     * This tests the issue of getting &lt; and &gt; instead of < and > when parsing an XML payload with an array inside
     * the payload factory.
     */
    @Test(groups = {"wso2.esb"}, description = "Transformation a payload that has an XML array")
    public void transformPayloadWithXmlArray() throws AxisFault, XPathExpressionException, XMLStreamException {
        OMElement response;

        response = axis2Client.send(getProxyServiceURLHttp("payloadFactoryXmlArrayTestProxy"),
                                    null, "mediate", getRequest());
        assertTrue(response.toString().contains(
                "<root xmlns=\"http://ws.apache.org/ns/synapse\"><header><code>1000</code></header><header><code"
                + ">1000</code></header></root>"), "Payload not correctly formatted. Received payload: " + response);
    }

    private OMElement getRequest() throws XMLStreamException {
        OMFactory fac = OMAbstractFactory.getOMFactory();

        OMElement omRoot = fac.createOMElement("root", null);
        omRoot.addChild(createHeaderElement());
        omRoot.addChild(createHeaderElement());
        return omRoot;

    }

    private OMElement createHeaderElement(){
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement omHeader = fac.createOMElement("header", null);
        omHeader.addChild(createCodeElement("1000"));
        return omHeader;
    }

    private OMElement createCodeElement(String value){
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement omCode = fac.createOMElement("code", null);
        omCode.setText(value);
        return omCode;
    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }


}
