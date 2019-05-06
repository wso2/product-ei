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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.http.HttpResponse;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.AvailabilityPollingUtils;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class tests the scenario of sending back response when client sends a SOAP1.1 request and when there is a
 * SOAP backend which sends a content type that is not in SOAP1.1/SOAP1.2 content type format. Even through client has
 * sent a SOAP1.1 request, it is not expected to convert the response back to SOAP1.1 format when the backend responses
 * as non REST.
 *
 * Related issue: https://github.com/wso2/product-ei/issues/1798
 */
public class ConversionWithNonSOAPContentTypeBackendTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverManager;

    @BeforeClass(alwaysRun = true) public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        serverManager.applyConfiguration(new File(
                getClass().getResource("/artifacts/ESB/passthru/transport/soapconversion/axis2.xml").getPath()));
        super.init();

        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/passthru/transport/soapconversion/SOAPFormatConversionTestArtifacts.xml");
    }

    @Test(groups = { "wso2.esb" }, description = "Test for response Content-Type when the client invokes as a SOAP11 "
            + "request and the SOAP backend responses with content type that is not in SOAP11/SOAP12 format")
    public void testResponseContentType() throws Exception {

        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "   <soapenv:Header/>\n   <soapenv:Body/>\n</soapenv:Envelope>";

        String requestContentType = "text/xml;charset=UTF-8";

        String responseContentType = "application/abc";

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();

        headers.put("Content-Type", requestContentType);

        HttpResponse response = httpClient
                .doPost(getProxyServiceURLHttp("SOAP11ProxyService"), headers, payload, requestContentType);

        String contentTypeData = response.getEntity().getContentType().getValue();

        Assert.assertTrue(contentTypeData.contains(responseContentType));

    }

    @AfterClass(alwaysRun = true) public void stop() throws Exception {
        try {
            cleanup();
        } finally {
            Awaitility.await()
                              .pollInterval(500, TimeUnit.MILLISECONDS)
                              .atMost(120000, TimeUnit.MILLISECONDS)
                              .until(AvailabilityPollingUtils.isProxyNotAvailable("SOAP11ProxyService",
                                      contextUrls.getBackEndUrl(), sessionCookie));
            serverManager.restoreToLastConfiguration();
            serverManager = null;
        }

    }

}