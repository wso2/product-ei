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
package org.wso2.carbon.esb.mediator.test.drop;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

/*
Put a send mediator after drop mediator and test whether response is null.
*/
public class DropIntegrationTest extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/core_mediator/synapse.xml");

    }
    /*https://wso2.org/jira/browse/STRATOS-2240*/
    @Test(groups = "wso2.esb", description = "Tests-Null response after Drop mediator")
    public void testDropMediator() throws AxisFault {
        OMElement stockQuoteResponse1 = null;
        OMElement stockQuoteResponse2 = null;
        OMElement stockQuoteResponse3 = null;
        stockQuoteResponse1 = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");

        assertNotNull(stockQuoteResponse1, "Response is null");
        assertEquals(stockQuoteResponse1.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText(),
                     "IBM", "Symbol does not match");
        try {
            stockQuoteResponse2 = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "MSFT");
            fail("Request Should throws a AxisFault");

        } catch (AxisFault axisFault) {
            assertNull(stockQuoteResponse2, "Response is not null");
            assertEquals(axisFault.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL);
        }

        try {
            stockQuoteResponse3 = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
            fail("Request Should throws a AxisFault");

        } catch (AxisFault axisFault) {
            assertNull(stockQuoteResponse3, "Response is not null");
            assertEquals(axisFault.getMessage(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL);
        }
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
