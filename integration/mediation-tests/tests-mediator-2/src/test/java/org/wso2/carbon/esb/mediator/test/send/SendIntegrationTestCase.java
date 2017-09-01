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

package org.wso2.carbon.esb.mediator.test.send;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClientUtils;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.clients.LoadbalanceFailoverClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SendIntegrationTestCase extends ESBIntegrationTest {
    private static final Log log = LogFactory.getLog(SendIntegrationTestCase.class);

    private LoadbalanceFailoverClient lbClient;
    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;
    private SampleAxis2Server axis2Server3;
    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void initServers() throws Exception {
        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");
        axis2Server3 = new SampleAxis2Server("test_axis2_server_9003.xml");

        axis2Server1.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server1.deployService(SampleAxis2Server.LB_SERVICE_1);

        axis2Server2.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server2.deployService(SampleAxis2Server.LB_SERVICE_2);

        axis2Server3.deployService(SampleAxis2Server.LB_SERVICE_3);

        setEnvironment();

    }

    private void setEnvironment()
            throws Exception {
        axis2Server1.start();
        axis2Server2.start();
        axis2Server3.start();

        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "sendMediatorConfig" + File.separator + "synapse.xml");

        lbClient = new LoadbalanceFailoverClient();

        //Test weather all the axis2 servers are up and running
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE)
                , null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
        response = axis2Client.sendSimpleStockQuoteRequest("http://localhost:9001/services/SimpleStockQuoteService", null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
        response = axis2Client.sendSimpleStockQuoteRequest("http://localhost:9002/services/SimpleStockQuoteService", null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        resourceAdminServiceClient = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
, context.getContextTenant().getContextUser().getPassword());
        uploadResourcesToConfigRegistry();

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        log.info("Tests Are Completed");
        if (axis2Server1.isStarted()) {
            axis2Server1.stop();
        }
        if (axis2Server2.isStarted()) {
            axis2Server2.stop();
        }
        if (axis2Server3.isStarted()) {
            axis2Server3.stop();
        }
        resourceAdminServiceClient.deleteResource("/_system/config/test_sequences_config");
        resourceAdminServiceClient.deleteResource("/_system/local/test_sequences_local");
        resourceAdminServiceClient.deleteResource("/_system/governance/test_sequences_gov");
        axis2Server1 = null;
        axis2Server2 = null;
        axis2Server3 = null;
        resourceAdminServiceClient = null;
        lbClient = null;
        super.cleanup();
    }

    @AfterMethod(groups = "wso2.esb")
    public void startServersA() throws InterruptedException, IOException {
        if (!axis2Server1.isStarted()) {
            axis2Server1.start();
        }
        if (!axis2Server2.isStarted()) {
            axis2Server2.start();
        }
        if (!axis2Server3.isStarted()) {
            axis2Server3.start();
        }
        Thread.sleep(1000);
    }

    @BeforeMethod(groups = "wso2.esb")
    public void startServersB() throws InterruptedException, IOException {
        if (!axis2Server1.isStarted()) {
            axis2Server1.start();
        }
        if (!axis2Server2.isStarted()) {
            axis2Server2.start();
        }
        if (!axis2Server3.isStarted()) {
            axis2Server3.start();
        }
        Thread.sleep(1000);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint")
    public void testSendingAddressEndpoint() throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint"),
                                                                     null, "WSO2");

        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint")
    public void testSendingDefaultEndpoint() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint")
    public void testSendingWSDLEndpoint() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint")
    public void testSendingFailOverEndpoint() throws Exception, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failoverEndPointBM"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failoverEndPointBM"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failoverEndPointBM"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint Build Message Before Sending")
    public void testSendingAddressEndpoint_BuildMessage() throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPointBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Build Message Before Sending")
    public void testSendingDefaultEndpoint_BuildMessage() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPointBM"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Build Message Before Sending")
    public void testSendingWSDLEndpoint_BuildMessage() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPointBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Build Message Before Sending")
    public void testSendingFailOverEndpoint_BuildMessage()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failoverEndPointBM"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failoverEndPointBM"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failoverEndPointBM"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint while Receiving Sequence in Local Registry")
    public void testDynamicAddressEndpointSequence_LocalReg() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_LocalReg"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint while Receiving Sequence in Config Registry")
    public void testSendingAddressEndpoint_Receiving_Sequence_ConfigReg() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_ConfigReg"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint while Receiving Sequence in Governance  Registry")
    public void testSendingAddressEndpoint_Receiving_Sequence_GovReg() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_GovReg"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence in Local Registry")
    public void testSendingDefaultEndpoint_Receiving_Sequence_LocalReg() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_LocalReg"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence  in Config Registry")
    public void testSendingDefaultEndpoint_Receiving_Sequence_ConfigReg() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_ConfigReg"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence in Governance Registry")
    public void testSendingDefaultEndpoint_Receiving_Sequence_GovReg() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_GovReg"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence in Local Registry")
    public void testSendingWSDLEndpoint_Receiving_Sequence_LocalReg() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_LocalReg"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence in Config Registry")
    public void testSendingWSDLEndpoint_Receiving_Sequence_ConfigReg() throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_ConfigReg"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence in Governance Registry")
    public void testSendingWSDLEndpoint_Receiving_Sequence_GovReg() throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_GovReg"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence in Local Registry")
    public void testSendingFailOverEndpoint_Receiving_Sequence_LocalReg()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_LocalReg"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_LocalReg"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_LocalReg"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence in Config Registry")
    public void testSendingFailOverEndpoint_Receiving_Sequence_ConfigReg()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_ConfigReg"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_ConfigReg"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_ConfigReg"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence in Gov Registry")
    public void testSendingFailOverEndpoint_Receiving_Sequence_GovReg()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_GovReg"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_GovReg"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_GovReg"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint Receiving Sequence in Local Registry while BuildMessage enabled")
    public void testDynamicAddressEndpoint_Receiving_Sequence_LocalReg_BuildMessage()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_LocalRegBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint Receiving Sequence in Config Registry while BuildMessage enabled")
    public void testDynamicAddressEndpoint_Receiving_Sequence__ConfigReg_BuildMessage()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_ConfigRegBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint Receiving Sequence in Governance  Registry while BuildMessage enabled")
    public void testDynamicAddressEndpoint_Receiving_Sequence_GovReg_BuildMessage()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_GovRegBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence in Local Registry while BuildMessage enabled")
    public void testSendingDefaultEndpoint_Receiving_Sequence_LocalReg_BuildMessage()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_LocalRegBM"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence in Config Registry while BuildMessage enabled")
    public void testSendingDefaultEndpoint_Receiving_Sequence_ConfigReg_BuildMessage()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_ConfigRegBM"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence in Governance Registry while BuildMessage enabled")
    public void testSendingDefaultEndpoint_Receiving_Sequence_GovReg_BuildMessage()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_GovRegBM"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence in Local Registry while BuildMessage enabled")
    public void testSendingWSDLEndpoint_Receiving_Sequence_LocalReg_BuildMessage()
            throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_LocalRegBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence in Config Registry while BuildMessage enabled")
    public void testSendingWSDLEndpoint_Receiving_Sequence_ConfigReg_BuildMessage()
            throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_ConfigRegBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence in Governance Registry while BuildMessage enabled")
    public void testSendingWSDLEndpoint_Receiving_Sequence_GovReg_BuildMessage()
            throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_GovRegBM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence in Local Registry while BuildMessage enabled")
    public void testSendingFailOverEndpoint_Receiving_Sequence_LocalReg_BuildMessage()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_LocalRegBM"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_LocalRegBM"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_LocalRegBM"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence in Config Registry while BuildMessage enabled")
    public void testSendingFailOverEndpoint_Receiving_Sequence_ConfigReg_BuildMessage()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_ConfigRegBM"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_ConfigRegBM"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_ConfigRegBM"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence in Gov Registry while BuildMessage enabled")
    public void testSendingFailOverEndpoint_Receiving_Sequence_GovReg_BuildMessage()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_GovRegBM"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_GovRegBM"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_GovRegBM"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    // Receiving Sequence Dynamic Build Message= False
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint Receiving Sequence Dynamic")
    public void testSendingAddressEndpoint_Receiving_Sequence_Dynamic() throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_Dynamic"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence Dynamic")
    public void testSendingDefaultEndpoint_Receiving_Sequence_Dynamic() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_Dynamic"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence Dynamic")
    public void testSendingWSDLEndpoint_Receiving_Sequence_Dynamic() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_Dynamic"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence Dynamic")
    public void testSendingFailOverEndpoint_Receiving_Sequence_Dynamic()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_Dynamic"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_Dynamic"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_Dynamic"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    // Receiving Sequence Dynamic Build Message= True
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Address Endpoint Receiving Sequence Dynamic Build Message")
    public void testSendingAddressEndpoint_Receiving_Sequence_Dynamic_BM() throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("addressEndPoint_Receiving_Sequence_Dynamic_BM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Default Endpoint Receiving Sequence Dynamic Build Message")
    public void testSendingDefaultEndpoint_Receiving_Sequence_Dynamic_BM() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("defaultEndPoint_Receiving_Sequence_Dynamic_BM"),
                                                                     getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to WSDL Endpoint Receiving Sequence Dynamic Build Message")
    public void testSendingWSDLEndpoint_Receiving_Sequence_Dynamic_BM() throws IOException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("wsdlEndPoint_Receiving_Sequence_Dynamic_BM"),
                                                                     null, "WSO2");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("WSO2 Company"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Fail Over Endpoint Receiving Sequence Dynamic Build Message")
    public void testSendingFailOverEndpoint_Receiving_Sequence_Dynamic_BM()
            throws IOException, InterruptedException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_Dynamic_BM"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.stop();
        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_Dynamic_BM"),
                                                           null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2 Company"));

        axis2Server1.start();
        axis2Server2.stop();

        Thread.sleep(2000);

        int counter = 0;
        while (!AxisServiceClientUtils.isServiceAvailable("http://localhost:9001/services/SimpleStockQuoteService")) {
            if (counter > 100) {
                break;
            }
            counter++;
        }

        if (counter > 100) {
            throw new AssertionError("Axis2 Server didn't started with in expected time period.");
        } else {

            response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("failOverEndPoint_Receiving_Sequence_Dynamic_BM"),
                                                               null, "WSO2");
            Assert.assertTrue(response.toString().contains("WSO2 Company"));

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm")
    public void testSendingLoadBalancingEndpoint1() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint1"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint1"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint1"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint1"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint1"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm")
    public void testSendingLoadBalancingEndpoint2() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint2"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint2"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint2"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint2"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint3() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint3"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint3"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint3"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint3"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint3"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint4() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint4"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint4"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint4"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint4"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm Receiving Sequence in Conf Registry while BuildMessage Disabled")
    public void testSendingLoadBalancingEndpoint5() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint5"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint5"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint5"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint5"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint5"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm Receiving Sequence in Conf Registry while BuildMessage Disabled")
    public void testSendingLoadBalancingEndpoint6() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint6"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint6"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint6"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint6"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm Receiving Sequence in Conf Registry while BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint7() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint7"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint7"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint7"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint7"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint7"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm Receiving Sequence in Conf Registry while BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint8() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint8"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint8"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint8"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint8"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm Receiving Sequence in Gov Registry while BuildMessage Disabled")
    public void testSendingLoadBalancingEndpoint9() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint9"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint9"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint9"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint9"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint9"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm Receiving Sequence in Gov Registry while BuildMessage Disabled")
    public void testSendingLoadBalancingEndpoint10() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint10"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint10"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint10"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint10"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm Receiving Sequence in Gov Registry while BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint11() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint11"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint11"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint11"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint11"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint11"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm Receiving Sequence in Gov Registry while BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint12() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint12"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint12"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint12"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint12"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm Receiving Sequence Dynamic BuildMessage Disabled")
    public void testSendingLoadBalancingEndpoint13() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint13"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint13"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint13"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint13"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint13"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm Receiving Sequence Dynamic BuildMessage Disabled")
    public void testSendingLoadBalancingEndpoint14() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint14"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint14"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint14"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint14"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the WeightedRoundRobin Algorithm Receiving Sequence Dynamic BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint15() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint15"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint15"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint15"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint15"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint15"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = "wso2.esb", description = "Test sending request to Load Balancing Endpoint With the RoundRobin Algorithm Receiving Sequence Dynamic BuildMessage Enabled")
    public void testSendingLoadBalancingEndpoint16() throws IOException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint16"),
                                                          "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint16"),
                                                   "http://localhost:9002/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint16"),
                                                   "http://localhost:9003/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadBalancingEndPoint16"),
                                                   "http://localhost:9001/services/LBService1");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

    }

    /**
     * This method will upload all the dynamic sequences to Local, Config and Governance Registries of the ESB
     * These files will be kept in different t directories in the Registries
     *
     * @throws Exception when the upload is failed.
     */
    private void uploadResourcesToConfigRegistry() throws Exception {

        resourceAdminServiceClient.addCollection("/_system/config/", "test_sequences_config", "",
                                                 "Contains test Sequence files");
        resourceAdminServiceClient.addCollection("/_system/governance/", "test_sequences_gov", "",
                                                 "Contains test Sequence files");
        resourceAdminServiceClient.addCollection("/_system/local/", "test_sequences_local", "",
                                                 "Contains test Sequence files");
        resourceAdminServiceClient.addResource(
                "/_system/config/test_sequences_config/receivingSequence_Conf.xml", "application/vnd.wso2.sequence", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/sendMediatorConfig/test_sequences_config/receivingSequence_Conf.xml").getPath())));
        Thread.sleep(1000);
        resourceAdminServiceClient.addResource(
                "/_system/local/test_sequences_local/receivingSequence_Local.xml", "application/vnd.wso2.sequence", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/sendMediatorConfig/test_sequences_local/receivingSequence_Local.xml").getPath())));
        Thread.sleep(1000);
        resourceAdminServiceClient.addResource(
                "/_system/governance/test_sequences_gov/receivingSequence_Gov.xml", "application/vnd.wso2.sequence", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/sendMediatorConfig/test_sequences_gov/receivingSequence_Gov.xml").getPath())));
        Thread.sleep(1000);

        resourceAdminServiceClient.addResource(
                "/_system/governance/test_sequences_gov/receivingSequence_Gov.xml", "application/vnd.wso2.sequence", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/synapseconfig/sendMediatorConfig/test_sequences_gov/receivingSequence_Gov.xml").getPath())));
        Thread.sleep(1000);
    }


}

