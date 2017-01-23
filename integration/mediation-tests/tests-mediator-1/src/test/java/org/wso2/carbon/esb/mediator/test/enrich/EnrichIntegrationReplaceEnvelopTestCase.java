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
package org.wso2.carbon.esb.mediator.test.enrich;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Replace out going message envelope with the envelop of source message
 */
public class EnrichIntegrationReplaceEnvelopTestCase extends ESBIntegrationTest {
    private OMElement response;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/enrich/replace_envelop.xml");
    }

    @Test(groups = "wso2.esb", description = "Tests-Replace out going message envelop")
    public void testReplaceEnvelop() throws AxisFault {

        response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("enrichSample6"), null, "WSO2");
        assertNotNull(response, "Response is null");
        assertEquals(response.getQName().getLocalPart(), "getQuote");
        assertEquals(response.getFirstElement().getLocalName().toString(),
                     "request", "Local name does not match");
        assertEquals(response.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples", "symbol")).getText(),
                     "WSO2", "Tag does not match");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
