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
package org.wso2.carbon.esb.samples.test.miscellaneous;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * https://wso2.org/jira/browse/ESBJAVA-3451
 * Unlimited strength policy files for the JDK have to be installed for this test case to succeed
 */
public class Sample100TestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        updateESBRegistry(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" +
                          File.separator + "ESBRegistry.xml");
        loadSampleESBConfiguration(100);
    }

    @Test(groups = {"wso2.esb"}, description = "Adding a policy as a local entry and secure endpoint using it")
    public void testAddPolicyViaLocalEntry() throws IOException,
                                                                 XMLStreamException {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName(new QName("http://services.samples/xsd", "symbol")).getText();
        assertEquals(symbolResponse, "IBM", "Symbol is not match");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
