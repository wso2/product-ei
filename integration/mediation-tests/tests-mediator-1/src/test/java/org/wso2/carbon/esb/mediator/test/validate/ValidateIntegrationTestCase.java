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
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.activation.DataHandler;
import javax.servlet.ServletException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;


public class ValidateIntegrationTestCase extends ESBIntegrationTest {
    /**
     * This patch is to test on validate mediator test cases with changing relavent configuration files
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        uploadConfig();
    }

    /**
     * This test case is to test if validate mediator works with dynamic key as schema key
     */

    @Test(groups = {"wso2.esb"}, description = "Specify a dynamic key as schema key")
    public void TestWithDynamicKey() throws Exception {
        super.loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/dynamickey.xml");

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
            fail("Test failed to invoke on-fail sequence of mediators");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request");
        }
    }

    /**
     * This test case is to test if validate mediator works with static key as schema key
     */

    @Test(groups = {"wso2.esb"}, description = "Static key for schema key")
    public void TestWithStaticKey() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/staticKey.xml");
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
            fail("Test failed to invoke on-fail sequence of mediators");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request");
        }
    }

    /**
     * This test case is to validate if validate mediator works with proxy services used to define external resources
     */

    @Test(groups = {"wso2.esb"}, description = "Verify whether validate mediator supports defining external schema definitions like WSDL resources for proxy service")
    public void TestWithProxy() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/validate_with_proxy.xml");

        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("validateProxy2")
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
            fail("Test failed to invoke on-fail sequence of mediators");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request");
        }
    }

    /**
     * These two test cases to test if validate mediator works with additional features turned on and off.
     */
    @Test(groups = {"wso2.esb"}, description = "Test validate mediator without secure processing feature ")
    public void validateWithSecureProcessingfalse() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/validate_secure_false.xml");
        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
            fail("Test failed to invoke on-fail sequence of mediators");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request");
        }
    }


    @Test(groups = {"wso2.esb"}, description = "Test validate mediator with secure processing feature")
    public void validateWithSecureProcessingTrue() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/validate_secure_true.xml");

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
            fail("Test failed to invoke on-fail sequence of mediators");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request");
        }
    }

    /**
     * This test case shows validate mediator works with additional resources added
     */
    @Test(groups = {"wso2.esb"}, description = "Add additional resources to validate mediator to check whether message is processed accurately")
    public void testWithResources() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/validatemediator/validate_with_resources.xml");

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                    , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
            fail("Test failed to invoke on-fail sequence of mediators");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "Invalid custom quote request");
        }
    }

    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        clearUploadedResource();
        super.cleanup();
    }

    public void uploadConfig()
            throws RemoteException, ResourceAdminServiceExceptionException, MalformedURLException,
            InterruptedException, XPathExpressionException {
        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());
        resourceAdminServiceStub.deleteResource("/_system/config/validate");
        resourceAdminServiceStub.addCollection("/_system/config/", "validate", "", "Contains test schema files");
        resourceAdminServiceStub.addResource("/_system/config/validate/schema.xml", "application/xml", "schema files"
                , new DataHandler(new URL("file:///" + getESBResourceLocation() + "/mediatorconfig/validate/schema.xml")));
        Thread.sleep(1000);
    }

    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {
        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());
        resourceAdminServiceStub.deleteResource("/_system/config/validate");

    }

}
