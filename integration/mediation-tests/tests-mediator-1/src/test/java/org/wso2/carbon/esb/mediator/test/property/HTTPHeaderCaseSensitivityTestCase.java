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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class HTTPHeaderCaseSensitivityTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        String filePath = "/artifacts/ESB/mediatorconfig/property/synapse_http_header_case_sensitivity.xml";
        loadESBConfigurationFromClasspath(filePath);
    }

    /*Add a http header with name "TEST_HEADER" where all the letters are in uppercase.*/
    @Test(groups = "wso2.esb", description = " extracting http header value, when header name can " +
                                             "be uppercase"
    )
    public void testHttpHeaderUpperCase() throws AxisFault {
        StockQuoteClient axis2Client1 = new StockQuoteClient();
        OMElement response;
        axis2Client1.addHttpHeader("TEST_HEADER", "uppercase");
        response = axis2Client1.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("simpleProxy"), null,
                                                            "WSO2");
        assertNotNull(response, "Response message null");
        OMElement returnElement = response.getFirstElement();

        OMElement symbolElement = returnElement.getFirstChildWithName(
                new QName("http://services.samples/xsd", "symbol"));

        assertEquals(symbolElement.getText(), "uppercase", "Fault, invalid response");

    }

    /*Add a http header with name "test_header" where all the letters are in lower case.*/
    @Test(groups = "wso2.esb", description = " extracting http header value, when header name can " +
                                             "be  lowercase "
    )
    public void testHttpHeaderLowerCase() throws AxisFault {
        StockQuoteClient axis2Client2 = new StockQuoteClient();
        OMElement response;
        axis2Client2.addHttpHeader("test_header", "lowercase");
        response = axis2Client2.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("simpleProxy"), null,
                                                            "WSO2");
        assertNotNull(response, "Response message null");
        OMElement returnElement = response.getFirstElement();

        OMElement symbolElement = returnElement.getFirstChildWithName(
                new QName("http://services.samples/xsd", "symbol"));

        assertEquals(symbolElement.getText(), "lowercase", "Fault, invalid response");
    }

    /*Add a http header with name "test_header" where  the letters are in both upper & lower case.*/
    @Test(groups = "wso2.esb", description = " extracting http header value, when header name can " +
                                             "be a mix of lowercase or uppercase"
    )
    public void testHttpHeaderCombined() throws AxisFault {
        StockQuoteClient axis2Client3 = new StockQuoteClient();
        OMElement response;
        axis2Client3.addHttpHeader("Test_Header", "mixed");
        response = axis2Client3.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("simpleProxy"), null,
                                                            "WSO2");
        assertNotNull(response, "Response message null");
        OMElement returnElement = response.getFirstElement();

        OMElement symbolElement = returnElement.getFirstChildWithName(
                new QName("http://services.samples/xsd", "symbol"));

        assertEquals(symbolElement.getText(), "mixed", "Fault, invalid response");
    }


    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
