/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.message.processor.test;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

/**
 * Testing whether a Capp with a deactivated Message Processor is deployed successfully upon ESB server restart.
 */
public class DeactivatedCappMPBehaviourOnRestartTestCase extends ESBIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private SampleAxis2Server axis2Server;
    private static String carFileName = "CappMPServerRestartTestCApp_1.0.0.car";
    private static final String PROXY_SERVICE_NAME = "CappMPServerRestartTestProxy";
    private static final String EXPECTED_ERROR_MESSAGE = "Error occurred while deploying Carbon Application";
    private static final String SUCCESS_MESSAGE = "Deploying Carbon Application : CappMPServerRestartTestCApp_1.0.0.car";
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(context);
        axis2Server = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server.start();
        uploadCapp(carFileName, new DataHandler(new FileDataSource(new File(
                getESBResourceLocation() + File.separator + "car" + File.separator + carFileName))));
        isProxyDeployed(PROXY_SERVICE_NAME);
    }

    @Test(groups = { "wso2.esb" },
          description = "Testing whether a deactivated Message Processor from a capp is deployed successfully upon ESB server restart")
    public void testDeactivatedMPUponServerRestart() throws Exception {

        // Stopping the axis2 Server before sending the client request.
        axis2Server.stop();
        AxisServiceClient client = new AxisServiceClient();
        client.sendRobust(createPlaceOrderRequest(3.141593E0, 4, "IBM"), getProxyServiceURLHttp(PROXY_SERVICE_NAME),
                "placeOrder");

        // Wait till the MP deactivates successfully. MP will try sending the message 4 times before getting deactivated.
        Thread.sleep(15000);

        axis2Server.start();

        // Restart the ESB Server after the MP is deactivated.
        serverConfigurationManager.restartGracefully();

        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        Assert.assertFalse(Utils.checkForLog(logViewerClient, EXPECTED_ERROR_MESSAGE, 10),
                "Error occurred while deploying Carbon Application!");
        Assert.assertTrue(Utils.checkForLog(logViewerClient, SUCCESS_MESSAGE, 10),
                "Unable to deploy Carbon Application!");
    }

    /*
     * Method create the payload required for place orders
     */
    private OMElement createPlaceOrderRequest(double purchPrice, int qty, String symbol) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement placeOrder = factory.createOMElement("placeOrder", ns);
        OMElement order = factory.createOMElement("order", ns);
        OMElement price = factory.createOMElement("price", ns);
        OMElement quantity = factory.createOMElement("quantity", ns);
        OMElement symb = factory.createOMElement("symbol", ns);
        price.setText(Double.toString(purchPrice));
        quantity.setText(Integer.toString(qty));
        symb.setText(symbol);
        order.addChild(price);
        order.addChild(quantity);
        order.addChild(symb);
        placeOrder.addChild(order);
        return placeOrder;
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
        if (axis2Server.isStarted()) {
            axis2Server.stop();
        }
    }

}