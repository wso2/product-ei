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

import javax.servlet.ServletException;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Replace part of the message defined by xpath expression with the message body defined in source configuration
 */
public class EnrichIntegrationReplacePartOfMessageByBodyTest extends ESBIntegrationTest {
    private OMElement response;


    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception, ServletException, RemoteException {
        init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/enrich/replace_part_of_msg_by_body.xml");

    }

    @Test(groups = "wso2.esb", description = "Tests-Replace part of the message defined by xpath from message body")
    public void testReplacePartOfMessageByBody() throws AxisFault, XMLStreamException {

        response=axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("enrichSample6"), null,"WSO2");
        assertNotNull(response,"Response is null");
        assertEquals(response.getFirstElement().getLocalName(), "getQuote",
                     "Tag does not match");
        assertEquals(response.getFirstElement().getFirstElement().getLocalName(),"request",
                     "Tag does not match");
        assertEquals(response.getFirstElement().getFirstElement().getFirstElement().getLocalName(),
                     "symbol","Tag does not match");
        assertEquals(response.getFirstElement().getFirstElement().getFirstElement().getText(),
                     "WSO2","Tag does not match");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
