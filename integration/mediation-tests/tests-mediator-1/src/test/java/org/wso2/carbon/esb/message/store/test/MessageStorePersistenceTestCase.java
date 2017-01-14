/*
* Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.esb.message.store.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.Arrays;
import java.util.List;

/**
* This class checks whether a message store is persisted after ESB restarted
*/
public class MessageStorePersistenceTestCase extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;
    private final String MESSAGE_STORE_NAME = "automationMessageStore";
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        messageStoreAdminClient = new MessageStoreAdminClient(contextUrls.getBackEndUrl(),
                                                              getSessionCookie());
        serverConfigurationManager = new ServerConfigurationManager(context);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test whether message store is persistance")
    public void messageStorePersistenceTest() throws Exception {
        OMElement messageStore =
                AXIOMUtil.stringToOM("<messageStore xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" +
                                     MESSAGE_STORE_NAME +
                                     "\">" +
                                     "<parameter name=\"abc\">10</parameter>" +
                                     "</messageStore>");
        addMessageStore(messageStore);
        String[] messageStores = messageStoreAdminClient.getMessageStores();
        // addEndpoint is a a asynchronous call, it will take some time to write
        // to a registry
        int i = 0;
        boolean found = false;
        for (i = 0; i < 10; i++) {
            Thread.sleep(1000);
            if (messageStores != null) {
                for (int j = 0; j < messageStores.length; j++) {
                    String string = messageStores[j];
                    if (string.equalsIgnoreCase(MESSAGE_STORE_NAME)) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                break;
            }
            messageStores = messageStoreAdminClient.getMessageStores();
        }
        if (i == 10) {
            Assert.fail("message store creation failed");
        }
        //let message processor to persist
        Thread.sleep(5000);
        serverConfigurationManager.restartGracefully();
        Thread.sleep(10000);
        super.init();
        messageStoreAdminClient = new MessageStoreAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        messageStores = messageStoreAdminClient.getMessageStores();
        Assert.assertNotNull(messageStores, "Message Store Not Found after restarting server");
        if (messageStores != null) {
            List list = Arrays.asList(messageStores);
            Assert.assertTrue(list.contains(MESSAGE_STORE_NAME),
                              "Message Store not found after restarting server: " + MESSAGE_STORE_NAME);
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        String[] messageStores = messageStoreAdminClient.getMessageStores();
        if (messageStores != null) {
            List list = Arrays.asList(messageStores);
            if (list.contains(MESSAGE_STORE_NAME)) {
                esbUtils.deleteMessageStore(contextUrls.getBackEndUrl(), getSessionCookie(), MESSAGE_STORE_NAME);
            }
        }
        messageStoreAdminClient = null;
        serverConfigurationManager = null;
        super.cleanup();
    }
}
