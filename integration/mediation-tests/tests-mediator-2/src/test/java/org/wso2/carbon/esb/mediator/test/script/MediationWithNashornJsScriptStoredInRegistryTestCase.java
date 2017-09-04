/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.net.URL;
import java.rmi.RemoteException;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test case verifies that, mediation with NashornJs script which is stored in registry happens correctly by
 * invoking the script with the given 'key'.
 */
public class MediationWithNashornJsScriptStoredInRegistryTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToConfigRegistry();
    }

    @Test(groups = "wso2.esb", description = "Mediate with NashornJs script which is stored in registry by "
            + "invoking it with the given 'key'")
    public void testJSMediatorWithTheGivenKey() throws Exception {

        OMElement response = axis2Client.sendCustomQuoteRequest(
                getProxyServiceURLHttp("scriptMediatorNashornJsRetrieveFromRegistryTestProxy"), null,
                "NashornInvokeFromRegistry");
        assertNotNull(response, "Fault response message null");
        assertNotNull(response.getQName().getLocalPart(), "Fault response null localpart");
        assertEquals(response.getQName().getLocalPart(), "CheckPriceResponse", "Fault localpart mismatched");
        assertNotNull(response.getFirstElement().getQName().getLocalPart(), " Fault response null localpart");
        assertEquals(response.getFirstElement().getQName().getLocalPart(), "Code", "Fault localpart mismatched");
        assertEquals(response.getFirstElement().getText(), "NashornInvokeFromRegistry", "Fault value mismatched");
        assertNotNull(response.getFirstChildWithName(new QName("http://services.samples/xsd", "Price")), "Fault "
                + "response null localpart");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        clearUploadedResource();
    }

    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub = new ResourceAdminServiceClient(contextUrls
                .getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(), context.getContextTenant
                ().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/script_js");
        resourceAdminServiceStub.addCollection("/_system/config/", "script_js", "", "Contains test js files");
        resourceAdminServiceStub.addResource("/_system/config/script_js/stockquoteTransformNashorn.js",
                "application/x-javascript", "js files", new DataHandler(new URL("file:///" + getESBResourceLocation()
                        + "/mediatorconfig/script_js/stockquoteTransformNashorn.js")));
    }

    private void clearUploadedResource() throws InterruptedException, ResourceAdminServiceExceptionException,
            RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub = new ResourceAdminServiceClient(contextUrls
                .getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(), context.getContextTenant
                ().getContextUser().getPassword());
        resourceAdminServiceStub.deleteResource("/_system/config/script_js");
    }
}
