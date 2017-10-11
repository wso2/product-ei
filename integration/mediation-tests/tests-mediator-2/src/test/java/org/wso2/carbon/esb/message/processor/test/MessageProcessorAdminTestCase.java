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

package org.wso2.carbon.esb.message.processor.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;


public class MessageProcessorAdminTestCase extends ESBIntegrationTest {

    private MessageProcessorClient messageProcessorClient;

    private AutomationContext esbContext;
    private String sessionCookie;
    private LoginLogoutClient loginLogoutClient;

    @BeforeClass(alwaysRun = true, description = "Test Car with Mediator deployment")
    protected void setup() throws Exception {
        super.init();

        esbContext = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        loginLogoutClient = new LoginLogoutClient(esbContext);
        sessionCookie = loginLogoutClient.login();

        messageProcessorClient = new MessageProcessorClient(esbContext.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @Test(groups = {"wso2.esb"}, description = "Test adding a new message processor.")
    public void testAddNewMessageProcessor() throws Exception {

        messageProcessorClient.addMessageProcessor(new DataHandler(new URL("file:" +
                File.separator + File.separator +
                getESBResourceLocation() + File.separator + "messageProcessorConfig" +
                File.separator + "processorWithInMemoryStoreForTestCreation.xml")));

        Assert.assertTrue(true);
        verifyMessageProcessorExistence("processorWithInMemoryStoreForTestCreation");
    }


    @AfterClass(alwaysRun = true)
    public void cleanState() throws Exception {

        super.cleanup();
    }

}
