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

package org.wso2.carbon.esb.mediator.test.fastXslt;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.esb.integration.common.clients.registry.PropertiesAdminServiceClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import javax.activation.DataHandler;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


public class DynamicKeyFastXsltTransformationTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        uploadResourcesToRegistry();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fast_xslt/fast_xslt_dynamic_key_synapse.xml");
    }

    @Test(groups = {"wso2.esb"},
            description = "Do Fast fastXSLT transformation by Select the key type as dynamic key and retrieve" +
                    " the transformation from that.")
    public void fastXsltTransformationFromDynamicKeyTest() throws Exception {

        OMElement response;
        response = axis2Client.sendCustomQuoteRequest(
                getMainSequenceURL(),
                null,
                "WSO2");
        assertNotNull(response, "Response message null");
        assertTrue(response.toString().contains("Code"));
        assertTrue(response.toString().contains("WSO2"));

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        try {
            clearRegistry();
        } finally {
            super.cleanup();
        }

    }

    private void uploadResourcesToRegistry() throws Exception {
        ResourceAdminServiceClient resourceAdminServiceClient =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        PropertiesAdminServiceClient propertiesAdminServiceClient =
                new PropertiesAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceClient.deleteResource("/_system/config/localEntries");
        resourceAdminServiceClient.addCollection("/_system/config/", "localEntries", "",
                "Contains dynamic sequence request entry");

        resourceAdminServiceClient.addResource(
                "/_system/config/localEntries/request_transformation.txt", "text/plain", "text files",
                new DataHandler("Dynamic Sequence request transformation".getBytes(), "application/text"));
        propertiesAdminServiceClient.setProperty("/_system/config/localEntries/request_transformation.txt",
                "resourceName", "request_transform.xslt");

        resourceAdminServiceClient.deleteResource("/_system/governance/localEntries");
        resourceAdminServiceClient.addCollection("/_system/governance/", "localEntries", "",
                "Contains dynamic sequence response entry");
        resourceAdminServiceClient.addResource(
                "/_system/governance/localEntries/response_transformation_back.txt", "text/plain", "text files",
                new DataHandler("Dynamic Sequence response transformation".getBytes(), "application/text"));
        propertiesAdminServiceClient.setProperty("/_system/governance/localEntries/response_transformation_back.txt",
                "resourceName", "response_transform.xslt");

        Thread.sleep(1000);
    }

    private void clearRegistry() throws Exception {
        ResourceAdminServiceClient resourceAdminServiceClient =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceClient.deleteResource("/_system/config/localEntries");

        resourceAdminServiceClient.deleteResource("/_system/governance/localEntries");
    }
}
