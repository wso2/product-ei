/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.json;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ReplaceJSONPayloadTestcase extends ESBIntegrationTest {

    private final String proxyServiceName = "replaceJSONPayload";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        verifyProxyServiceExistence(proxyServiceName);
    }

    @Test(groups = "wso2.esb")
    public void replaceJSONPlayloadTest() throws Exception {
        String expectedResponse = "SUCCESS";
        String requestMessageBody = createRequest();
        String response = httpClient(getProxyServiceURLHttp(proxyServiceName), requestMessageBody);
        Assert.assertTrue(response.contains(expectedResponse), "The expected response is not received");
    }

    @AfterClass(alwaysRun = true)
    public void clear() throws Exception {
        super.cleanup();
    }

    private String createRequest() {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:typ=\"http://www.wso2.org/types\">"
                + "<soapenv:Header/><soapenv:Body>"
                + " <typ:greet>"
                + "<!--Optional:-->"
                + "<name>hello</name>"
                + "</typ:greet>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
    }

    private String httpClient(String proxyLocation, String xml) {
        try {
            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod(proxyLocation);

            post.setRequestEntity(new StringRequestEntity(xml));
            post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
            post.setRequestHeader("SOAPAction", "urn:mediate");
            httpclient.executeMethod(post);

            InputStream in = post.getResponseBodyAsStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            reader.close();
            return buffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
