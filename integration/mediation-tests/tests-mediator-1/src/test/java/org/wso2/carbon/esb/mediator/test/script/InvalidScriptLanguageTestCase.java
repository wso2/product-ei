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

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class InvalidScriptLanguageTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadResourcesToConfigRegistry();

    }


    @Test(groups = "wso2.esb", description = "Give an invalid scripting language name (jh instead of js) . it throws an Exception")
    public void testSequenceJSMediatorWithInvalidScriptLanguage() throws Exception {
        try {
            loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/config66/synapse.xml");
            Assert.fail("This Configuration can not be saved successfully due to invalid Script language");
        } catch (AxisFault expected) {
            assertTrue((expected.getMessage().contains(ESBTestConstant.ERROR_ADDING_SEQUENCE) || expected.getMessage().contains(ESBTestConstant.UNABLE_TO_SAVE_SEQUENCE))
                    , "Error Message Mismatched. actual:" + expected.getMessage() + " but expected: Error adding sequence or Unable to save the Sequence");
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        clearUploadedResource();
        super.cleanup();
    }


    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/script_js");
        resourceAdminServiceStub.addCollection("/_system/config/", "script_js", "",
                                               "Contains test js files");

        resourceAdminServiceStub.addResource(
                "/_system/config/script_js/stockquoteTransform.js", "application/x-javascript", "js files",
                new DataHandler(new URL("file:///" + getESBResourceLocation() +
                                        "/mediatorconfig/script_js/stockquoteTransform.js")));

    }

    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/script_js");

        Thread.sleep(1000);
    }
}
