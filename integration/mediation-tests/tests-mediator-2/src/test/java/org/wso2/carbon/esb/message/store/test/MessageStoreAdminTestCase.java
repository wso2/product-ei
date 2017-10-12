/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.message.store.test;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.message.store.stub.MessageInfo;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Tests to verify service behaviour of Message Store Admin Service.
 */
public class MessageStoreAdminTestCase extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;

    private final String PROCESSOR_NAME = "mspAdminTestInMemoryMessageProcessor";
    private final String PROXY_NAME = "mspAdminTestProxy";
    private final String STORE_NAME = "mspAdminTestInMemoryMessageStore";

    @BeforeClass(alwaysRun = true, description = "Test Message Store admin services ")
    protected void setup() throws Exception {
        super.init();

        loadESBConfigurationFromClasspath("artifacts/ESB/messageProcessorConfig/mspAdminTestConfig.xml");

        messageStoreAdminClient = new MessageStoreAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

        verifyMessageStoreExistence(STORE_NAME);
        verifyMessageProcessorExistence(PROCESSOR_NAME);
        verifyProxyServiceExistence(PROXY_NAME);
    }

    /**
     * 1. Invoke the proxy with 5 requests while the backend is unavailable.
     * 2. Verify existence of messages in message store.
     * 3. Verify retrieval of message content.
     * 4. Delete a message/ all messages, and verify the pending message count.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test pending message related operations on a message processor.")
    public void testMessagesInMessageStore() throws Exception {

        int initialMessageCount = 5;

        for (int i = 0; i < initialMessageCount; i++) {
            axis2Client.sendSimpleQuoteRequest(getProxyServiceURLHttp(PROXY_NAME), null, "IBM");
        }

        MessageInfo[] messages = messageStoreAdminClient.getPaginatedMessages(STORE_NAME, 0);

        Assert.assertEquals(messages.length, initialMessageCount, "Pending message count from list for Message store : " +
                STORE_NAME + " is not accurate.");

        String messageEnvelope = messageStoreAdminClient.getEnvelope(STORE_NAME, messages[0].getMessageId());

        Assert.assertEquals(messageEnvelope, generateSampleStockQuoteRequest("IBM"), "Message content retrieved from " +
                "the Message Store is not same as the published content.");

        messageStoreAdminClient.deleteFirstMessage(STORE_NAME);

        Assert.assertEquals(messageStoreAdminClient.getMessageCount(STORE_NAME), initialMessageCount - 1, "Message was " +
                "not deleted from message store : " + STORE_NAME);

        messageStoreAdminClient.deleteAllMessages(STORE_NAME);

        Assert.assertEquals(messageStoreAdminClient.getMessageCount(STORE_NAME), 0, "Messages were " +
                "not deleted from message store : " + STORE_NAME);

    }

    /**
     * Generate a request payload similar to what is sent by the test-axis2-client for SimpleStockQuoteService.
     * @param symbol parameter used for payload
     * @return sample request payload
     */
    private String generateSampleStockQuoteRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();

        OMNamespace soapNs = fac.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");

        OMElement soapEnvelope = fac.createOMElement("Envelope", soapNs);
        OMElement soapBody = fac.createOMElement("Body", soapNs);

        OMElement method = fac.createOMElement("getSimpleQuote", omNs);
        OMElement value1 = fac.createOMElement("symbol", omNs);

        value1.addChild(fac.createOMText(method, symbol));
        method.addChild(value1);

        soapBody.addChild(method);
        soapEnvelope.addChild(soapBody);

        return "<?xml version='1.0' encoding='utf-8'?>" + soapEnvelope.toString();
    }


    @AfterClass(alwaysRun = true)
    public void cleanState() throws Exception {

        super.cleanup();
    }

}
