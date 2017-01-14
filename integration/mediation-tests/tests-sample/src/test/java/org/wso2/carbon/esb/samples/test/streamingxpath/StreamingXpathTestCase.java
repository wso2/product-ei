/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.samples.test.streamingxpath;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;

import java.io.File;

public class StreamingXpathTestCase extends ESBIntegrationTest {
    private final SimpleHttpClient httpClient = new SimpleHttpClient();
    private ServerConfigurationManager serverManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        File sourceFile = new File(FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                "ESB" + File.separator + "streamingxpath" + File.separator + "synapse.properties");
        serverManager.applyConfiguration(sourceFile);
        super.init();
        String relativePath = "artifacts" + File.separator + "ESB" + File.separator + "streamingxpath" +
                File.separator + "Streaming.xml";
        ESBTestCaseUtils util = new ESBTestCaseUtils();
        OMElement proxyConfig = util.loadResource(relativePath);
        addProxyService(proxyConfig);

    }

    @Test(groups = "wso2.esb", description = " Streaming XPath Running", enabled = true)
    public void streamingXpathTestCase() throws Exception {
       OMElement result =    axis2Client.sendSimpleQuoteRequest("http://localhost:8480/services/Streaming",null,"IBM");
        Assert.assertNotNull(result);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverManager.restoreToLastConfiguration();
    }

}
