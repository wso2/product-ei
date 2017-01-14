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
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class Sample420TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
       // loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/cache/synapse_sample_420.xml");
        loadSampleESBConfiguration(420);

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Creating simple cache sample 420 Test Case", enabled = false)
    public void testSimpleCashing() throws AxisFault, XPathExpressionException, InterruptedException {
        OMElement response;

            long currTime = System.currentTimeMillis();
            long timeDiff = 0;

            response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL() , "", "IBM");
            String firstResponse = response.getFirstElement().toString();

            while (timeDiff < 20000) {

                response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL() , "", "IBM");

                if ( ! firstResponse.equalsIgnoreCase( response.getFirstElement().toString() ))
                    assertFalse(false, "Caching is less than 20 seconds");

                Thread.sleep(2000);
                timeDiff = System.currentTimeMillis() - currTime;
            }

            response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL() , "", "IBM");

            if ( ! firstResponse.equalsIgnoreCase( response.getFirstElement().toString() ))
                assertTrue(true, "Caching is more than 20 seconds");


    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }
}
