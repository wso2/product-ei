/*
*Copyright (c) 2014 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertEquals;

/**
 * This class tests the functionality of the FORCE_SC_ACCEPTED property
 */
public class PropertyIntegrationForceSCAcceptedPropertyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "Testing functionality of FORCE_SC_ACCEPTED " +
                                             "- Enabled False")
    public void testFORCE_SC_ACCEPTEDPropertyEnabledFalseScenario() throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/FORCE_SC_ACCEPTED_Disabled.xml");

        int responseStatus = 0;

        String strXMLFilename = FrameworkPathUtil.getSystemResourceLocation() + "artifacts"
                                + File.separator + "ESB" + File.separator + "mediatorconfig" +
                                File.separator + "property" + File.separator + "GetQuoteRequest.xml";

        File input = new File(strXMLFilename);
        PostMethod post = new PostMethod(getProxyServiceURLHttp("Axis2ProxyService"));
        RequestEntity entity = new FileRequestEntity(input, "text/xml");
        post.setRequestEntity(entity);
        post.setRequestHeader("SOAPAction", "getQuote");

        HttpClient httpclient = new HttpClient();

        try {
            responseStatus = httpclient.executeMethod(post);
        } finally {
            post.releaseConnection();
        }

        assertEquals(responseStatus, 200, "Response status should be 200");

    }

    @Test(groups = "wso2.esb", description = "Testing functionality of FORCE_SC_ACCEPTED " +
                                             "Enabled True  - " +
                                             "Client should receive 202 message",
          dependsOnMethods = "testFORCE_SC_ACCEPTEDPropertyEnabledFalseScenario")
    public void testWithFORCE_SC_ACCEPTEDPropertyEnabledTrueScenario() throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/FORCE_SC_ACCEPTED_Enabled.xml");

        int responseStatus = 0;

        String strXMLFilename = FrameworkPathUtil.getSystemResourceLocation() + "artifacts"
                                + File.separator + "ESB" + File.separator + "mediatorconfig" +
                                File.separator + "property" + File.separator + "PlaceOrder.xml";

        File input = new File(strXMLFilename);
        PostMethod post = new PostMethod(getProxyServiceURLHttp("Axis2ProxyOutOnlyService"));
        RequestEntity entity = new FileRequestEntity(input, "text/xml");
        post.setRequestEntity(entity);
        post.setRequestHeader("SOAPAction", "placeOrder");

        HttpClient httpclient = new HttpClient();

        try {
            responseStatus = httpclient.executeMethod(post);
        } finally {
            post.releaseConnection();
        }

        assertEquals(responseStatus, 202, "Response status should be 202");
    }


}
