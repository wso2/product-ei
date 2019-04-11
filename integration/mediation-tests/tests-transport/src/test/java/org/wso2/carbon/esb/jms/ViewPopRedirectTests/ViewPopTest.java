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

import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class ViewPopTest extends ESBIntegrationTest {
    private final String STORE_NAME = "VPE_Store";
    private final String PROCESSOR_NAME = "VPE_Processor";
    private final String PROXY_NAME = "VPE_Proxy";
    private MessageProcessorClient messageProcessorClient;
    private String returnedMessage = null;

    /**
     * Initializing environment variables
     */
    @BeforeClass(alwaysRun = true, description = "Test Message processor View and Pop service")
    protected void setup() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/messageProcessorConfig/ViewPopTest.xml");

        // Initialization
        messageProcessorClient = new MessageProcessorClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

        verifyMessageStoreExistence(STORE_NAME);
        verifyMessageProcessorExistence(PROCESSOR_NAME);
        isProxySuccesfullyDeployed(PROXY_NAME);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    /**
     * 1. Send one payload to the proxy while the backend is unavailable
     * 2. Check if the Message Processor has successfully deactivated
     * 3. Call browseMessage function and verify that the queue is sending the expected message
     * 4. Call popMessage function and verify that browseMessage function is returning null
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

        //Check if the processor has deactivated
        Awaitility.await()
                .pollInterval(3, TimeUnit.SECONDS)
                .atMost(120, TimeUnit.SECONDS)
                .until(isProcessorDeactivated(PROCESSOR_NAME));
        Assert.assertFalse(messageProcessorClient.isActive(PROCESSOR_NAME), "Message processor should not be active, " +
                "but it is active.");

        //Check if browseMessage returns the expected message
        Awaitility.await()
                .pollInterval(3, TimeUnit.SECONDS)
                .atMost(120, TimeUnit.SECONDS)
                .until(hasMessage(PROCESSOR_NAME));
        Assert.assertEquals(returnedMessage, expectedMessage, "Returned message is not the same as expected message.");

        //Check if message is successfully popped from the queue
        Assert.assertTrue(messageProcessorClient.popMessage(PROCESSOR_NAME), "Message processor failed to pop the message");

        //Check if the returned message is null
        returnedMessage = messageProcessorClient.browseMessage(PROCESSOR_NAME);
        Assert.assertNull(returnedMessage, "Returned message is not null as expected.");
    }

    private Callable<Boolean> hasMessage(final String PROCESSOR_NAME) throws Exception {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                returnedMessage = messageProcessorClient.browseMessage(PROCESSOR_NAME);
                if (returnedMessage == null) {
                    return false;
                }
                return true;
            }
        };
    }

    private Callable<Boolean> isProcessorDeactivated(final String PROCESSOR_NAME) throws Exception {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (messageProcessorClient.isActive(PROCESSOR_NAME)) {
                    return false;
                }
                return true;
            }
        };
    }

}
