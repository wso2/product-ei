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

package org.wso2.carbon.esb.samples.test.mediation;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;

public class Sample600TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(600);
    }

    @Test(enabled = false,
          description = "Simple Message Transformation - Rule Mediator for Message Transformation- SUN changed to IBM")
    public void testSymbolSunChangedToIBM() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(), null, "SUN");
        Assert.assertNotNull(response, "Response is null");

        OMElement returnElement = response.getFirstElement();
        OMElement symbolElement = returnElement.getFirstChildWithName(
            new QName("http://services.samples/xsd", "symbol"));
        assertEquals(symbolElement.getText(), "IBM", "Fault, invalid response");
    }

    @Test(enabled = false,
          description = "Simple Message Transformation - Rule Mediator for Message Transformation - MSFT changed to IBM")
    public void testSymbolMsftChangedToIBM() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(), null, "MSFT");
        Assert.assertNotNull(response, "Response is null");

        OMElement returnElement = response.getFirstElement();
        OMElement symbolElement = returnElement.getFirstChildWithName(
            new QName("http://services.samples/xsd", "symbol"));
        assertEquals(symbolElement.getText(), "IBM", "Fault, invalid response");
    }

    @Test(enabled = false,
          description = "Simple Message Transformation - Rule Mediator for Message Transformation - WSO2 not changed to IBM")
    public void testSymbolWso2NotChanged() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(), null, "WSO2");
        Assert.assertNotNull(response, "Response is null");

        OMElement returnElement = response.getFirstElement();
        OMElement symbolElement = returnElement.getFirstChildWithName(
            new QName("http://services.samples/xsd", "symbol"));
        assertEquals(symbolElement.getText(), "WSO2", "Fault, invalid response");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
