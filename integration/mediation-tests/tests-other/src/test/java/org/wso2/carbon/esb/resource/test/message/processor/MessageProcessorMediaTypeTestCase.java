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
package org.wso2.carbon.esb.resource.test.message.processor;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.rmi.RemoteException;

public class MessageProcessorMediaTypeTestCase extends ESBIntegrationTest{
    private Log log = LogFactory.getLog(MessageProcessorMediaTypeTestCase.class);

    private MessageProcessorClient messageProcessorClient;
    private MessageStoreAdminClient messageStoreAdminClient;
    private ResourceAdminServiceClient resourceAdmin;
    private final String MESSAGE_PROCESSOR_NAME = "automationMessageProcessor";
    private final String MESSAGE_STORE_NAME = "xyz";
    private boolean isMessageProcessorExist = false;
    private boolean isMessageStoreExist = false;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        messageProcessorClient = new MessageProcessorClient(contextUrls.getBackEndUrl(), getSessionCookie());
        messageStoreAdminClient = new MessageStoreAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test message Processor media type - text/xml", enabled = false)
    public void messageProcessorMediaTypeTest() throws Exception {
        String messageStoreName = "xyz";
        OMElement messageStore = AXIOMUtil.stringToOM("<messageStore xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + messageStoreName + "\">" +
                                                      "<parameter name=\"abc\">10</parameter>" +
                                                      "</messageStore>");
        messageStoreAdminClient.addMessageStore(messageStore);
        isMessageStoreExist = true;
        Thread.sleep(1000);
        OMElement messageProcessor = AXIOMUtil.stringToOM("<messageProcessor xmlns=\"http://ws.apache.org/ns/synapse\" " +
                                                          "class=\"org.apache.synapse.message.processors.forward.ScheduledMessageForwardingProcessor\" " +
                                                          "name=\"" + MESSAGE_PROCESSOR_NAME + "\" messageStore=\"" + messageStoreName + "\">" +
                                                          "<parameter name=\"abc\">100</parameter>" +
                                                          "</messageProcessor>");
        messageProcessorClient.addMessageProcessor(messageProcessor);
        isMessageProcessorExist = true;
        //addEndpoint is a a asynchronous call, it will take some time to write to a registry
        Thread.sleep(10000);
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/synapse-message-processors/" + MESSAGE_PROCESSOR_NAME);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for Message Processor");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws RemoteException {
        if (isMessageProcessorExist) {
            messageProcessorClient.deleteMessageProcessor(MESSAGE_PROCESSOR_NAME);
        }
        if (isMessageStoreExist) {
            messageStoreAdminClient.deleteMessageStore(MESSAGE_STORE_NAME);
        }
    }
}
