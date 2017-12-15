/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediator.test.cache;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Cache Mediator - verify 'headersToExcludeInHash' parameter
 * <p>
 * This testcase is to verify that a defined header can be exclude when hashing
 */
public class ExcludeHeadersWithCacheTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.esb",
          description = "Verify a header can be excluded when hasing")
    public void testExcludingHeaders() throws AxisFault {
        OMElement stockResponse;

        // Sending a request to SimpleStockQuote Service
        stockResponse = axis2Client
                .sendSimpleStockQuoteRequest(getApiInvocationURL("excludeHeaders"), "", "ExcludeHeaders");
        assertNotNull(stockResponse, "Response is null");
        String change = stockResponse.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "change")).getText();

        // Sending a request to SimpleStockQuote Service with 'user' header
        axis2Client.addHttpHeader("user", "Peter");
        stockResponse = axis2Client
                .sendSimpleStockQuoteRequest(getApiInvocationURL("excludeHeaders"), "", "ExcludeHeaders");
        assertNotNull(stockResponse, "Response is null");
        String change1 = stockResponse.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "change")).getText();

        assertEquals(change, change1, "Response caching did not exclude the given header value");

        // Sending a request to SimpleStockQuote Service with 'user' and 'country' headers
        axis2Client.addHttpHeader("user", "Peter");
        axis2Client.addHttpHeader("country", "UK");
        stockResponse = axis2Client
                .sendSimpleStockQuoteRequest(getApiInvocationURL("excludeHeaders"), "", "ExcludeHeaders");
        assertNotNull(stockResponse, "Response is null");
        String change2 = stockResponse.getFirstElement()
                .getFirstChildWithName(new QName("http://services.samples/xsd", "change")).getText();

        assertNotEquals(change, change2, "Response caching works with the undefined exclude header");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

}
