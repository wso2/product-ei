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
package org.wso2.carbon.esb.samples.test.messaging.store;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class Sample704TestCase extends ESBIntegrationTest {

    private MessageStoreAdminClient messageStoreAdminClient;
    private final String MESSAGE_STORE_NAME = "MSG_STORE";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadSampleESBConfiguration(704);

        messageStoreAdminClient =
                new MessageStoreAdminClient(contextUrls.getBackEndUrl(),
                        getSessionCookie());
    }

    @Test(groups = { "wso2.esb" }, description = "RESTful Invocations with Message Forwarding" +
            " Processor test case")
    public void messageStoreFIXStoringTest() throws Exception {

        ServiceClient serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getBackEndServiceUrl("StockQuoteProxy")));
        options.setAction("urn:placeOrder");
        options.setProperty(Constants.Configuration.TRANSPORT_URL, getMainSequenceURL());
        serviceClient.setOptions(options);

        for (int i = 0; i < 10; i++) {
            serviceClient.sendRobust(createPayload());
        }

        Thread.sleep(2000);
        Assert.assertTrue(messageStoreAdminClient.getMessageCount(MESSAGE_STORE_NAME) == 0,
                "Messages are stored");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
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
