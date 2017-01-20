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

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Sample 7: Using Schema Validation and the Usage of Local Registry for Storing Configuration Metadata
 */
public class Sample7TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(7);
    }

    @Test(groups = { "wso2.esb" },
          description = "Add a validate mediator, which validates the first element of the SOAP " +
                        "body of incoming message using the schema")
    public void testValidationSchema() throws Exception {

        try {
            axis2Client.sendSimpleStockQuoteRequest(
                getMainSequenceURL(),
                getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                "WSO2");
            fail("Request Should throw AxisFault");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request",
                         "Fault: value mismatched, should be 'Invalid custom quote request'");
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
