/*
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

package org.wso2.carbon.esb.mailto.transport.receiver.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.esb.integration.common.utils.MailToTransportUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;

/**
 * This class is for replace axis2.xml to enable mail transport and restart after that to apply
 * new axis2.xml, at the end restore to the default axis2.xml.
 */
public class MailToTransportBaseClass extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration(
                new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" +
                         File.separator + "ESB" + File.separator + "mailTransport" + File.separator +
                         "mailTransportReceiver" + File.separator + "axis2.xml"));
        MailToTransportUtil.readXMLforEmailCredentials();

    }

    @AfterTest(alwaysRun = true)
    public void cleanUp() throws Exception {
        if (serverConfigurationManager != null) {
            serverConfigurationManager.restoreToLastConfiguration();
        }
    }
}
