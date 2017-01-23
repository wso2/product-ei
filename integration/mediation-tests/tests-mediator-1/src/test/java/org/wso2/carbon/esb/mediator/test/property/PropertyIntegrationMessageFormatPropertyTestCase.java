/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import static org.testng.Assert.assertEquals;

/**
 * This class tests the functionality of the MESSAGE_FORMAT property
 */

public class PropertyIntegrationMessageFormatPropertyTestCase extends ESBIntegrationTest {

    private HttpClientUtil clientUtil;
    private String proxyName = "MyProxy";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/MESSAGE_FORMAT.xml");
        clientUtil = new HttpClientUtil();
    }


    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Testing with POX message format")
    public void testMessageFormatPOXTest() throws Exception {

        for (int x = 0; x < 4; x++) {
            OMElement response = clientUtil.get(getProxyServiceURLHttp(proxyName));
            assertEquals(response.toString(),"<messageformat>pox</messageformat>",
                         "Response should be in pox message format but received " +
                         response.getText());
        }

    }

    @Test(groups = "wso2.esb", description = "Testing with SOAP 1.1 message format",
          dependsOnMethods = "testMessageFormatPOXTest")
    public void testMessageFormatSOAP11Test() throws Exception {

        AxisServiceClient axisServiceClient = new AxisServiceClient();

        for (int x = 0; x < 4; x++) {
            OMElement response = axisServiceClient.sendReceive(
                    createPayLoad(), getProxyServiceURLHttp(proxyName), "");

            assertEquals(response.toString(),"<messageformat>soap11</messageformat>",
                         "Response should be in SOAP 1.1 message format but received "
                         + response.getText());
        }
    }

    private static OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope/", "ns");
        OMElement getOme = fac.createOMElement("echoInt", omNs);
        OMElement getOmeTwo = fac.createOMElement("x", omNs);
        getOmeTwo.setText("25");
        getOme.addChild(getOmeTwo);
        return getOme;
    }
}
