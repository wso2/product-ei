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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.esb.endpoint.test.util.EndpointTestUtils;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;

public class DynamicLoadBalanceEpTestCase extends ESBIntegrationTest {
    private EndPointAdminClient endPointAdminClient;
    private final String ENDPOINT_PATH_1 = "conf:/DynamicLBEndpointConf";
    private final String ENDPOINT_PATH_2 = "gov:/DynamicLBEndpointGov";
    private final String ENDPOINT_XML = "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"anonymous\">\n" +
                                               "   <loadbalance algorithm=\"org.apache.synapse.endpoints.algorithms.RoundRobin\">\n" +
                                               "      <endpoint>\n" +
                                               "         <address uri=\"http://webservices.amazon.com/AWSECommerceService/UK/AWSECommerceService.wsdl\">\n" +
                                               "            <suspendOnFailure>\n" +
                                               "               <progressionFactor>1.0</progressionFactor>\n" +
                                               "            </suspendOnFailure>\n" +
                                               "            <markForSuspension>\n" +
                                               "               <retriesBeforeSuspension>0</retriesBeforeSuspension>\n" +
                                               "               <retryDelay>0</retryDelay>\n" +
                                               "            </markForSuspension>\n" +
                                               "         </address>\n" +
                                               "      </endpoint>\n" +
                                               "   </loadbalance>\n" +
                                               "</endpoint>";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        cleanupEndpoints();
    }

    @Test(groups = {"wso2.esb"})
    public void testDynamicLoadBalanceEndpoint() throws Exception {
        dynamicEndpointAdditionScenario(ENDPOINT_PATH_1);
        dynamicEndpointAdditionScenario(ENDPOINT_PATH_2);

        dynamicEndpointDeletionScenario(ENDPOINT_PATH_1);
        dynamicEndpointDeletionScenario(ENDPOINT_PATH_2);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        endPointAdminClient = null;
        super.cleanup();
    }

    private void cleanupEndpoints()
            throws RemoteException, EndpointAdminEndpointAdminException {
        EndpointTestUtils.cleanupDynamicEndpoint(ENDPOINT_PATH_1, endPointAdminClient);
        EndpointTestUtils.cleanupDynamicEndpoint(ENDPOINT_PATH_2, endPointAdminClient);
    }

    private void dynamicEndpointAdditionScenario(String path)
            throws IOException, EndpointAdminEndpointAdminException, XMLStreamException {
        int beforeCount = endPointAdminClient.getDynamicEndpointCount();
        endPointAdminClient.addDynamicEndPoint(path, AXIOMUtil.stringToOM(ENDPOINT_XML));
        EndpointTestUtils.assertDynamicEndpointAddition(path, beforeCount, endPointAdminClient);

    }

    private void dynamicEndpointDeletionScenario(String path)
            throws RemoteException, EndpointAdminEndpointAdminException {
        int beforeCount = endPointAdminClient.getDynamicEndpointCount();
        endPointAdminClient.deleteDynamicEndpoint(path);
        EndpointTestUtils.assertDynamicEndpointDeletion(beforeCount, endPointAdminClient);
    }


}
