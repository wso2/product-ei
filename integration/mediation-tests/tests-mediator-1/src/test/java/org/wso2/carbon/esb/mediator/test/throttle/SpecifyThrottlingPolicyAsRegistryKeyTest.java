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
package org.wso2.carbon.esb.mediator.test.throttle;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class SpecifyThrottlingPolicyAsRegistryKeyTest extends ESBIntegrationTest {

    private static final int THROTTLE_MAX_MSG_COUNT = 4;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/throttle/specifyThrottlingPolicyAsARegistryKey.xml");
    }

    @Test(groups = "wso2.esb",
          description = "Specified throttling policy as a registry key",
          timeOut = 1000 * 60 * 2)
    public void testThrottlingPolicyFromRegistry() throws Exception {
        int throttleCounter = 0;
        OMElement response = null;

        try {
            for (int i = 0; i <= THROTTLE_MAX_MSG_COUNT; i++) {
                response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
                assertTrue(response.toString().contains("WSO2"), "Fault: Required response not found.");
                throttleCounter++;
            }
            if (throttleCounter > THROTTLE_MAX_MSG_COUNT) {
                assertFalse(response.toString().contains("WSO2"), "Fault: Required response not found.replying service");
            } else {
                fail("Fault: Throttling response count does not match");
            }
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("**Access Denied**"), "Fault: value mismatched, should be '**Access Denied**'");
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            clearUploadedResource();
        } finally {
            super.cleanup();
        }
    }

    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/policy");
        resourceAdminServiceStub.addCollection("/_system/config/", "policy", "",
                                               "Contains throttle policy files");

        resourceAdminServiceStub.addResource(
                "/_system/config/policy/throttle_policy.xml", "application/xml", "throttle policy files",
                new DataHandler(new URL("file:///" + getESBResourceLocation() + "/mediatorconfig/policy/throttle_policy.xml")));


    }

    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/policy");
        Thread.sleep(1000);
    }
}
