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
package org.wso2.carbon.esb.mediator.test.enrich;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.servlet.ServletException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
/*
Test: Define source configurain using xpath expression and use it to replace the body of current message
 */

public class EnrichIntegrationReplaceBodyUsingXpathTest extends ESBIntegrationTest {
    private OMElement response;

    /*
    Deploying Artifacts
     */

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception, ServletException, RemoteException {
        init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/synapseconfig/enrich_mediator/replaceBodyOfMessageSynapse.xml");

    }
/*
   Test: Define source configurain using xpath expression and use it to replace the body of current message
 */

    @Test(groups = "wso2.esb", description = "Tests-Replace the body of current message using xpath expression")
    public void testReplaceBodyUsingXpath() throws AxisFault, XMLStreamException {

        response=axis2Client.sendSimpleStockQuoteRequest
                (getProxyServiceURLHttp("enrichSample3"),null,createStandardRequest("IBM"));
        assertNotNull(response,"Response is null");
        System.out.println(response);
        assertEquals(response.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText(),
                     "IBM","Tag does not match");
    }

    /*
    Clean up
     */
    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
    /*
    Create payload
     */
    private OMElement createStandardRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "m");
        OMElement method = fac.createOMElement("getQuote", omNs);
        OMElement value1 = fac.createOMElement("testRequest", omNs);
        OMElement value2 = fac.createOMElement("testSymbol", omNs);

        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);

        return method;
    }
}
