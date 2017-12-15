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
package org.wso2.carbon.esb.samples.test.mediation.cache;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.esb.samples.test.util.ESBSampleIntegrationTest;

import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;

public class Sample420TestCase extends ESBSampleIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadSampleESBConfiguration(420);

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Creating simple cache sample 420 Test Case")
    public void testSimpleCachingNotExists() throws AxisFault, XPathExpressionException, InterruptedException {
        OMElement response;

        long currTime = System.currentTimeMillis();
        long timeDiff = 0;

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "", "IBM");
        String firstResponse = response.getFirstElement().toString();

        while (timeDiff < 20000) {

            response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "", "IBM");

            assertEquals(firstResponse, response.getFirstElement().toString(), "Caching is less than 20 seconds");

            /*The timeout I have set in the Sample is 20s. The test here within the while loop is to ensure the
             cache is not evicted before 20s. Hence a thread sleep is used.*/
            Thread.sleep(2000);
            timeDiff = System.currentTimeMillis() - currTime;
        }

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "", "IBM");

        assertNotEquals(firstResponse, response.getFirstElement().toString());
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Creating simple cache sample 420 Test Case")
    public void testSimpleCachingExists() throws AxisFault, XPathExpressionException, InterruptedException {
        OMElement response;

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "", "IBM");
        String firstResponse = response.getFirstElement().toString();

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), "", "IBM");

        assertEquals(firstResponse, response.getFirstElement().toString());
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
