package org.wso2.carbon.esb.jms.ViewPopEnqueueTest;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.message.processor.service.xsd.MessageProcessorMetaData;
import org.wso2.carbon.message.processor.stub.MessageProcessorAdminService;
import org.wso2.carbon.message.processor.stub.MessageProcessorAdminServiceCallbackHandler;
import org.wso2.carbon.message.processor.stub.MessageProcessorAdminServiceException;
import org.wso2.carbon.message.processor.stub.MessageProcessorAdminServiceStub;
import org.wso2.carbon.message.store.stub.MessageInfo;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ViewPopEnqueueTest extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;
    MessageProcessorAdminService messageProcessorAdminService;
    MessageProcessorClient messageProcessorClient;
    private JMSQueueMessageProducer jmsQueueMessageProducer;

    private ConfigurationContext configurationContext;

    private final String STORE_NAME = "VPE_Store";
    private final String PROCESSOR_NAME = "VPE_Processor";
    private final String PROXY_NAME = "VPE_Proxy";

    /**
     *  Initializing environment variables
     */

    @BeforeClass(alwaysRun = true, description = "Test Message store View, Pop and Enqueue")
    protected void setup() throws Exception {
        super.init();


        loadESBConfigurationFromClasspath("artifacts/ESB/messageProcessorConfig/viewPopEnqueueTest.xml");

        // Initialization
        messageStoreAdminClient = new MessageStoreAdminClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
        messageProcessorClient = new MessageProcessorClient(context.getContextUrls().getBackEndUrl(),sessionCookie);

        AxisConfiguration axisConfiguration = new AxisConfiguration();
        configurationContext = new ConfigurationContext(axisConfiguration);

        messageProcessorAdminService = new MessageProcessorAdminServiceStub(null,null);

        System.out.println("=== Initialized the messageProcessorAdminService");


        // Verifying Existence
        verifyMessageStoreExistence(STORE_NAME);
        verifyMessageProcessorExistence(PROCESSOR_NAME);
        isProxySuccesfullyDeployed(PROXY_NAME);
        //verifySequenceExistence(PROXY_NAME);



    }

    /**
     *  1. Invoke the proxy with one request while the backend is unavailable
     *  2. Verify the existence of messages in message store + automatic de-activation of processor due to backend failure
     *  3. Call getMessage function and verify that the queue is sending the right message
     *
     *  Run View and Pop functions
     *  Assert
     */

    @Test(groups = {"wso2.esb"}, description = "Test View and Pop and service in MessageStore")
    public void testViewInMessageStore() throws Exception {


        //Invoke proxy to send message to store
        //axis2Client.sendSimpleQuoteRequest(getProxyServiceURLHttp(PROXY_NAME), null, "IBM");

        //Check if the message processor is active
        System.out.println("=== Checking if the Message processor is ACTIVE ===");
        Assert.assertTrue(messageProcessorClient.isActive(PROCESSOR_NAME), "Message processor should not be active, " +
                "but it is active.");

        System.out.println("=== Checking if there are any messages in the store BEFORE SENDING THE MESSAGE===");
        int num = messageStoreAdminClient.getMessageCount(STORE_NAME);
        System.out.println("=== We found  " + num + " messages");

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

        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "text/xml");
        requestHeader.put("SOAPAction", "urn:mediate");
        requestHeader.put("Accept", "application/json");

        System.out.println("=== Sending payload to " + PROXY_NAME + " === " + getProxyServiceURLHttp(PROXY_NAME));
        HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(PROXY_NAME)), inputPayload, requestHeader);

        Thread.sleep(5000); //Make sure that the Message Processor has time to deactivate

        //Check if the message processor has deactivated
        System.out.println("=== Checking if the Message processor has DEACTIVATED ===");
        Assert.assertFalse(messageProcessorClient.isActive(PROCESSOR_NAME), "Message processor should not be active, " +
                "but it is active.");

        //Check the Queue/Store to see if there are any left over messages
        System.out.println("=== Checking if there are any messages in the store AFTER SENDING THE MESSAGE===");
        num = messageStoreAdminClient.getMessageCount(STORE_NAME);
        System.out.println("=== We found  " + num + " messages");

        Assert.assertEquals(messageStoreAdminClient.getMessageCount(STORE_NAME), -1, "Message was " +
                "not deleted from message store : " + STORE_NAME);

        //Call getMessage function passing the PROCESSOR_NAME and assert the message
        System.out.println("=== Retrieving msg from Queue. Passing processor : " + PROCESSOR_NAME +" ===");
        String returnedMessage = messageProcessorAdminService.getMessage(PROCESSOR_NAME);
        String expectedMessage = "Expected Message should be defined here";
        //Assert.assertEquals(returnedMessage,expectedMessage);

    }

}
