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

/**
 * This test case tests the iterate mediator, when the iterate ID and aggregate ID mismatch.
 */
public class IterateIDAggregateIDMismatchTestCase extends ESBIntegrationTest {
    private String symbol;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        symbol = FileUtils.readFileToString(new File(getESBResourceLocation() + "/mediatorconfig/iterate/iterate1.txt"));
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/IterateIDAggregateIDMismatch.xml");
    }

    /**
     * This is a negative test under iterator mediator.Defining an  id as the iterate ID that does not
     * match with the aggregate ID and verify aggregation does not happen
     */
    @Test(groups = {"wso2.esb"}, description = "Tests iterator ID that does not match with aggregate ID")
    public void testIterateIDAggregateIDMismatch() throws Exception {

        try {
            axis2Client.sendMultipleQuoteRequest(getMainSequenceURL(), null, symbol, 8);
            Assert.fail("This Request must throw AxisFault"); // This will execute when the exception is not thrown as expected
        } catch (AxisFault message) {
            Assert.assertEquals(message.getReason(), ESBTestConstant.READ_TIME_OUT, "Expected Reason not found");
        }
    }

    @AfterClass(groups = "wso2.esb", alwaysRun = true)
    public void close() throws Exception {
        symbol = null;
        super.cleanup();
    }

}
