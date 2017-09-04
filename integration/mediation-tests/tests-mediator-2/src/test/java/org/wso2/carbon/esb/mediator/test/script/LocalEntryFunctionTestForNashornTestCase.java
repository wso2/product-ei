/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import javax.xml.namespace.QName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This test case verifies that, mediation with invoking NashornJs script as local entry happens correctly.
 */
public class LocalEntryFunctionTestForNashornTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb", description = "Tests changing payload with inline script")
    public void testInlineFunction() throws Exception {
        OMElement response;
        response = axis2Client.sendCustomQuoteRequest(getProxyServiceURLHttp
                ("scriptMediatorNashornJsLocalEntryTestProxy"), null, "nashornLocalEntry");
        assertNotNull(response, "Response message null");
        assertEquals(response.getFirstElement().getLocalName().toString(),
                "Code", "Local Name does not match");
        assertEquals(response.getQName().getLocalPart().toString(), "CheckPriceResponse", "Local Part does not "
                + "match");
        assertEquals(response.getFirstChildWithName
                        (new QName("http://services.samples/xsd", "Code")).getText(),
                "nashornLocalEntry", "Text does not match");
        assertEquals(response.getFirstChildWithName
                        (new QName("http://services.samples/xsd", "Price")).getLocalName(),
                "Price", "Local Name does not match");
        assertNotNull(response.getFirstChildWithName(new QName("http://services.samples/xsd", "Price")).getText(),
                "Text does not exist");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
