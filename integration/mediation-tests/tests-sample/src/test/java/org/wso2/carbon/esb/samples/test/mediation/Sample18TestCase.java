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

package org.wso2.carbon.esb.samples.test.mediation;

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
 * Sample 18: Transforming a Message Using ForEachMediator
 */
public class Sample18TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadSampleESBConfiguration(18);
    }

    @Test(groups = {"wso2.esb"},
            description = "Transforming a Message Using ForEachMediator")
    public void testTransformWithForEachMediator() throws Exception {

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewer.clearLogs();

        String request =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:m0=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                        "    <soap:Header/>\n" +
                        "    <soap:Body>\n" +
                        "        <m0:getQuote>\n" +
                        "            <m0:request><m0:symbol>IBM</m0:symbol></m0:request>\n" +
                        "            <m0:request><m0:symbol>WSO2</m0:symbol></m0:request>\n" +
                        "            <m0:request><m0:symbol>MSFT</m0:symbol></m0:request>\n" +
                        "        </m0:getQuote>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>\n";
        sendRequest(getMainSequenceURL(), request);

        LogEvent[] getLogsInfo = logViewer.getAllRemoteSystemLogs();
        for (LogEvent event : getLogsInfo) {

            if (event.getMessage().contains("<m0:getQuote>")) {
                assertTrue(true, "Payload not found");

                String payload = event.getMessage();
                String search = "<m0:getQuote>(.*)</m0:getQuote>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "getQuote element not found");
                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertTrue(quote.contains(
                                    "<m0:checkPriceRequest><m0:code>IBM</m0:code></m0:checkPriceRequest>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:checkPriceRequest><m0:code>WSO2</m0:code></m0:checkPriceRequest>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:checkPriceRequest><m0:code>MSFT</m0:code></m0:checkPriceRequest>"),
                            "MSTF Element not found");

                }
            }
        }
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
                "application/xml;charset=" + charset);
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(query.getBytes(charset));
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    log.error("Error while closing the connection");
                }
            }
        }
        InputStream response = connection.getInputStream();
        String out = "[Fault] No Response.";
        if (response != null) {
            StringBuilder sb = new StringBuilder();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = response.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            out = sb.toString();
        }
        response.close();
    }
}
