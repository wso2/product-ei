/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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

package org.wso2.carbon.esb.car.deployment.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * Test case to ensure Carbon apps (.car files) are deployed before the transport starts.
 * JIRA CARBON16113
 */
public class CARBON16113DeployArtifactsBeforeTransportStarsTestCase extends ESBIntegrationTest {

    private final String TRANSPORT_MESSAGE = "Pass-through HTTP Listener started on ";
    private final String CAPP_MESSAGE = "Deploying Carbon Application : car-deployment-before-tranaport-start-test_1.0.0.car";
    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    private void initialize() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(
                new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
    }

    @Test(groups = { "wso2.esb" },
          description = "Testing whether CApp is deployed before transport starts")
    public void carReDeploymentTest() throws Exception {
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        uploadCapp("car-deployment-before-tranaport-start-test_1.0.0.car", new DataHandler(
                new FileDataSource(new File(getESBResourceLocation() + File.separator + "car"
                        + File.separator + "car-deployment-before-tranaport-start-test_1.0.0.car"))));
        logViewerClient.clearLogs();
        serverConfigurationManager.restartGracefully();
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean cappBeforeTransport = false;
        int size = logs.length;
        for (int i = 0; i < size; i++) {
            if (logs[i].getMessage().contains(CAPP_MESSAGE)) {
                for (int j = i; j < size; j++) {
                    if (logs[j].getMessage().contains(TRANSPORT_MESSAGE)) {
                        cappBeforeTransport = true;
                        break;
                    }
                }
                break;
            }
        }

        Assert.assertTrue(cappBeforeTransport, "Transport started before deploying Carbon app");
    }

    @AfterClass(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
        super.cleanup();
    }
}