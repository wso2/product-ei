/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ei.scenario.test;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.jaxen.JaxenException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.http.HTTPUtils;
import org.wso2.carbon.esb.scenario.test.common.http.SOAPClient;
import org.wso2.carbon.esb.scenario.test.common.utils.FileUtils;
import org.wso2.carbon.esb.scenario.test.common.utils.XMLUtils;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This test class contains tests related to Enrich Mediator being used to enrich payload with child elements.
 */
public class AddChildToXMLTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(AddChildToXMLTest.class);
    private String sourcesFilePath;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        sourcesFilePath = testResourcesDir + File.separator + "source_files";
    }

    @Test(description = "1.6.1.1")
    public void testAddingInlineContentAsChild() throws IOException, XMLStreamException {
        String url = getProxyServiceURLHttp("1_6_1_1_Proxy_SoapToPoxMsgEnrichWithChild");
        String request = FileUtils.readFile(sourcesFilePath + File.separator + "request_1_6_1_1.xml");

        Map<String, String> headers = new HashMap<>(1);
        headers.put(ScenarioConstants.MESSAGE_ID, "1_6_1_1");

        SOAPClient soapClient = new SOAPClient();
        HttpResponse response = soapClient.sendSimpleSOAPMessage(url, request, "urn:mediate", headers);

        String expectedResponse = FileUtils.readFile(sourcesFilePath + File.separator + "response_1_6_1_1.xml");

        assertResponse(response, expectedResponse);
    }


    @Test(description = "1.6.1.2")
    public void testPassThroughProxyTemplate() throws IOException, XMLStreamException {

        String url = getProxyServiceURLHttp("1_6_1_2_Proxy_SoapToPoxEnrichWithXpathAsChild");

        String request = FileUtils.readFile(sourcesFilePath  + File.separator + "request_1_6_1_2.xml");

        Map<String, String> headers = new HashMap<>(1);
        headers.put(ScenarioConstants.MESSAGE_ID, "1_6_1_2");

        SOAPClient soapClient = new SOAPClient();
        HttpResponse response = soapClient.sendSimpleSOAPMessage(url, request, "urn:mediate", headers);

        String expectedResponse = FileUtils.readFile(sourcesFilePath + File.separator + "response_1_6_1_2.xml");

        assertResponse(response, expectedResponse);
    }


    private void assertResponse(HttpResponse response, String expectedResponse) throws IOException, XMLStreamException {
        Assert.assertEquals(HTTPUtils.getHTTPResponseCode(response), 200, "Response failed");
        OMElement respElement = HTTPUtils.getOMFromResponse(response);

        Assert.assertNotNull(respElement, "Invalid response");

        boolean compareResult = XMLUtils.compareOMElements(XMLUtils.StringASOM(expectedResponse), respElement);
        Assert.assertEquals(compareResult, true, "expected payload " + expectedResponse
                + " , but received " + respElement.toString());
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }
}
