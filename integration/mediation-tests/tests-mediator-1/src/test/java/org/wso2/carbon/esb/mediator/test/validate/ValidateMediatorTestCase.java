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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

public class ValidateMediatorTestCase extends ESBIntegrationTest {

    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        // Initialize ESBMediatorTest
        super.init();
        resourceAdminServiceClient = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(),
                getSessionCookie());
        URL url2 = new URL("file:///" + getESBResourceLocation() + File.separator + "synapseconfig"
                           + File.separator + "filters" + File.separator + "validate" + File.separator + "schema1a.xml");
        resourceAdminServiceClient.deleteResource("/_system/config/schema");
        resourceAdminServiceClient.addResource("/_system/config/schema/schema1a", "application/xml"
                , "Second Schema", new DataHandler(url2));

        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                                          + "synapseconfig" + File.separator + "filters" + File.separator
                                          + "validate" + File.separator + "validate_synapse.xml");

    }

    /*https://wso2.org/jira/browse/STRATOS-2297*/
    @Test(groups = "wso2.esb")
    public void validateMediatorDynamicSchemaTest() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");

        Assert.assertTrue(response.toString().contains("GetQuoteResponse"), "GetQuoteResponse not found in response");
        Assert.assertTrue(response.toString().contains("WSO2 Company"), "GetQuoteResponse not found in response");
    }

    @AfterClass(alwaysRun = true)
    public void clear() throws Exception {
        try {
            resourceAdminServiceClient.deleteResource("/_system/config/schema");
        } finally {
            super.cleanup();
            resourceAdminServiceClient = null;
        }

    }


}
