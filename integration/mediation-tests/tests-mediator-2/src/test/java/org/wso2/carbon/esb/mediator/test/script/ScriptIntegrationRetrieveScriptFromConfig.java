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

package org.wso2.carbon.esb.mediator.test.script;


import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class ScriptIntegrationRetrieveScriptFromConfig extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        String filePath = "/artifacts/ESB/synapseconfig/script_mediator/synapse3.xml";
        loadESBConfigurationFromClasspath(filePath);
        uploadResourcesToConfigRegistry();


    }


    @Test(groups = "wso2.esb", description = "Tests ScriptMediator retrieve script from config registry")
    public void testRetrieveKey() throws Exception {
        OMElement response;

        response = axis2Client.sendCustomQuoteRequest(getMainSequenceURL(), null, "IBM");

        assertNotNull(response, "Response message null");
        assertNotNull(response.getQName().getLocalPart(), "Fault response null localpart");
        assertEquals(response.getQName().getLocalPart(), "CheckPriceResponse",
                     "Fault localpart mismatched");

        assertNotNull(response.getFirstElement().getQName().getLocalPart(),
                      " Fault response null localpart");
        assertEquals(response.getFirstElement().getQName().getLocalPart(), "Code",
                     "Fault localpart mismatched");
        assertEquals(response.getFirstElement().getText(), "IBM", "Fault value mismatched");


        assertNotNull(response.getFirstChildWithName(
                new QName("http://services.samples/xsd", "Price")), "Fault response null localpart");
    }


    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    private void uploadResourcesToConfigRegistry() throws Exception {
        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(),
                                               context.getContextTenant().getContextUser().getUserName(), context.getContextTenant().getContextUser().getPassword());
        resourceAdminServiceStub.deleteResource("/_system/config/script_key");

        resourceAdminServiceStub.addCollection("/_system/config/", "script_key", "",
                                               "Contains test js files");
        resourceAdminServiceStub.addResource("/_system/config/script_key/stockquoteTransform.js",
                                             "application/x-javascript", "js file",
                                             new DataHandler(new URL("file:///"
                                                                     + getESBResourceLocation() +
                                                                     "/mediatorconfig/script/stockquoteTransform.js")));
    }
}
