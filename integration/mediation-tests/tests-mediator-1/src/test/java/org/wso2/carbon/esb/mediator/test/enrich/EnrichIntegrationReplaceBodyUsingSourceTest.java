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
import javax.xml.stream.XMLStreamException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Replace the body of out going message with the body of source message defined in source configuraion
 */

public class EnrichIntegrationReplaceBodyUsingSourceTest extends ESBIntegrationTest {
    private OMElement response;


    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/enrich_mediator/replace_body_using_source_type_body.xml");

    }

    @Test(groups = "wso2.esb", description = "Tests-replace body using source type body")
    public void testReplaceBodyUsingSourceBody() throws AxisFault, XMLStreamException {

        response=axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("enrichSample3"), null,"IBM");
        System.out.println(response);
        assertNotNull(response,"Response is null");
        assertEquals(response.getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd", "symbol")).getText(),
                                          "IBM", "Tag does not match");

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
