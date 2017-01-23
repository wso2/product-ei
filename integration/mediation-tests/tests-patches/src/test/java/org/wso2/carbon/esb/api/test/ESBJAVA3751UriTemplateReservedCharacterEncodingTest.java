package org.wso2.carbon.esb.api.test;

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

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;


public class ESBJAVA3751UriTemplateReservedCharacterEncodingTest extends ESBIntegrationTest {
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "rest"
                + File.separator + "uri-template-encoding.xml");
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a query param consist of" +
            " reserved character : ")
    public void testURITemplateExpandWithPercentEncoding() throws Exception {
        boolean isPercentEncoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/urlEncoded?queryParam=ESB:WSO2"),
                null);
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("ESB%3AWSO2")) {
                isPercentEncoded = true;
                break;
            }
        }
        Assert.assertTrue(isPercentEncoded,
                "Reserved character should be percent encoded while uri-template expansion");

    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a query param consist of reserved " +
            "character : with percent encoding escaped at uri-template expansion")
    public void testURITemplateExpandWithEscapedPercentEncoding() throws Exception {
        boolean isPercentEncoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/escapeUrlEncoded?queryParam=ESB:WSO2"),
                null);
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("ESB%3AWSO2")) {
                isPercentEncoded = true;
                break;
            }
        }
        Assert.assertFalse(isPercentEncoded,
                "Reserved character should not be percent encoded while uri-template expansion as escape enabled");

    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a path param consist of" +
            " reserved character : ")
    public void testURITemplateExpandWithPercentEncodingPathParamCase() throws Exception {
        boolean isPercentEncoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/urlEncoded/ESB:WSO2"),
                null);
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("To: /services/test_2/ESB%3AWSO2")) {
                isPercentEncoded = true;
                break;
            }
        }
        Assert.assertTrue(isPercentEncoded,
                "Reserved character should be percent encoded while uri-template expansion");

    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a path param consist of reserved " +
            "character : with percent encoding escaped at uri-template expansion")
    public void testURITemplateExpandWithEscapedPercentEncodingPathParam() throws Exception {
        boolean isPercentEncoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/escapeUrlEncoded/ESB:WSO2"),
                null);
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("To: /services/test_2/ESB%3AWSO2")) {
                isPercentEncoded = true;
                break;
            }
        }
        Assert.assertFalse(isPercentEncoded,
                "Reserved character should not be percent encoded while uri-template expansion as escape enabled");

    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a query param consist of" +
            " reserved space character ")
    public void testURITemplateParameterDecodingSpaceCharacterCase() throws Exception {
        boolean isPercentEncoded = false;
        boolean isMessageContextPropertyPercentDecoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/urlEncoded?queryParam=ESB%20WSO2"),
                null);
        String decodedMessageContextProperty="decodedQueryParamValue = ESB WSO2";
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(decodedMessageContextProperty)) {
                isMessageContextPropertyPercentDecoded = true;
                continue;
            }
            if (message.contains("ESB%20WSO2")) {
                isPercentEncoded = true;
                continue;
            }
        }
        Assert.assertTrue(isMessageContextPropertyPercentDecoded,
                "Uri-Template parameters should be percent decoded at message context property");
        Assert.assertTrue(isPercentEncoded,
                "Reserved character should be percent encoded while uri-template expansion");
    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a query param consist of" +
            " reserved + character ")
    public void testURITemplateParameterDecodingPlusCharacterCase() throws Exception {
        boolean isPercentEncoded = false;
        boolean isMessageContextPropertyPercentDecoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/urlEncoded?queryParam=ESB+WSO2"),
                null);
        String decodedMessageContextProperty="decodedQueryParamValue = ESB+WSO2";
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains(decodedMessageContextProperty)) {
                isMessageContextPropertyPercentDecoded = true;
                continue;
            }
            if (message.contains("ESB%2BWSO2")) {
                isPercentEncoded = true;
                continue;
            }
        }
        Assert.assertTrue(isMessageContextPropertyPercentDecoded,
                "Uri-Template parameters should be percent decoded at message context property");
        Assert.assertTrue(isPercentEncoded,
                "Reserved character should be percent encoded while uri-template expansion");
    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a query param consist of" +
            " reserved + character ")
    public void testURITemplateParameterDecodingWithPercentEncodingEscapedAtExpansion() throws Exception {
        boolean isPercentEncoded = false;
        boolean isMessageContextPropertyPercentDecoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/escapeUrlEncoded?queryParam=ESB+WSO2"),
                null);
        String decodedMessageContextProperty="decodedQueryParamValue = ESB+WSO2";
        LogEvent[] logs = logViewerClient.getAllSystemLogs();

        //introduced since clearLogs() is not clearing previoues URL call logs, and need to stop
        // searching after 4 messages
        int count = 0;
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (count++ >= 4) {
                break;
            }
            if (message.contains(decodedMessageContextProperty)) {
                isMessageContextPropertyPercentDecoded = true;
                continue;
            }
            if (message.contains("ESB%2BWSO2")) {
                isPercentEncoded = true;
                continue;
            }
        }
        Assert.assertTrue(isMessageContextPropertyPercentDecoded,
                "Uri-Template parameters should be percent decoded at message context property");
        Assert.assertFalse(isPercentEncoded,
                "Reserved character should not be percent encoded while uri-template expansion");
    }

    @Test(groups = { "wso2.esb" }, description = "Sending http request with a path param consist of" +
            " whole URL including protocol , host , port etc. ")
    public void testURITemplateSpecialCaseVariableWithFullURL() throws Exception {
        boolean isPercentEncoded = false;
        logViewerClient.clearLogs();
        HttpResponse response = HttpRequestUtil.sendGetRequest(
                getApiInvocationURL("services/client/special_case/http://localhost:8480/services/test_2/special_case"),
                null);
        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("To: /services/test_2/special_case")) {
                isPercentEncoded = true;
                break;
            }
        }
        Assert.assertTrue(isPercentEncoded,
                "The Special case of of Full URL expansion should be identified and should not percent encode full URL");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
