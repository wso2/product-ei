/*
 * Copyright (c)2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.nhttp.transport.test;

import org.testng.annotations.BeforeTest;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;

/**
 * NhttpBaseTestCase replaces the default axis2.xml with the one at /artifacts/ESB/nhttp/transport/axis2.xml which
 * enables nhttp for the http transport at the beginning of the execution of nhttp tests.
 */
public class NhttpBaseTestCase extends ESBIntegrationTest {


    ServerConfigurationManager serverManager;

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        serverManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverManager.applyConfiguration(
                new File(getClass().getResource("/artifacts/ESB/nhttp/transport/axis2.xml").getPath()));
        super.init();
    }
}
