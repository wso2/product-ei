/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediators.rule;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ESBJAVA2506RuleFetchFromRegistryFailsForTheFirstTime extends ESBIntegrationTest {

    String carFileName = "esb-artifacts-rule-mediator-car_1.0.0.car";

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        super.init();
        uploadCapp(carFileName
                , new DataHandler(new URL("file:" + File.separator + File.separator
                + getESBResourceLocation() + File.separator + "car"
                + File.separator + carFileName)));
        TimeUnit.SECONDS.sleep(20);
        log.info(carFileName + " uploaded successfully");
    }

    @Test(groups = "wso2.esb", enabled = true, description = "Test whether proxy which has Rule " +
            "mediator which fetch custom rules from registry in sequence get deployed through capp")
    public void testRuleMediatorProxyDeployed() throws Exception {
        Thread.sleep(6000);
        org.testng.Assert.assertTrue(
                esbUtils.isProxyDeployed(contextUrls.getBackEndUrl(), getSessionCookie(),
                        "proxyService2")
                , "ERROR - ProxyServiceDeployer ProxyService Deployment from the file : " +
                        "esb-artifacts-rule-mediator-car_1.0.0.car/" +
                        "proxyService2_1.0.0/proxyService2-1.0.0.xml : Failed");
    }

    @AfterTest(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
        super.cleanup();
    }
}
