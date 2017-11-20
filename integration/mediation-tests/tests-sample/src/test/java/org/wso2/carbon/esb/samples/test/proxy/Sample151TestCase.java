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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.samples.test.util.ESBSampleIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class Sample151TestCase extends ESBSampleIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(151);
    }

    @Test(groups = "wso2.esb", description = "Custom sequences and endpoints with proxy services")
    public void customSequencesAndEndpointsWithProxyServices() throws Exception {
        //invoking the StockQuoteProxy1
        OMElement responseStockQuoteProxy1 = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy1"), null, "WSO2");

        String lastPriceStockQuoteProxy1 = responseStockQuoteProxy1.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "last")).getText();
        assertNotNull(lastPriceStockQuoteProxy1, "Fault: response message 'last' price null");

        String symbol = responseStockQuoteProxy1.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

        //invoking the StockQuoteProxy2
        OMElement responseStockQuoteProxy2 = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy2"), null, "WSO2");

        String lastPriceStockQuoteProxy2 = responseStockQuoteProxy2.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "last")).getText();
        assertNotNull(lastPriceStockQuoteProxy2, "Fault: response message 'last' price null");

        String symbol2 = responseStockQuoteProxy2.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbol2, "WSO2", "Fault: value 'symbol' mismatched");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }


}
