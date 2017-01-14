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
* This class checks in memory message store again concurrent large no of messages
*/
public class MessageStoreMessageConcurrencyTestCase extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;
    private final String MESSAGE_STORE_NAME = "automationMessageStore";
    private boolean isMessageStoreCreated = false;
    private String[] messageStores = null;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        messageStoreAdminClient =
                new MessageStoreAdminClient(contextUrls.getBackEndUrl(),
                                            getSessionCookie());
        initialize();
    }

    @Test(groups = {"wso2.esb"}, description = "Test whether all messages are stored from different sources")
    public void messageStoreQuantityTest() throws Exception {
        // The count should be 0 as soon as the message store is created
        Assert.assertTrue(messageStoreAdminClient.getMessageCount(MESSAGE_STORE_NAME) == 0,
                          "Message store should be initially empty");
        // refer within a sequence through a store mediator, mediate messages
        // and verify the messages are stored correctly in the store.
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/messageStore/sample_700.xml");
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            threads.add(new Sender());
        }
        //each thread send 5 message simultaneously
        for (int i = 0; i < 10; i++) {
            threads.get(i).start();
        }
        Thread.sleep(30000);
        Assert.assertTrue(messageStoreAdminClient.getMessageCount(MESSAGE_STORE_NAME) == 40,
                          "Messsages are missing or repeated");
        MessageInfo info[] = messageStoreAdminClient.getPaginatedMessages(MESSAGE_STORE_NAME, 0);
        String sendEnvelope =
                "<?xml version='1.0' encoding='utf-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns:getSimpleQuote xmlns:ns=\"http://services.samples\"><ns:symbol>WSO2</ns:symbol></ns:getSimpleQuote></soapenv:Body></soapenv:Envelope>";
        OMElement sendElement = AXIOMUtil.stringToOM(sendEnvelope);
        for (int i = 1; i <= 4; i++) {
            for (int j = 0; j < info.length; j++) {
                OMElement stored = AXIOMUtil.stringToOM(info[j].getSoapXml());
                // verify whether the SOAP message is equivalent to what was
                // mediated
                Assert.assertEquals(sendElement.toString(), stored.toString());
            }
            info = messageStoreAdminClient.getPaginatedMessages(MESSAGE_STORE_NAME, i);
        }
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


    class Sender extends Thread {
        private StockQuoteClient client = new StockQuoteClient();

        @Override
        public void run() {
            for (int i = 0; i < 4; i++) {
                try {
                    client.sendSimpleQuoteRequest(getMainSequenceURL(), null, "WSO2");
                } catch (Exception e) {
                }
            }
        }
    }
}
