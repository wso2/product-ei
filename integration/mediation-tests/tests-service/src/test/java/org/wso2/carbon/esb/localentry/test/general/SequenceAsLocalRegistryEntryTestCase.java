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
package org.wso2.carbon.esb.localentry.test.general;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SequenceAsLocalRegistryEntryTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/localEntryConfig/sequence_as_local_registry_entry.xml");
    }

    @Test(groups = {"wso2.esb"},description = "Create an sequence as a local registry entry")
    public void testSequenceAsLocalRegistryEntry() throws Exception {
       OMElement response=axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL()
                ,getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),"IBM");
        assertNotNull(response,"Response is null");
        assertEquals(response.getLocalName(),"getQuoteResponse","getQuoteResponse mismatch");
        OMElement omElement=response.getFirstElement();
        String symbolResponse=omElement.getFirstChildWithName
                (new QName("http://services.samples/xsd","symbol")).getText();
        assertEquals(symbolResponse,"IBM","Symbol is not match");
    }

    @AfterClass
    private void destroy() throws Exception{
        super.cleanup();
    }
}
