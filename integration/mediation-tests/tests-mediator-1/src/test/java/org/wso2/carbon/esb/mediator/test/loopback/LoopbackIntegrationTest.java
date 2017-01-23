package org.wso2.carbon.esb.mediator.test.loopback;

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


import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import javax.xml.namespace.QName;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;


public class LoopbackIntegrationTest extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/core_mediator/synapseLoopbackMediatorConfig.xml");
    }

    @Test(groups = "wso2.esb", description = "LoopBack mediator test")
    public void testRespondMediator() throws AxisFault {
        OMElement stockQuoteResponse1 = null;
        stockQuoteResponse1 = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        assertNotNull(stockQuoteResponse1, "Response is null");

        assertEquals(stockQuoteResponse1.getLocalName(),"messageBeforeLoopBack","First element should be messageBeforeLoopBack");
        assertEquals(stockQuoteResponse1.getFirstElement().getLocalName(),"messageBeforeLoopBackSymbol","Second element should be messageBeforeLoopBackSymbol");
        assertEquals(stockQuoteResponse1.getFirstElement().getFirstChildWithName (new QName("http://services.samples", "symbolBeforeLoopBack")).getText(),"WSO2","Symbol does not match");

        assertNotEquals(stockQuoteResponse1.getLocalName(),"messageAfterLoopBack","First element should Not be messageAfterLoopBack");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
