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

package org.wso2.carbon.esb.samples.test.template;


import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CallTemplateIntegrationSample750TestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception, IOException {
        super.init();
        loadSampleESBConfiguration(750);
    }

    @Test(groups = {"wso2.esb"}, description = "Stereotyping XSLT Transformations with Templates " +
                                               ":Test using sample 750")
    public void testXSLTTransformationWithTemplates() throws IOException, XMLStreamException {
        OMElement response=axis2Client.sendCustomQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy")
                ,null,"IBM");
        assertNotNull(response,"Response message is null");
        assertEquals(response.getLocalName(),"CheckPriceResponse","CheckPriceResponse not match");
        assertTrue(response.toString().contains("Price"),"No price tag in response");
        assertTrue(response.toString().contains("Code"),"No code tag in response");
        assertEquals(response.getFirstChildWithName
                (new QName("http://services.samples/xsd","Code")).getText(),"IBM","Symbol not matched");

    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}
