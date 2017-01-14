/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.proxyservice.test.proxyservices;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * https://wso2.org/jira/browse/ESBJAVA-4846
 * This Test class will test the functionality of HTTP protocol version That ESB responding.
 * When the incoming protocol is HTTP1.0, ESB should respond in HTTP1.0
 */

public class ESBJAVA4846HttpProtocolVersionTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/proxyservice/proxyConfig.xml");

    }

    @Test(groups = "wso2.esb", description = "Sending HTTP1.0 message")
    public void sendingHTTP10Message() throws Exception {
        PostMethod post = new PostMethod(getProxyServiceURLHttp("StockQuoteProxyTestHTTPVersion"));
        RequestEntity entity = new StringRequestEntity(getPayload(), "text/xml", "UTF-8");
        post.setRequestEntity(entity);
        post.setRequestHeader("SOAPAction", "urn:getQuote");
        HttpMethodParams params = new HttpMethodParams();
        params.setVersion(HttpVersion.HTTP_1_0);
        post.setParams(params);
        HttpClient httpClient = new HttpClient();
        String httpVersion = "";

        try {
            httpClient.executeMethod(post);
            post.getResponseBodyAsString();
            httpVersion = post.getStatusLine().getHttpVersion();
        } finally {
            post.releaseConnection();
        }
        Assert.assertEquals(httpVersion, HttpVersion.HTTP_1_0.toString(), "Http version mismatched");

    }

    @Test(groups = "wso2.esb", description = "Sending HTTP1.1 message")
    public void sendingHTTP11Message() throws Exception {
        PostMethod post = new PostMethod(getProxyServiceURLHttp("StockQuoteProxyTestHTTPVersion"));
        RequestEntity entity = new StringRequestEntity(getPayload(), "text/xml", "UTF-8");
        post.setRequestEntity(entity);
        post.setRequestHeader("SOAPAction", "urn:getQuote");
        HttpMethodParams params = new HttpMethodParams();
        params.setVersion(HttpVersion.HTTP_1_1);
        post.setParams(params);
        HttpClient httpClient = new HttpClient();
        String httpVersion = "";

        try {
            httpClient.executeMethod(post);
            post.getResponseBodyAsString();
            httpVersion = post.getStatusLine().getHttpVersion();
        } finally {
            post.releaseConnection();
        }
        Assert.assertEquals(httpVersion, HttpVersion.HTTP_1_1.toString(), "Http version mismatched");
    }

    @AfterClass(alwaysRun = true)
    public void cleanArtifacts() throws Exception {
        super.cleanup();
    }

    private String getPayload() {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
               " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
               "   <soapenv:Header/>\n" +
               "   <soapenv:Body>\n" +
               "      <ser:getQuote>\n" +
               "         <ser:request>\n" +
               "            <xsd:symbol>10</xsd:symbol>\n" +
               "         </ser:request>\n" +
               "      </ser:getQuote>\n" +
               "   </soapenv:Body>\n" +
               "</soapenv:Envelope>";
    }


}
