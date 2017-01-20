/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.endpoint.test;

import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.esb.endpoint.test.util.EndpointTestUtils;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.LoadbalanceFailoverClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class LoadBalanceEndpointTestCase extends ESBIntegrationTest {

    private final String ENDPOINT_NAME = "lbEpTest";
    private EndPointAdminClient endPointAdminClient;
    private ResourceAdminServiceClient resourceAdminServiceClient;
    private SampleAxis2Server axis2Server1;
    private SampleAxis2Server axis2Server2;
    private SampleAxis2Server axis2Server3;
    private LoadbalanceFailoverClient lbClient;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "endpoint" + File.separator + "loadbalancingEndpointConfig" + File.separator + "synapse.xml");

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server2 = new SampleAxis2Server("test_axis2_server_9002.xml");
        axis2Server3 = new SampleAxis2Server("test_axis2_server_9003.xml");

        axis2Server1.deployService(SampleAxis2Server.LB_SERVICE_1);

        axis2Server2.deployService(SampleAxis2Server.LB_SERVICE_2);

        axis2Server3.deployService(SampleAxis2Server.LB_SERVICE_3);
        axis2Server1.start();

        axis2Server2.start();
        axis2Server3.start();

        endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        lbClient = new LoadbalanceFailoverClient();
        resourceAdminServiceClient = new ResourceAdminServiceClient
                (context.getContextUrls().getBackEndUrl(), getSessionCookie());

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
        resourceAdminServiceClient.deleteResource("/_system/config/test_ep_config/");
        axis2Server1 = null;
        axis2Server2 = null;
        axis2Server3 = null;
        resourceAdminServiceClient = null;
        endPointAdminClient = null;
        lbClient = null;
        super.cleanup();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"})
    public void testLoadBalanceEndpoint() throws Exception {
        endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());

        cleanupEndpoints();
        endpointAdditionScenario();
        endpointStatisticsScenario();
        endpointDeletionScenario();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a Message to a loadbalancing endpoint")
    public void testSendingToLoaBalancingEndpoint()
            throws IOException, EndpointAdminEndpointAdminException,
                   LoginAuthenticationExceptionException,
                   XMLStreamException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadbalancingEndPoint"),
                                                          null);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadbalancingEndPoint"),
                                                   null);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadbalancingEndPoint"),
                                                   null);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a Message to a loadbalancing endpoint in Config Reg")
    public void testSendingToLoaBalancingEndpoint_ConfigReg()
            throws IOException, EndpointAdminEndpointAdminException,
                   LoginAuthenticationExceptionException,
                   XMLStreamException {

        String response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadbalancingEndPoint_Config_Reg"),
                                                          null);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_1"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadbalancingEndPoint_Config_Reg"),
                                                   null);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_2"));

        response = lbClient.sendLoadBalanceRequest(getProxyServiceURLHttp("loadbalancingEndPoint_Config_Reg"),
                                                   null);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("Response from server: Server_3"));

    }

    private void cleanupEndpoints() throws RemoteException, EndpointAdminEndpointAdminException {
        EndpointTestUtils.cleanupDefaultEndpoint(ENDPOINT_NAME, endPointAdminClient);
    }

    private void endpointAdditionScenario()
            throws Exception {
        int beforeCount = endPointAdminClient.getEndpointCount();

        addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                         "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + ENDPOINT_NAME + "\">\n" +
                                         "    <loadbalance algorithm=\"org.apache.synapse.endpoints.algorithms.RoundRobin\">\n" +
                                         "        <endpoint name=\"endpoint_urn_uuid_7E71CCD625D839E55A25548428059450-1123062013\">\n" +
                                         "            <address uri=\"http://webservices.amazon.com/AWSECommerceService/UK/AWSECommerceService.wsdl\"/>\n" +
                                         "        </endpoint>\n" +
                                         "    </loadbalance>\n" +
                                         "</endpoint>"));

        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(1, afterCount - beforeCount);

        String[] endpoints = endPointAdminClient.getEndpointNames();
        if (endpoints != null && endpoints.length > 0 && endpoints[0] != null) {
            List endpointList = Arrays.asList(endpoints);
            assertTrue(endpointList.contains(ENDPOINT_NAME));
        } else {
            fail("Endpoint has not been added to the system properly");
        }
    }

    private void endpointStatisticsScenario() {
        try {
            endPointAdminClient.enableEndpointStatistics(ENDPOINT_NAME);
        } catch (EndpointAdminEndpointAdminException e) {
            return;
        } catch (RemoteException e) {
            return;
        }
        fail("Enabling statistics on a load-balance endpoint did not cause an error");
    }

    private void endpointDeletionScenario()
            throws RemoteException, EndpointAdminEndpointAdminException {
        int beforeCount = endPointAdminClient.getEndpointCount();
        endPointAdminClient.deleteEndpoint(ENDPOINT_NAME);
        EndpointTestUtils.assertDefaultEndpointDeletion(beforeCount, endPointAdminClient);

    }

    private void uploadResourcesToConfigRegistry() throws Exception {

        resourceAdminServiceClient.addCollection("/_system/config/", "test_ep_config", "",
                                                 "Contains test Default EP files");
        resourceAdminServiceClient.addResource(
                "/_system/config/test_ep_config/loadbalancingEP_Test.xml", "application/xml", "xml files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/endpoint/loadbalancingEndpointConfig/loadbalancingEP_Test.xml").getPath())));
        Thread.sleep(1000);

    }
}
