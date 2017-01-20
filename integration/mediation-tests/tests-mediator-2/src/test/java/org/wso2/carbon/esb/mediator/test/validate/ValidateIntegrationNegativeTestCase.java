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
package org.wso2.carbon.esb.mediator.test.validate;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ValidateIntegrationNegativeTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadConfig();
    }


    @Test(groups = {"wso2.esb"}, description = "Provide invalid dynamic key as shema location")
    public void TestWithInvalidDynamicKey() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/invalid_dynamic_key.xml");
        OMElement response = null;
        try {
            response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , null, "WSO2");
            fail("This Request must throws a AxisFault");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request");
        }
    }

    @Test(groups = {"wso2.esb"}, description = "Create validate mediator and specifying an invalid " +
                                               "XPath expression using \"source\" attribute " +
                                               "Check how mediator operates on the elements of SOAP body")
    public void TestWithInvalidXpath() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/validate_with_invalid_Xpath.xml");

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , null, "WSO2");
        } catch (AxisFault expected) {
            assertEquals(expected.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL, "Error Message mismatched");
        }
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        clearUploadedResource();
        super.cleanup();
    }

    private void uploadConfig()
            throws RemoteException, ResourceAdminServiceExceptionException, MalformedURLException,
            InterruptedException, XPathExpressionException {
        ResourceAdminServiceClient resourceAdminServiceStub = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
, context.getContextTenant().getContextUser().getPassword());
        resourceAdminServiceStub.deleteResource("/_system/config/validate");
        resourceAdminServiceStub.addCollection("/_system/config/", "validate", "", "Contains test schema files");

        resourceAdminServiceStub.addResource("/_system/config/validate/schema.xml", "application/xml",
                                             "schema files", new DataHandler(new URL("file:///" +
                                                                                     getESBResourceLocation() + "/mediatorconfig/validate/schema.xml")));
    }

    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
, context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/config/validate");
    }

}


