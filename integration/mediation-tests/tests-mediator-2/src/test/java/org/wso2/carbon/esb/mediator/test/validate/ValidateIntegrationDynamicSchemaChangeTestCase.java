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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

public class ValidateIntegrationDynamicSchemaChangeTestCase extends ESBIntegrationTest {

    private String toUrl = null;
    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        // Initialize ESBMediatorTest
        super.init();
        resourceAdminServiceClient = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        toUrl = getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE);


    }

    /**
     * This test is for Validate Mediator
     * Test Scenario:
     * Add two schema to registry
     * Add a validate mediator, which validates the first element of the SOAP body of incoming message using above schema (static key)
     * Send a request and see whether the validation happens
     * Now change the schema (now validation should happen according to the new schema)
     * Check whether the validation happens accordingly
     * <p/>
     * Test artifacts: /synapseconfig/filters/validate/synapse1.xml , /synapseconfig/filters/validate/schema1.xml , /synapseconfig/filters/validate/schema1a.xml
     *
     * @throws Exception
     */

    @Test(groups = "wso2.esb")
    public void validateMediatorDynamicSchemaChangeTest() throws Exception {
        URL url = new URL("file:///" + getESBResourceLocation() + File.separator + "synapseconfig"
                          + File.separator + "filters" + File.separator + "validate" + File.separator + "schema1.xml");

        resourceAdminServiceClient.addResource("/_system/config/filters/schema1", "application/xml", "First Schema", new DataHandler(url));
        //Work - Schema 1
        Thread.sleep(1000);

        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "synapseconfig" + File.separator + "filters" + File.separator
                                          + "validate" + File.separator + "synapse1.xml");

        try {
            axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
            Assert.fail("Validate mediator on-fail did not executed as expected");
        } catch (AxisFault e) {
            Assert.assertTrue(e.getMessage().contains("Invalid custom quote request for Validate Mediator Test")
                    , "Received Fault message - after validation schema failure");
        }

        URL url2 = new URL("file:///" + getESBResourceLocation() + File.separator + "synapseconfig"
                           + File.separator + "filters" + File.separator + "validate" + File.separator + "schema1a.xml");

        resourceAdminServiceClient.addResource("/_system/config/filters/schema1", "application/xml"
                , "Second Schema", new DataHandler(url2));
        //Work - Schema 2
        /** Time to set up schema - strictly necessary */
        Thread.sleep(30000);

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"), "GetQuoteResponse not found in response");
        Assert.assertTrue(response.toString().contains("WSO2 Company"), "GetQuoteResponse not found in response");
    }

    @AfterClass(alwaysRun = true)
    public void clear() throws Exception {
        try {
            resourceAdminServiceClient.deleteResource("/_system/config/filters/schema1");
        } finally {
            super.cleanup();
            toUrl = null;
            resourceAdminServiceClient = null;
        }

    }


}
