/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediators.payloadFactory;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class tests whether XML special chars are properly escaped in JSON payload scenarios.
 * When the XML special chars are not escaped in JSON payloads and if we use a content aware mediator
 * afterwards it gives Illegal Character errors since content aware mediator tries to build the message
 * as XML.
 */
public class JSONPayloadXMLSpecialCharEscapeTest extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                + "synapseconfig" + File.separator + "payloadmediatype" + File.separator
                + "JSONPayloadXMLSpecialCharEscapeTest.xml");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "JSONPayload with XMLSpecialChar Escape Test")
    public void JSONPayloadWithXMLSpecialCharEscapeTest()
            throws AxisFault, MalformedURLException, AutomationFrameworkException {

        //json request payload.
        String payload = "<text>{\"chars\" : \"266/W, Simple Text &#60; &#62; &#38;,&#xd;\n" +
                "line2,&#xd;\n" +
                "line 3\"}</text>";

        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String serviceURL = this.getApiInvocationURL("testEnterAPI");

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                new URL(serviceURL), writer, "application/xml");
        assertNotNull(response, "Response is null");
        //should return the response without throwing any errors.
        assertTrue(response.contains("{\"chars\" : \"266/W, Simple Text < > &,\\r\\nline2,\\r\\nline 3\"}"));
    }
}

