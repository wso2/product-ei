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
package org.wso2.carbon.esb.samples.test.messaging;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

public class Sample705TestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server1 = null;
    private SampleAxis2Server axis2Server2 = null;
    private SampleAxis2Server axis2Server3 = null;


    @BeforeClass(alwaysRun = true)
    public void startJMSBrokerAndConfigureESB() throws Exception {
        super.init();
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        super.init();
        loadSampleESBConfiguration(705);

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");
        axis2Server3 = new SampleAxis2Server("test_axis2_server_9003.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server1.start();

        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server2.start();

        axis2Server3.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server3.start();

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        try {
            //reverting the changes done to esb sever
            Thread.sleep(10000); //let server to clear the artifact undeployment

            axis2Server1.stop();
            axis2Server2.stop();
            axis2Server3.stop();


        } finally {
            super.cleanup();
        }

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = { "wso2.esb" }, description = "Test forwarding with load balancing")
    public void loadBalancingTest() throws Exception {

        ServiceClient serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getProxyServiceURLHttp("StockQuoteProxy")));
        options.setAction("urn:placeOrder");
        serviceClient.setOptions(options);

        for (int i = 0; i < 100; i++ ) {
            serviceClient.sendRobust(createPayload());
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
