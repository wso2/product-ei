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
package org.wso2.carbon.esb.samples.test.proxy;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.fail;

/**
 * Sample 152: Switching Transports and Message Format from SOAP to REST POX
 */
public class Sample152TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(152);
    }

    @Test(groups = { "wso2.esb" },
          description = "Switching Transports and Message Format from SOAP to REST POX")
    public void testMessageSoapToRest() throws Exception {
        boolean assertException = false;
        try {
            axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy"), null,
                                             "WSO2");

            fail("This query must throw an exception.");
        } catch (AxisFault ex) {
            assertException = ex.getMessage().contains(
                "The service cannot be found for the endpoint reference (EPR) " +
                "/services/StockQuoteProxy"
            );
        }

        Assert.assertTrue(assertException, "Exception should be thrown");

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getProxyServiceURLHttps("StockQuoteProxy"), null, "WSO2");

        Assert.assertNotNull(response, "Response is null");
        Assert.assertTrue(response.toString().contains("getQuoteResponse"),
                          "Fault, invalid response");
        Assert.assertTrue(response.toString().contains("WSO2"), "Fault, invalid response");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
