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
package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will test the charset repeating issue in http Content-Type header when message is built
 * in out sequence.
 * Content-Type: text/xml; charset=UTF-8; charset=UTF-8
 * https://wso2.org/jira/browse/ESBJAVA-4468
 */

public class ESBJAVA4468ContentTypeCharsetInResponseTestCase extends ESBIntegrationTest {

    private String messagePayload;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA4468.xml");
        messagePayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                         "xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                         "   <soapenv:Header/>\n" +
                         "   <soapenv:Body>\n" +
                         "      <ser:getQuote>\n" +
                         "         <ser:request>\n" +
                         "            <xsd:symbol>WSO2</xsd:symbol>\n" +
                         "         </ser:request>\n" +
                         "      </ser:getQuote>\n" +
                         "   </soapenv:Body>\n" +
                         "</soapenv:Envelope>";
    }

    @Test(groups = {"wso2.esb"}, description = "Test for charset value in the Content-Type header " +
                                               "in response once message is built in out out sequence")
    public void charsetTestWithInComingContentType() throws Exception {

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/xml;charset=UTF-8");
        headers.put("SOAPAction", "urn:getQuote");

        HttpResponse response = HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp("contentTypeCharsetProxy1"))
                , messagePayload, headers);
        Assert.assertNotNull(response, "Response is null");
        Assert.assertTrue(response.getData().contains("WSO2 Company"), "Response not as expected " + response);

        String contentType = response.getHeaders().get("Content-Type");
        Assert.assertTrue(contentType.contains("text/xml"), "Content-Type mismatched " + contentType);
        Assert.assertEquals(StringUtils.countMatches(contentType, HTTPConstants.CHAR_SET_ENCODING), 1
                , "charset repeated in Content-Type header " + contentType);

    }

    @Test(groups = {"wso2.esb"}, description = "Test for charset value in the Content-Type header " +
                                               "in response once message is built in out out sequence " +
                                               "with messageType")
    public void charsetTestByChangingContentType() throws Exception {

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/xml;charset=UTF-8");
        headers.put("SOAPAction", "urn:getQuote");

        HttpResponse response = HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp("contentTypeCharsetProxy2"))
                , messagePayload, headers);
        Assert.assertNotNull(response, "Response is null");
        Assert.assertTrue(response.getData().contains("WSO2 Company"), "Response not as expected " + response);

        String contentType = response.getHeaders().get("Content-Type");
        //Removing since a invalid scenario after the fix for https://wso2.org/jira/browse/ESBJAVA-1994
        //Assert.assertTrue(contentType.contains("application/xml"), "Content-Type is not changed " + contentType);
        Assert.assertEquals(StringUtils.countMatches(contentType, HTTPConstants.CHAR_SET_ENCODING), 1
                , "charset repeated in Content-Type header " + contentType);
    }

    @Test(groups = {"wso2.esb"}, description = "Test for charset value in the Content-Type header " +
                                               "in response once message is built in out out sequence " +
                                               "with messageType with charset")
    public void charsetTestByChangingContentTypeWithCharset() throws Exception {

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/xml;charset=UTF-8");
        headers.put("SOAPAction", "urn:getQuote");

        HttpResponse response = HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp("contentTypeCharsetProxy3"))
                , messagePayload, headers);
        Assert.assertNotNull(response, "Response is null");
        Assert.assertTrue(response.getData().contains("WSO2 Company"), "Response not as expected " + response);

        String contentType = response.getHeaders().get("Content-Type");
        //Removing Invalid scenario after the fix for https://wso2.org/jira/browse/ESBJAVA-1994
        //Assert.assertTrue(contentType.contains("application/xml"), "Content-Type is not changed " + contentType);
        Assert.assertEquals(StringUtils.countMatches(contentType, HTTPConstants.CHAR_SET_ENCODING), 1
                , "charset repeated in Content-Type header " + contentType);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        cleanup();
    }
}
