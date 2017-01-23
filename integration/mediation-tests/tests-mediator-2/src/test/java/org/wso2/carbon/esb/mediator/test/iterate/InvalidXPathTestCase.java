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
package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertEquals;

/**
 * This class will test Iterator Mediator, when the specified 'attachPath' is invalid
 */
public class InvalidXPathTestCase extends ESBIntegrationTest {
    private String symbol;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception{
        super.init();
        symbol = FileUtils.readFileToString(new File(getESBResourceLocation() + "/mediatorconfig/iterate/iterate1.txt"));
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/invalid_XPath.xml");
    }

    /**
     * This is a negative test under iterator mediator.It will specify an invalid xpath as the 'attachPath'
     * and verify error handling.
     */

    @Test(groups = {"wso2.esb"}, description = "Tests an invalid 'attachPath' expression in Iterator Mediator")
        public void testInvalidXPath() throws Exception {


        OMElement response;
        try {
            response =axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), null, symbol,5);
            Assert.fail("This Request must throw AxisFault"); // This will execute when the exception is not thrown as expected
        } catch (AxisFault message) {
            assertEquals(message.getReason(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL,
                    "Iterator mediator worked even with a invalid XPath expression for 'attachPath'");
        }

    }

    @AfterClass(groups = "wso2.esb", alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
        symbol = null;
    }
}
