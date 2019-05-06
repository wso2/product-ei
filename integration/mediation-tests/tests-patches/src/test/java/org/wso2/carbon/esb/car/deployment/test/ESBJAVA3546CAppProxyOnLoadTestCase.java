/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.esb.car.deployment.test;

import junit.framework.Assert;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA3546CAppProxyOnLoadTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        super.init();
    }

    @Test(groups = {"wso2.esb"}, description = "proxy service with startOnLoad=true deployed from car file")
    public void startOnLoadTrueProxyTest() throws Exception {
        boolean trueResponseReceived = false;

        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    "http://127.0.0.1:8480/services/samplePassThroughProxy", null, "IBM");

            trueResponseReceived = true;
        } catch (AxisFault axisFault) {
            log.error("Service Invocation Failed > " + axisFault.getMessage());
        } finally {
            Assert.assertTrue("startOnLoad=true proxy invocation test failed", trueResponseReceived);
        }
    }

    @Test(groups = {"wso2.esb"},
          description = "proxy service with startOnLoad=false deployed from car file but inactive",
          expectedExceptions = AxisFault.class)
    public void startOnLoadFalseProxyTest() throws Exception {
            axis2Client.sendSimpleStockQuoteRequest("http://127.0.0.1:8480/services/InactiveProxy",
                                                    null, "IBM");
    }

    @AfterTest(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
        super.cleanup();
    }
}
