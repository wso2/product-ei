/*
 *Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.jms.ViewPopRedirectTests;

import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ViewPopTest extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;
    private MessageProcessorClient messageProcessorClient;
    private JMSQueueMessageProducer jmsQueueMessageProducer;

    private ConfigurationContext configurationContext;

    private final String STORE_NAME = "VPE_Store";
    private final String PROCESSOR_NAME = "VPE_Processor";
    private final String PROXY_NAME = "VPE_Proxy";

    /**
     *  Initializing environment variables
     */
    @BeforeClass(alwaysRun = true, description = "Test Message processor View and Pop service")
    protected void setup() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/messageProcessorConfig/ViewPopTest.xml");

        // Initialization
        messageStoreAdminClient = new MessageStoreAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        messageProcessorClient = new MessageProcessorClient(context.getContextUrls().getBackEndUrl(),sessionCookie);

        verifyMessageStoreExistence(STORE_NAME);
        verifyMessageProcessorExistence(PROCESSOR_NAME);
        isProxySuccesfullyDeployed(PROXY_NAME);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    /**
     *  1. Send one payload to the proxy while the backend is unavailable
     *  2. Check if the Message Processor has successfully deactivated
     *  3. Call browseMessage function and verify that the queue is sending the expected message
     *  4. Call popMessage function and verify that browseMessage function is returning null
     */
    @Test(groups = {"wso2.esb"}, description = "Test View and Pop and service for Message processor")
    public void testViewInMessageStore() throws Exception {

        //Initializing Payload
        String inputPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "<soapenv:Header/>\n"
                + "<soapenv:Body>\n"
                + "<m0:getQuote xmlns:m0=\"http://services.samples\">\n"
                + " <m0:request>IBM\n"
                + " </m0:request>\n"
                + "   <m0:request>WSO2\n"
                + " </m0:request>\n"
                + "</m0:getQuote>\n"
                + "</soapenv:Body>\n"
                + "</soapenv:Envelope>";

        String expectedMessage = "<?xml version='1.0' encoding='utf-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soapenv:Body>"
                + "<m0:getQuote xmlns:m0=\"http://services.samples\">\n"
                + " <m0:request>IBM\n"
                + " </m0:request>\n"
                + "   <m0:request>WSO2\n"
                + " </m0:request>\n"
                + "</m0:getQuote>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "text/xml");
        requestHeader.put("SOAPAction", "urn:mediate");
        requestHeader.put("Accept", "application/json");

        HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(PROXY_NAME)), inputPayload, requestHeader);
        Thread.sleep(5000); //Make sure that the Message Processor has time to deactivate

        Assert.assertFalse(messageProcessorClient.isActive(PROCESSOR_NAME), "Message processor should not be active, " +
                "but it is active.");

        String returnedMessage = messageProcessorClient.browseMessage(PROCESSOR_NAME);
        Assert.assertEquals(returnedMessage,expectedMessage,"Returned message is not the same as expected message.");

        messageProcessorClient.popMessage(PROCESSOR_NAME);
        returnedMessage = messageProcessorClient.browseMessage(PROCESSOR_NAME);
        Assert.assertEquals(returnedMessage,null);
    }

}
