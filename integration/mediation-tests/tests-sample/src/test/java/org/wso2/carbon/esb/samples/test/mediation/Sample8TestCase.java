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
package org.wso2.carbon.esb.samples.test.mediation;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;
import java.io.File;

import static org.testng.Assert.*;

/**
 * Sample 8: Introduction to Static and Dynamic Registry Resources and Using XSLT Transformations
 */
public class Sample8TestCase extends ESBIntegrationTest {
    private String oldSynapseConfig;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        File sourceFile = new File(getESBResourceLocation() + File.separator +
                                   "samples" + File.separator + "synapse_sample_8.xml");

        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        oldSynapseConfig = synapseConfigAdminClient.getConfiguration();
        synapseConfigAdminClient.updateConfiguration(sourceFile);
    }

    @Test(groups = { "wso2.esb" },
          description = "Static and Dynamic Registry Resources and Using " +
                        "XSLT Transformations")
    public void testXSLTransformation() throws Exception {
        OMElement response = axis2Client.sendCustomQuoteRequest(getMainSequenceURL()
            , getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE), "WSO2");
        assertNotNull(response, "Response message is null");
        assertEquals(response.getLocalName(), "CheckPriceResponse", "CheckPriceResponse not match");
        assertTrue(response.toString().contains("Price"), "No price tag in response");
        assertTrue(response.toString().contains("Code"), "No code tag in response");
        assertEquals(response.getFirstChildWithName
                         (new QName("http://services.samples/xsd", "Code")).getText(), "WSO2",
                     "Symbol not match"
        );
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        synapseConfigAdminClient.updateConfiguration(oldSynapseConfig);
    }
}
