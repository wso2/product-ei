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

package org.wso2.carbon.esb.message.processor.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.message.store.stub.MessageInfo;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.xml.namespace.QName;
import java.util.Iterator;

/**
 * Tests to verify service behaviour of both Message Processor Admin Service and Message Store Admin Service.
 */
public class MessageProcessorAdminTestCase extends ESBIntegrationTest {

    private MessageProcessorClient messageProcessorClient;
    private MessageStoreAdminClient messageStoreAdminClient;

    private AutomationContext esbContext;
    private String sessionCookie;
    private LoginLogoutClient loginLogoutClient;

    private final String PROCESSOR_NAME = "mspAdminTestInMemoryMessageProcessor";
    private final String PROXY_NAME = "mspAdminTestProxy";
    private final String STORE_NAME = "mspAdminTestInMemoryMessageStore";

    @BeforeClass(alwaysRun = true, description = "Test Car with Mediator deployment")
    protected void setup() throws Exception {
        super.init();

        esbContext = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        loginLogoutClient = new LoginLogoutClient(esbContext);
        sessionCookie = loginLogoutClient.login();

        messageProcessorClient = new MessageProcessorClient(esbContext.getContextUrls().getBackEndUrl(), sessionCookie);
        messageStoreAdminClient = new MessageStoreAdminClient(esbContext.getContextUrls().getBackEndUrl(), sessionCookie);

        verifyMessageProcessorExistence(PROCESSOR_NAME);
        verifyProxyServiceExistence(PROXY_NAME);

    }

    /**
     * Modify the existing message processor by updating the interval configuration of processor.
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test modifying a message processor.")
    public void modifyMessageProcessor() throws Exception {

        String modifyingAttribute = "interval";
        String modifiedInterval = "6000";

        OMElement processorConfig = esbUtils.loadResource
                ("artifacts/ESB/server/repository/deployment/server/synapse-configs/default/message-processors/" +
                        PROCESSOR_NAME + ".xml");

        Iterator<OMElement> iterator = processorConfig.getChildElements();

        while (iterator.hasNext()) {
            OMElement child = iterator.next();

            if (child.getAttribute(new QName("name")).getAttributeValue().equals(modifyingAttribute)) {
                child.setText(modifiedInterval);
                break;
            }
        }

        messageProcessorClient.modifyMessageProcessor(processorConfig.toString());

        String modifiedProcessorConfig = messageProcessorClient.getMessageProcessor(PROCESSOR_NAME);

        OMElement modifiedProcessorXML = AXIOMUtil.stringToOM(modifiedProcessorConfig);

        boolean processorUpdated = false;

        Iterator<OMElement> modifiedIterator = modifiedProcessorXML.getChildElements();

        while (modifiedIterator.hasNext()) {
            OMElement child = modifiedIterator.next();

            if (child.getAttribute(new QName("name")).getAttributeValue().equals(modifyingAttribute)) {
                if (child.getText().equals(modifiedInterval)) {
                    processorUpdated = true;
                    break;
                }
            }
        }

        Assert.assertTrue(processorUpdated, "Message Processor was not updated with new interval value : " + modifiedInterval);
    }

    /**
     * Verify activation/ de-activation of Message Processor.
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test activating/deactivating a message processor.")
    public void testActivateMessageProcessor() throws Exception {

        messageProcessorClient.deactivateProcessor(PROCESSOR_NAME);

        Assert.assertFalse(messageProcessorClient.isActive(PROCESSOR_NAME), "Message processor should not be active, " +
                "but it is active.");

        messageProcessorClient.activateMessageProcessor(PROCESSOR_NAME);

        Assert.assertTrue(messageProcessorClient.isActive(PROCESSOR_NAME), "Message processor should be active, but " +
                "it is not active.");
    }

    /**
     * 1. Invoke the proxy with 5 requests while the backend is unavailable.
     * 2. Verify existence of messages in message store + automatic de-activation of processor due to backend failure.
     * 3. Delete a message and verify the pending message count.
     * @throws Exception
     */
    @Test(groups = {"wso2.esb"}, description = "Test pending message related operations on a message processor.")
    public void testMessagesInMessageProcessor() throws Exception {

        int initialMessageCount = 5;
        long waitMilliseconds = 30000;

        for (int i=0; i< initialMessageCount; i++) {
            axis2Client.sendSimpleQuoteRequest(getProxyServiceURLHttp(PROXY_NAME), null, "IBM");
        }

        long deadline = System.currentTimeMillis() + waitMilliseconds;
        while (System.currentTimeMillis() < deadline) {
            if (!messageProcessorClient.isActive(PROCESSOR_NAME)) {
                break;
            }
            Thread.sleep(1000);
        }

        Assert.assertFalse(messageProcessorClient.isActive(PROCESSOR_NAME), "Message Processor : " + PROCESSOR_NAME +
                " did not de-activate after failed delivery attempts.");

        MessageInfo[] messages = messageStoreAdminClient.getPaginatedMessages(STORE_NAME, 0);

        Assert.assertEquals(messages.length, initialMessageCount, "Pending message count from list for Message store : " +
                STORE_NAME + " is not accurate.");

        int getSizeResult = messageStoreAdminClient.getMessageCount(STORE_NAME);

        Assert.assertEquals(getSizeResult, initialMessageCount,
                "message count returned by getSize service is wrong for Message store : " + STORE_NAME + ".");

        messageStoreAdminClient.deleteMessage(STORE_NAME, messages[0].getMessageId());

        Assert.assertEquals(messageStoreAdminClient.getMessageCount(STORE_NAME), initialMessageCount -1, "Message was" +
                " not deleted from " +
                "message store : " + STORE_NAME);

        SampleAxis2Server offsetAxis2Server = new SampleAxis2Server("test_axis2_server_9018.xml");
        offsetAxis2Server.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        offsetAxis2Server.start();

        messageProcessorClient.activateMessageProcessor(PROCESSOR_NAME);

        long processDeadline = System.currentTimeMillis() + waitMilliseconds;
        while (System.currentTimeMillis() < processDeadline) {
            if (messageStoreAdminClient.getMessageCount(STORE_NAME) == 0) {
                break;
            }
            Thread.sleep(1000);
        }

        offsetAxis2Server.stop();

        Assert.assertEquals(messageStoreAdminClient.getMessageCount(STORE_NAME), 0, "Messages were not sent after " +
                "activating message processor : " + PROCESSOR_NAME);

    }


    @AfterClass(alwaysRun = true)
    public void cleanState() throws Exception {

        super.cleanup();
    }

}
