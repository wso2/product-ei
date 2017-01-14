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

package org.wso2.carbon.esb.proxy.pinnedserver.test;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class ESBJAVA4201ProxyServiceWithPinnedServerTestCase extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    protected void deployProxyService() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                   "artifacts/ESB/proxyconfig/proxy/proxywithpinnedserver/TestProxy.xml");

    }

    @Test(groups = "wso2.esb", enabled = false, description = "Test whether proxy service get deployed")
    public void testProxyIsDeployed() throws Exception {
        isProxyDeployed("TestProxy");
        isProxyNotDeployed("TestProxyWithPinnedServer");

    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
