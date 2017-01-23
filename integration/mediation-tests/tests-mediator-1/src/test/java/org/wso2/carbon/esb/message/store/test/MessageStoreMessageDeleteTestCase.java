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
import org.wso2.esb.integration.common.utils.clients.stockquoteclient.StockQuoteClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.message.store.stub.MessageInfo;

import java.util.ArrayList;

/**
* This class checks the delete functionality of messages in message stores
*/
public class MessageStoreMessageDeleteTestCase extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;
    private final String MESSAGE_STORE_NAME = "automationMessageStore";
    private boolean isMessageStoreCreated = false;
    private String[] messageStores = null;
    private StockQuoteClient client;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        messageStoreAdminClient =
                new MessageStoreAdminClient(contextUrls.getBackEndUrl(),
                                            getSessionCookie());
        client = new StockQuoteClient();
        initialize();
    }

    @Test(groups = {"wso2.esb"}, description = "Test whether messages are deleted successfully")
    public void messageDeletingTest() throws Exception {
        // The count should be 0 as soon as the message store is created
        Assert.assertTrue(messageStoreAdminClient.getMessageCount(MESSAGE_STORE_NAME) == 0,
                          "Message store should be initially empty");
        // refer within a sequence through a store mediator, mediate messages
        // and verify the messages are stored correctly in the store.
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/messageStore/sample_700.xml");
        for (int i = 0; i < 5; i++) {
            client.sendSimpleQuoteRequest(getMainSequenceURL(), null, "WSO2");
        }
        Thread.sleep(30000);
        Assert.assertTrue(messageStoreAdminClient.getMessageCount(MESSAGE_STORE_NAME) == 5,
                          "Messsages are missing or repeated");
        MessageInfo info[] = messageStoreAdminClient.getPaginatedMessages(MESSAGE_STORE_NAME, 0);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < info.length; i++) {
            list.add(info[i].getMessageId());
        }
        messageStoreAdminClient.deleteMessage(MESSAGE_STORE_NAME, list.get(0));
        messageStoreAdminClient.deleteMessage(MESSAGE_STORE_NAME, list.get(1));
        Thread.sleep(30000);
        Assert.assertTrue(messageStoreAdminClient.getMessageCount(MESSAGE_STORE_NAME) == 3,
                          "Error in message deletion");
        for (int i = 2; i < 5; i++) {
            messageStoreAdminClient.deleteMessage(MESSAGE_STORE_NAME, list.get(i));
        }
        Thread.sleep(30000);
        //message count should be 0 after deleting all messages
        Assert.assertTrue(messageStoreAdminClient.getMessageCount(MESSAGE_STORE_NAME) == 0,
                          "Error in message deletion");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        clear();
        messageStoreAdminClient = null;
        super.cleanup();
    }

    // creates a message store
    public void initialize() throws Exception {
        OMElement messageStore =
                AXIOMUtil.stringToOM("<messageStore xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" +
                                     MESSAGE_STORE_NAME +
                                     "\">" +
                                     "<parameter name=\"abc\">10</parameter>" +
                                     "</messageStore>");
        addMessageStore(messageStore);
        messageStores = messageStoreAdminClient.getMessageStores();
        // addEndpoint is a a asynchronous call, it will take some time to write
        // to a registry
        int i = 0;
        boolean found = false;
        for (i = 0; i < 50; i++) {
            Thread.sleep(1000);
            if (messageStores != null) {
                for (int j = 0; j < messageStores.length; j++) {
                    String string = messageStores[j];
                    if (string.equalsIgnoreCase(MESSAGE_STORE_NAME)) {
                        found = true;
                        isMessageStoreCreated = true;
                        break;
                    }
                }
            }
            if (found) {
                break;
            }
            messageStores = messageStoreAdminClient.getMessageStores();
        }
        if (i == 50) {
            Assert.fail("message store creation failed");
        }
    }

    // delete the message store
    public void clear() throws Exception {
        if (isMessageStoreCreated) {
            esbUtils.deleteMessageStore(contextUrls.getBackEndUrl(), getSessionCookie(), MESSAGE_STORE_NAME);
        }
    }
}
