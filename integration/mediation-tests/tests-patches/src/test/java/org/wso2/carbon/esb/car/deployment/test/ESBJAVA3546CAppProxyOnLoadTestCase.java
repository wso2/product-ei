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

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ESBJAVA3546CAppProxyOnLoadTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        String carFileStartOnLoadTrue = "esb-artifacts-car_1.0.0.car";
        String carFileStartOnLoadFalse = "inactive_proxy_1.0.0.car";

        super.init();
        uploadCapp(carFileStartOnLoadTrue
                , new DataHandler(new URL("file:" + File.separator + File.separator
                                          + getESBResourceLocation() + File.separator + "car" +
                                          File.separator + carFileStartOnLoadTrue)));
        log.info(carFileStartOnLoadTrue + " car file uploaded");

        uploadCapp(carFileStartOnLoadFalse
                , new DataHandler(new URL("file:" + File.separator + File.separator
                                          + getESBResourceLocation() + File.separator + "car" +
                                          File.separator + carFileStartOnLoadFalse)));
        log.info(carFileStartOnLoadFalse + " car file uploaded");

        //Sleep allowing artifacts to be deployed
        log.info("Waiting 60 seconds to deploy artifacts");
        TimeUnit.SECONDS.sleep(60);
    }

    @Test(groups = {"wso2.esb"}, description = "proxy service with startOnLoad=true deployed " +
                                               "from car file")
    public void startOnLoadTrueProxyTest() throws Exception {
        boolean trueResponseReceived = false;

        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    "http://127.0.0.1:8480/services/samplePassThroughProxy",
                    null,
                    "IBM");

            //Response received successfully - deployed service is active as expected
            trueResponseReceived = true;
        } catch (AxisFault axisFault) {
            log.error("Service Invocation Failed > " + axisFault.getMessage());
        } finally {
            Assert.assertTrue("startOnLoad=true proxy invocation test failed", trueResponseReceived);
        }
    }

    @Test(groups = {"wso2.esb"}, description = "proxy service with startOnLoad=false deployed " +
                                               "from car file but inactive")
    public void startOnLoadFalseProxyTest() throws Exception {
        boolean falseResponseReceived = false;

        try {
            axis2Client.sendSimpleStockQuoteRequest(
                    "http://127.0.0.1:8480/services/InactiveProxy",
                    null,
                    "IBM");
        } catch (AxisFault axisFault) {
            //Exception received with inactive service error - deployed service is inactive
            // as expected
            falseResponseReceived = true;
            log.info("Test Success Since Service Invocation Failed > " + axisFault.getMessage(),
                     axisFault);
        } finally {
            Assert.assertTrue("startOnLoad=false proxy invocation test failed", falseResponseReceived);
        }
    }

    @AfterTest(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
        super.cleanup();
    }
}
