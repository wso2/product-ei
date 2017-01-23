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

import org.apache.axiom.om.OMElement;
import org.testng.annotations.Test;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.esb.endpoint.test.util.EndpointTestUtils;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;

public class DynamicInlinedWSDLEpTestCase extends ESBIntegrationTest {
    private EndPointAdminClient endPointAdminClient;
    private final String ENDPOINT_PATH_1 = "conf:/DynamicInlineWSDLEndpointConf";
    private final String ENDPOINT_PATH_2 = "gov:/DynamicInlineWSDLEndpointGov";


    @Test(groups = {"wso2.esb"}, enabled = false)
    public void testDynamicInlineWSDLEndpoint() throws Exception {
        endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        OMElement endpoint_xml = new ESBTestCaseUtils().loadResource("/artifacts/ESB/endpoint/wsdlTestEp.xml");
        cleanupEndpoints();

        dynamicEndpointAdditionScenario(ENDPOINT_PATH_1, endpoint_xml);
        dynamicEndpointAdditionScenario(ENDPOINT_PATH_2, endpoint_xml);

        dynamicEndpointDeletionScenario(ENDPOINT_PATH_1);
        dynamicEndpointDeletionScenario(ENDPOINT_PATH_2);
        endpoint_xml = null;
        endPointAdminClient = null;

    }

    private void cleanupEndpoints()
            throws RemoteException, EndpointAdminEndpointAdminException {
        EndpointTestUtils.cleanupDynamicEndpoint(ENDPOINT_PATH_1, endPointAdminClient);
        EndpointTestUtils.cleanupDynamicEndpoint(ENDPOINT_PATH_2, endPointAdminClient);

    }

    private void dynamicEndpointAdditionScenario(String path, OMElement endpointElm)
            throws IOException, EndpointAdminEndpointAdminException, XMLStreamException {
        int beforeCount = endPointAdminClient.getDynamicEndpointCount();
        endPointAdminClient.addDynamicEndPoint(path, endpointElm);
        EndpointTestUtils.assertDynamicEndpointAddition(path, beforeCount, endPointAdminClient);
    }

    private void dynamicEndpointDeletionScenario(String path)
            throws RemoteException, EndpointAdminEndpointAdminException {
        int beforeCount = endPointAdminClient.getDynamicEndpointCount();
        endPointAdminClient.deleteDynamicEndpoint(path);
        EndpointTestUtils.assertDynamicEndpointDeletion(beforeCount, endPointAdminClient);
    }


}

