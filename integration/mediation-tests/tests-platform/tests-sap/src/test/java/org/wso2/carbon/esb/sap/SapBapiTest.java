/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.sap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.sap.utils.Util;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SapBapiTest extends ESBIntegrationTest {

    public static final String MEDIA_TYPE_TEXT_XML = "text/xml";
    public static final int HTTP_SC_OK = 200;

    @BeforeClass()
    public void init() throws Exception {
        super.init();

    }

    /**
     * Sends a BAPI request to a SAP system and asserts that a response of code '200' and with a successful return
     * type is received.
     *
     * @throws Exception if an error occurs while sending the BAPI request
     */
    @Test(groups = {"wso2.esb"}, description = "Test ESB as an BAPI Sender")
    public void testSendBapiRequest() throws Exception {
        SimpleHttpClient soapClient = new SimpleHttpClient();
        String payload = "<bapirfc name=\"BAPI_SALESORDER_GETLIST\">\n"
                         + "   \t<import>\n"
                         + "\t\t<field name=\"CUSTOMER_NUMBER\">PCS-C301</field>\n"
                         + "\t\t<field name=\"SALES_ORGANIZATION\">10000</field>\n"
                         + "\t</import>\n"
                         + "</bapirfc>\n";
        HttpResponse response = soapClient.doPost(getProxyServiceURLHttp("sapBapiProxy"),
                                                  null, payload, MEDIA_TYPE_TEXT_XML);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HTTP_SC_OK, "incorrect response code received");
        String responseString = Util.getResponsePayload(response);
        OMElement responseElement = AXIOMUtil.stringToOM(responseString);
        assertNotNull(responseElement);
        OMElement outputElement = responseElement.getFirstChildWithName(new QName("OUTPUT"));
        assertNotNull(outputElement);
        OMElement returnElement = outputElement.getFirstChildWithName(new QName("RETURN"));
        assertNotNull(returnElement);
        OMElement returnTypeElement = returnElement.getFirstChildWithName(new QName("TYPE"));
        String returnTypeText = returnTypeElement.getText();
        assertEquals(returnTypeText, "", "Incorrect response received. Received response: " + responseString);

    }

    @AfterClass(alwaysRun = true)
    public void end() throws Exception {
        super.cleanup();
    }
}
