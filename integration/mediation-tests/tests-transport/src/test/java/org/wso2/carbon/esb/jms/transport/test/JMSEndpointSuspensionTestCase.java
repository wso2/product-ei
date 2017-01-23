package org.wso2.carbon.esb.jms.transport.test;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;

/**
 * https://wso2.org/jira/browse/ESBJAVA-1712
 */
public class JMSEndpointSuspensionTestCase extends ESBIntegrationTest {
    private EndPointAdminClient endPointAdminClient;

//    @BeforeClass(alwaysRun = true)
    public void deployService() throws Exception {
        super.init();
        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/jms_transport_jms_suspension.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
        endPointAdminClient = new EndPointAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

        /* Disabling this test case as it is not properly written and it does not properly test the intention of the author. */
//    @Test(groups = {"wso2.esb"}, description = "Test JMS Endpoint suspension")
    public void testJMSProxy() throws Exception {

        int initialEndpoints = endPointAdminClient.getEndpointCount();
        Assert.assertTrue(endPointAdminClient.getEndpointsData()[initialEndpoints - 1].getSwitchOn(), "Endpoint should not be suspended");

        Thread.sleep(7000);

        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        String queueName = "JmsProxy";
        String message = "<?xml version='1.0' encoding='UTF-8'?>" +
                         "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                         " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">" +
                         "  <soapenv:Header/>" +
                         "  <soapenv:Body>" +
                         "   <ser:placeOrder>" +
                         "     <ser:order>" +
                         "      <xsd:price>100</xsd:price>" +
                         "      <xsd:quantity>2000</xsd:quantity>" +
                         "      <xsd:symbol>JMSTransport</xsd:symbol>" +
                         "     </ser:order>" +
                         "   </ser:placeOrder>" +
                         "  </soapenv:Body>" +
                         "</soapenv:Envelope>";
        try {
            sender.connect(queueName);
            sender.pushMessage(message);
        } finally {
            sender.disconnect();
        }
        Thread.sleep(5000);

        Assert.assertTrue(!endPointAdminClient.getEndpointsData()[initialEndpoints - 1].getSwitchOn(), "Endpoint should be suspended");

    }

//    @AfterClass(alwaysRun = true)
    public void UndeployeService() throws Exception {
        super.cleanup();
        endPointAdminClient = null;
    }
}
