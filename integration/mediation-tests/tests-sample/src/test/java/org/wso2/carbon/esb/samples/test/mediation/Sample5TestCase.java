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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import static org.testng.Assert.fail;

/**
 * Sample 5: Creating SOAP Fault Messages and Changing the Direction of a Message
 */
public class Sample5TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadSampleESBConfiguration(5);
    }

    @Test(groups = { "wso2.esb" },
          description = "Sample 5: Creating SOAP fault messages and changing" +
                        " the direction of a message")
    public void testSOAPFaultCreation() throws Exception {
        try {
            axis2Client.sendSimpleStockQuoteRequest(
                getMainSequenceURL(),
                getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                "MSFT");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            log.info("Fault Message : " + expected.getMessage());
            Assert.assertTrue((expected.getMessage().contains("Connection refused") ||
                               expected.getMessage().contains("Read timed out") ||
                               expected.getMessage().contains("Error connecting to the back end")),
                              "Error Message Mismatched. 'Connection refused or' not found. actual: " +
                              expected.getMessage()
            );
        }

        try {
            axis2Client.sendSimpleStockQuoteRequest(
                getMainSequenceURL(),
                getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                "SUN");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            log.info("Test passed with symbol SUN - Fault Message : " + expected.getMessage());
            Assert.assertTrue((expected.getMessage().contains("Connection refused") ||
                               expected.getMessage().contains("Read timed out") ||
                               expected.getMessage().contains("Error connecting to the back end")),
                              "Error Message Mismatched. 'Connection refused or' not found. actual: " +
                              expected.getMessage()
            );
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
