/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.foreach;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertTrue;

/**
 * Test foreach mediator with json payload.
 */
public class ForEachJSONPayloadTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/foreach_json.xml");
    }

    @Test(groups = {"wso2.esb"},
            description = "Test ForEach mediator with JSON payload")
    public void testForEachMediatorWithJSONPayload() throws Exception {

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewer.clearLogs();

        String request = "{\"getQuote\":{\"request\":[{\"symbol\":\"IBM\"},{\"symbol\":\"WSO2\"},{\"symbol\":\"MSFT\"}]}}";

        sendRequest(getMainSequenceURL(), request);

        boolean reachedEnd = false;

        LogEvent[] getLogsInfo = logViewer.getAllRemoteSystemLogs();
        for (LogEvent event : getLogsInfo) {
            if (event.getMessage().contains("STATE = END")) {
                reachedEnd = true;
                String payload = event.getMessage();
                String search = "<jsonObject><getQuote>(.*)</getQuote></jsonObject>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "getQuote element not found");

                int start = matcher.start();
                int end = matcher.end();
                String quote = payload.substring(start, end);

                assertTrue(quote.contains(
                                "<checkPriceRequest xmlns=\"http://ws.apache.org/ns/synapse\"><code>IBM</code></checkPriceRequest>"),
                        "IBM Element not found");
                assertTrue(quote.contains(
                                "<checkPriceRequest xmlns=\"http://ws.apache.org/ns/synapse\"><code>WSO2</code></checkPriceRequest>"),
                        "WSO2 Element not found");
                assertTrue(quote.contains(
                                "<checkPriceRequest xmlns=\"http://ws.apache.org/ns/synapse\"><code>MSFT</code></checkPriceRequest>"),
                        "MSTF Element not found");
            }
        }
        assertTrue(reachedEnd, "Transformed json payload");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private void sendRequest(String addUrl, String query)
            throws IOException {
        String charset = "UTF-8";
        URLConnection connection = new URL(addUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type",
                "application/json;charset=" + charset);
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(query.getBytes(charset));
        } finally {
            if (output != null) {
                output.close();
            }
        }
        InputStream response = connection.getInputStream();
        if (response != null) {
            StringBuilder sb = new StringBuilder();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = response.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            response.close();
        }
    }
}
