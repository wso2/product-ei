package org.wso2.carbon.esb.jms.ViewPopEnqueueTest;

import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EnqueueTest extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;
    private MessageProcessorClient messageProcessorClient;
    private JMSQueueMessageProducer jmsQueueMessageProducer;

    private ConfigurationContext configurationContext;

    private final String STORE_NAME = "BASE_Store";
    private final String PROCESSOR_NAME = "BASE_Processor";
    private final String PROXY_NAME = "BASE_Proxy";

    private final String ENQUEUE_STORE_NAME = "ENQUEUE_Store";
    private final String ENQUEUE_PROCESSOR_NAME = "ENQUEUE_Processor";

    /**
     *  Initializing environment variables
     */

    @BeforeClass(alwaysRun = true, description = "Test Message processor Enqueue service")
    protected void setup() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts/ESB/messageProcessorConfig/EnqueueTest.xml");

        // Initialization
        messageStoreAdminClient = new MessageStoreAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        messageProcessorClient = new MessageProcessorClient(context.getContextUrls().getBackEndUrl(), sessionCookie);

        verifyMessageStoreExistence(STORE_NAME);
        verifyMessageStoreExistence(ENQUEUE_STORE_NAME);
        verifyMessageProcessorExistence(PROCESSOR_NAME);
        verifyMessageProcessorExistence(ENQUEUE_PROCESSOR_NAME);
        isProxySuccesfullyDeployed(PROXY_NAME);
    }

    /**
     *  1. Send one payload to the proxy while the backend is unavailable
     *  2. Check if the Message Processor has successfully deactivated
     *  3. Call the popandEnqueue fucntion and verify that getMessage function is returning is expected message
     */

    @Test(groups = {"wso2.esb"}, description = "Test Enqueue service for Message processor")
    public void testEnqueue() throws Exception {

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

        //Send payload to the proxy
        System.out.println("=== Sending payload to " + PROXY_NAME +":"+ getProxyServiceURLHttp(PROXY_NAME));
        HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(PROXY_NAME)), inputPayload, requestHeader);

        Thread.sleep(5000); //Make sure that the Message Processor has time to deactivate

        //Check if the message processor has deactivated
        Assert.assertFalse(messageProcessorClient.isActive(PROCESSOR_NAME), "Message processor" + PROCESSOR_NAME +"should not be active, " +
                "but it is active.");

        //Deactivate the ENQUEUE_PROCESSOR
        messageProcessorClient.deactivateProcessor(ENQUEUE_PROCESSOR_NAME);
        Assert.assertFalse(messageProcessorClient.isActive(ENQUEUE_PROCESSOR_NAME), "Message processor" + ENQUEUE_PROCESSOR_NAME + " should not be active, " +
                "but it is active.");

        //Call popAndEnqueue function to pass the message to ENQUEUE_STORE
        messageProcessorClient.popAndEnqueueMessage(PROCESSOR_NAME, ENQUEUE_STORE_NAME);


        //Call the getMessage function from the ENQUEUE_PROCESSOR
        String returnedMessage = messageProcessorClient.getMessage(ENQUEUE_PROCESSOR_NAME);
        System.out.println("=== RETURNED MESSAGE === \n" + returnedMessage );
        Assert.assertEquals(returnedMessage,expectedMessage,"Returned message is not the same as expected message.");

    }

}
