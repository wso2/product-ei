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

package org.wso2.carbon.esb.endpoint.test;

import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class HotDeploymentTestCase extends ESBIntegrationTest {

    private final String ENDPOINT_NAME = "hotUnDeploymentEp";
    private EndPointAdminClient endPointAdminClient;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        endPointAdminClient = new EndPointAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        cleanupEndpoints();

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            cleanupEndpoints();
            endPointAdminClient = null;
        } finally {
            super.cleanup();
        }
    }

    @Test(groups = {"wso2.esb", "localonly"}, enabled = false, description = "Test un-deployment of end point form file system delete. Check for the issue at CARBON-8044")
    public void testHotDeployment() throws Exception {
        /*   cleanupEndpoints();*/
        endpointAddition();
        Thread.sleep(5000);
        assertTrue(checkEndpoint(), "Endpoint does not exists");
    }

    @Test(groups = {"wso2.esb", "localonly"}, enabled = false, description = "Test un-deployment of end point form file system delete. Check for the issue at CARBON-8044")
    public void testHotUnDeployment() throws Exception {
        deleteEndpoint();
        Thread.sleep(6000);
        int i = 0;
        while (true) {
            i++;
            if (i >= 60 || checkEndpoint()) {
                break;
            }
            if (verifyFileIsAvailable()) {
                deleteEndpoint();
            }
            Thread.sleep(1000);

        }
        assertFalse(checkEndpoint(), "Endpoint exists even if endpoint deleted from file system");
    }

    private void cleanupEndpoints()
            throws RemoteException, EndpointAdminEndpointAdminException {
        String[] endpointNames = endPointAdminClient.getEndpointNames();
        List endpointList;
        if (endpointNames != null && endpointNames.length > 0 && endpointNames[0] != null) {
            endpointList = Arrays.asList(endpointNames);
            if (endpointList.contains(ENDPOINT_NAME)) {
                endPointAdminClient.deleteEndpoint(ENDPOINT_NAME);
            }
        }
    }

    private void endpointAddition()
            throws Exception {
        int beforeCount = endPointAdminClient.getEndpointCount();

        addEndpoint(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                         "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"" + ENDPOINT_NAME + "\">\n" +
                                         "    <default/>\n" +
                                         "</endpoint>"));
        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(1, afterCount - beforeCount);

    }

    private void deleteEndpoint() throws Exception {

        File f = new File(CarbonBaseUtils.getCarbonHome() + "/repository/deployment/server/synapse-configs/default/endpoints/hotUnDeploymentEp.xml");
        if (f.exists()) {
            f.delete();
        }
    }

    private boolean verifyFileIsAvailable() throws Exception {
        boolean available = false;
        File f = new File(CarbonBaseUtils.getCarbonHome() + "/repository/deployment/server/synapse-configs/default/endpoints/hotUnDeploymentEp.xml");
        if (f.exists()) {
            available = true;
        }
        return available;
    }

    private boolean checkEndpoint() throws EndpointAdminEndpointAdminException, RemoteException {
        String[] endpoints = endPointAdminClient.getEndpointNames();
        if (endpoints[0] != null) {
            List endpointList = Arrays.asList(endpoints);
            return endpointList.contains(ENDPOINT_NAME);
        } else {
            return false;
        }
    }

}

