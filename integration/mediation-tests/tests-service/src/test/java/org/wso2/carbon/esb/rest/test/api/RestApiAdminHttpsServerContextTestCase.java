/*
 *Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.rest.test.api;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.rest.api.RestApiAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * Testing https://wso2.org/jira/browse/ESBJAVA-2282
 */
public class RestApiAdminHttpsServerContextTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.applyConfiguration
                (new File(getESBResourceLocation() + File.separator + "restapi" + 
                           File.separator + "axis2.xml"));
        super.init();
    }

    @Test(groups = "wso2.esb", description = "Testing RestApiAdmin getServerContext method " +
                                             "when http transport is removed from axis2.xml.")
    public void restApiAdminServerContext() throws Exception {
        RestApiAdminClient restApiAdminClient = new RestApiAdminClient
                (contextUrls.getBackEndUrl(),getSessionCookie());

        String serverContext = restApiAdminClient.getServerContext();

        assertTrue(serverContext.startsWith("https://"),"  ");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        serverConfigurationManager.restoreToLastConfiguration();
    }
}
