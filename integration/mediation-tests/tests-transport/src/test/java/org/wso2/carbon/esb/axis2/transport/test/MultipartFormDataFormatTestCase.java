/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.axis2.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertNotNull;

/**
 * Test case to test the order of elements in the response received from the esb in multipart form data.
 */
public class MultipartFormDataFormatTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Test the order of elements in the response received from the esb in multipart form data")
    public void elementOrderTestCase() throws Exception {
        String serviceURL = getProxyServiceURLHttp("MultipartFormDataTestProxy");
        String payload = "<body/>";
        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String response = HttpURLConnectionClient
                .sendPostRequestAndReadResponse(data, new URL(serviceURL), writer, "application/xml");
        assertNotNull(response, "Response is null");

        //Extract <files> element from http response
        Pattern pattern = Pattern.compile("^.*(<files>.*<\\/files>).*$");
        Matcher matcher = pattern.matcher(response);
        matcher.find();
        String filesElement = matcher.group(1);

        assertNotNull(filesElement, "No <files> element found!");

        InputStream stream = new ByteArrayInputStream(filesElement.getBytes(StandardCharsets.UTF_8));
        OMElement elements = new StAXOMBuilder(stream).getDocumentElement();

        //Assert if the <contentToken> element placed inside <file> element
        assertNotNull(
                elements.getFirstChildWithName(new QName("file")).getFirstChildWithName(new QName("contentToken")),
                "Incorrect order of elements in the response!");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}