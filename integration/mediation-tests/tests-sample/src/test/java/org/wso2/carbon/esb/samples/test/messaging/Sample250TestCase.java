package org.wso2.carbon.esb.samples.test.messaging;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

public class Sample250TestCase extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        super.init();
        loadSampleESBConfiguration(250);
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    private JMSBrokerConfiguration getJMSBrokerConfiguration() {
        return JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration();
    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test JMS message sending to proxy ")
    public void testJMSProxy() throws Exception {

        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(
                getJMSBrokerConfiguration());
        String queueName = "StockQuoteProxy";
        try {
            sender.connect(queueName);
            for (int i = 0; i < 3; i++) {
                sender.pushMessage("<?xml version='1.0' encoding='UTF-8'?>" +
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                        " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">" +
                        "   <soapenv:Header/>" +
                        "   <soapenv:Body>" +
                        "      <ser:placeOrder>" +
                        "         <ser:order>" +
                        "            <xsd:price>100</xsd:price>" +
                        "            <xsd:quantity>2000</xsd:quantity>" +
                        "            <xsd:symbol>JMSTransport</xsd:symbol>" +
                        "         </ser:order>" +
                        "      </ser:placeOrder>" +
                        "   </soapenv:Body>" +
                        "</soapenv:Envelope>");
            }
        } finally {
            sender.disconnect();
        }

        Thread.sleep(5000);
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(getJMSBrokerConfiguration());
        try {
            consumer.connect(queueName);
            for (int i = 0; i < 3; i++) {
                if (consumer.popMessage() != null) {
                    org.testng.Assert.fail("JMS Proxy service failed to pick the messages from Queue");
                }
            }
        } finally {
            consumer.disconnect();
        }
    }

    private OMElement createPayload() {   // creation of payload for placeOrder

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ser");
        OMNamespace xsdNs = fac.createOMNamespace("http://services.samples", "xsd");
        OMElement payload = fac.createOMElement("placeOrder", omNs);
        OMElement order = fac.createOMElement("order", omNs);

        OMElement price = fac.createOMElement("price", xsdNs);
        price.setText("10");
        OMElement quantity = fac.createOMElement("quantity", xsdNs);
        quantity.setText("100");
        OMElement symbol = fac.createOMElement("symbol", xsdNs);
        symbol.setText("WSO2");

        order.addChild(price);
        order.addChild(quantity);
        order.addChild(symbol);
        payload.addChild(order);
        return payload;
    }
}
