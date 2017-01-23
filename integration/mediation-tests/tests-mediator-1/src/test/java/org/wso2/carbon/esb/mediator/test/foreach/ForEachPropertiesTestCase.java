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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test that foreach will process the payload sequentially. Verify the request payload order against processed order.
 */
public class ForEachPropertiesTestCase extends ESBIntegrationTest {

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
    }

    @Test(groups = "wso2.esb", description = "Test foreach properties in a single foreach construct")
    public void testSingleForEachProperties() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/foreach_property_single.xml");

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        String request =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:m0=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                        "    <soap:Header/>\n" +
                        "    <soap:Body>\n" +
                        "        <m0:getQuote>\n" +
                        "            <m0:group>Group1</m0:group>\n" +
                        "            <m0:request><m0:code>IBM</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>WSO2</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>MSFT</m0:code></m0:request>\n" +
                        "        </m0:getQuote>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>\n";

        sendRequest(getMainSequenceURL(), request);

        int msgCounter = 0;

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();

        int afterLogSize = logs.length;

        // Verify logs to check that the order of symbols is same as in the payload. The symbols should be as SYM[1-10]
        // as in payload. Since loop iterates from the last log onwards, verifying whether the symbols are in SYM[10-1] order
        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();

            if (message.contains("fe_originalpayload") || message.contains("in_originalpayload") || message.contains("out_originalpayload")) {
                //fe : original payload while in foreach
                //in : original payload outside foreach
                String payload = message;
                String search = "<m0:getQuote>(.*)</m0:getQuote>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "getQuote element not found");
                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertEquals(quote, "<m0:getQuote>\n" +
                            "            <m0:group>Group1</m0:group>\n" +
                            "            <m0:request><m0:code>IBM</m0:code></m0:request>\n" +
                            "            <m0:request><m0:code>WSO2</m0:code></m0:request>\n" +
                            "            <m0:request><m0:code>MSFT</m0:code></m0:request>\n" +
                            "        </m0:getQuote>", "original payload is incorrect");
                }
            }

            if (message.contains("fe_count")) {
                //counter in foreach sequence
                assertTrue(message.contains("fe_count = " + msgCounter), "Counter mismatch, expected " + msgCounter + " found = " + message);
                msgCounter++;
            }

            if (message.contains("fe_group") || message.contains("in_group")) {
                //group in insequence and foreach sequence
                assertTrue(message.contains("Group1"), "Group mismatch, expected Group1 found = " + message);
            }

            if (message.contains("in_count")) {
                //counter at the end of foreach in insequence
                assertTrue(message.contains("in_count = " + 3), "Final counter mismatch, expected 3 found = " + message);
                msgCounter++;
            }

            if (message.contains("in_payload")) {
                //final payload in insequence
                String payload = message;
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
                                    "<m0:group>Group1</m0:group>"),
                            "Group Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_IBM</m0:symbol>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_WSO2</m0:symbol>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_MSFT</m0:symbol>"),
                            "MSTF Element not found");
                }
            }
        }
    }

    @Test(groups = "wso2.esb", description = "Test foreach properties in a multiple foreach constructs without id specified")
    public void testMultipleForEachPropertiesWithoutID() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/foreach_property_multiple_withoutid.xml");

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        String request =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:m0=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                        "    <soap:Header/>\n" +
                        "    <soap:Body>\n" +
                        "        <m0:getQuote>\n" +
                        "            <m0:group>Group1</m0:group>\n" +
                        "            <m0:request><m0:code>IBM</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>WSO2</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>MSFT</m0:code></m0:request>\n" +
                        "        </m0:getQuote>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>\n";

        sendRequest(getMainSequenceURL(), request);

        int msgCounter1 = 0;
        int msgCounter2 = 0;

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();

        int afterLogSize = logs.length;

        // Verify logs to check that the order of symbols is same as in the payload. The symbols should be as SYM[1-10]
        // as in payload. Since loop iterates from the last log onwards, verifying whether the symbols are in SYM[10-1] order
        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();

            //*** MESSAGES FOR FOREACH 1 ****
            if (message.contains("1_fe_originalpayload") || message.contains("1_in_originalpayload")) {
                //fe : original payload while in foreach
                //in : original payload outside foreach
                String payload = message;
                String search = "<m0:getQuote>(.*)</m0:getQuote>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "getQuote element not found");
                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertEquals(quote, "<m0:getQuote>\n" +
                            "            <m0:group>Group1</m0:group>\n" +
                            "            <m0:request><m0:code>IBM</m0:code></m0:request>\n" +
                            "            <m0:request><m0:code>WSO2</m0:code></m0:request>\n" +
                            "            <m0:request><m0:code>MSFT</m0:code></m0:request>\n" +
                            "        </m0:getQuote>", "original payload is incorrect");
                }
            }

            if (message.contains("1_fe_count")) {
                //counter in foreach sequence
                assertTrue(message.contains("1_fe_count = " + msgCounter1), "Counter mismatch, expected " + msgCounter1 + " found = " + message);
                msgCounter1++;
            }

            if (message.contains("1_fe_group") || message.contains("1_in_group")) {
                //group in insequence and foreach sequence
                assertTrue(message.contains("Group1"), "Group mismatch, expected Group1 found = " + message);
            }

            if (message.contains("1_in_count")) {
                //counter at the end of foreach in insequence
                assertTrue(message.contains("in_count = " + 3), "Final counter mismatch, expected 3 found = " + message);
                msgCounter1++;
            }

            if (message.contains("1_in_payload")) {
                //final payload in insequence and payload in outsequence
                String payload = message;
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
                                    "<m0:group>Group1</m0:group>"),
                            "Group Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_IBM</m0:symbol>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_WSO2</m0:symbol>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_MSFT</m0:symbol>"),
                            "MSTF Element not found");
                }
            }

            //*** MESSAGES FOR FOREACH 2 ***

            if (message.contains("2_fe_originalpayload") || message.contains("2_in_originalpayload")) {
                //fe : original payload while in foreach
                //in : original payload outside foreach
                String payload = message;
                String search = "<m0:checkPrice(.*)</m0:checkPrice>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "checkPrice element not found. Instead found : " + payload);

                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertTrue(quote.contains(
                                    "<m0:group>Group2</m0:group>"),
                            "Group Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>IBM</m0:code>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>WSO2</m0:code>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>MSFT</m0:code>"),
                            "MSTF Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>SUN</m0:code>"),
                            "SUN Element not found");
                }
            }

            if (message.contains("2_fe_count")) {
                //counter in foreach sequence
                assertTrue(message.contains("2_fe_count = " + msgCounter2), "Counter mismatch, expected " + msgCounter2 + " found = " + message);
                msgCounter2++;
            }

            if (message.contains("2_fe_group") || message.contains("2_in_group")) {
                //group in insequence and foreach sequence
                assertTrue(message.contains("Group2"), "Group mismatch, expected Group1 found = " + message);
            }

            if (message.contains("2_in_count")) {
                //counter at the end of foreach in insequence
                assertTrue(message.contains("in_count = " + 4), "Final counter mismatch, expected 4 found = " + message);
                msgCounter2++;
            }

            if (message.contains("2_in_payload")) {
                //final payload in insequence and payload in outsequence
                String payload = message;
                String search = "<m0:checkPrice(.*)</m0:checkPrice>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "checkPrice element not found. Instead found : " + payload);

                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertTrue(quote.contains(
                                    "<m0:group>Group2</m0:group>"),
                            "Group Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group2_IBM</m0:symbol>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group2_WSO2</m0:symbol>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group2_MSFT</m0:symbol>"),
                            "MSTF Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group2_SUN</m0:symbol>"),
                            "SUN Element not found");
                }
            }
        }
    }

    @Test(groups = "wso2.esb", description = "Test foreach properties in a multiple foreach constructs with id specified")
    public void testMultipleForEachPropertiesWithID() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/foreach_property_multiple_withid.xml");

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        String request =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:m0=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                        "    <soap:Header/>\n" +
                        "    <soap:Body>\n" +
                        "        <m0:getQuote>\n" +
                        "            <m0:group>Group1</m0:group>\n" +
                        "            <m0:request><m0:code>IBM</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>WSO2</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>MSFT</m0:code></m0:request>\n" +
                        "        </m0:getQuote>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>\n";

        sendRequest(getMainSequenceURL(), request);

        int msgCounter1 = 0;
        int msgCounter2 = 0;

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        // Verify logs to check that the order of symbols is same as in the payload. The symbols should be as SYM[1-10]
        // as in payload. Since loop iterates from the last log onwards, verifying whether the symbols are in SYM[10-1] order
        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();

            //*** MESSAGES FOR FOREACH 1 ****
            if (message.contains("1_fe_originalpayload") || message.contains("1_in_originalpayload")) {
                //fe : original payload while in foreach
                //in : original payload outside foreach
                String payload = message;
                String search = "<m0:getQuote>(.*)</m0:getQuote>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "getQuote element not found");
                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertEquals(quote, "<m0:getQuote>\n" +
                            "            <m0:group>Group1</m0:group>\n" +
                            "            <m0:request><m0:code>IBM</m0:code></m0:request>\n" +
                            "            <m0:request><m0:code>WSO2</m0:code></m0:request>\n" +
                            "            <m0:request><m0:code>MSFT</m0:code></m0:request>\n" +
                            "        </m0:getQuote>", "original payload is incorrect");
                }
            }

            if (message.contains("1_fe_count")) {
                //counter in foreach sequence
                assertTrue(message.contains("1_fe_count = " + msgCounter1), "Counter mismatch, expected " + msgCounter1 + " found = " + message);
                msgCounter1++;
            }

            if (message.contains("1_fe_group") || message.contains("1_in_group")) {
                //group in insequence and foreach sequence
                assertTrue(message.contains("Group1"), "Group mismatch, expected Group1 found = " + message);
            }

            if (message.contains("1_in_count")) {
                //counter at the end of foreach in insequence
                assertTrue(message.contains("in_count = " + 3), "Final counter mismatch, expected 3 found = " + message);
                msgCounter1++;
            }

            if (message.contains("1_in_payload")) {
                //final payload in insequence and payload in outsequence
                String payload = message;
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
                                    "<m0:group>Group1</m0:group>"),
                            "Group Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_IBM</m0:symbol>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_WSO2</m0:symbol>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_MSFT</m0:symbol>"),
                            "MSTF Element not found");
                }
            }

            //*** MESSAGES FOR FOREACH 2 ***

            if (message.contains("2_fe_originalpayload") || message.contains("2_in_originalpayload")) {
                //fe : original payload while in foreach
                //in : original payload outside foreach
                String payload = message;
                String search = "<m0:checkPrice(.*)</m0:checkPrice>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "checkPrice element not found. Instead found : " + payload);

                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertTrue(quote.contains(
                                    "<m0:group>Group2</m0:group>"),
                            "Group Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>IBM</m0:code>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>WSO2</m0:code>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>MSFT</m0:code>"),
                            "MSTF Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>SUN</m0:code>"),
                            "SUN Element not found");
                }
            }

            if (message.contains("2_fe_count")) {
                //counter in foreach sequence
                assertTrue(message.contains("2_fe_count = " + msgCounter2), "Counter mismatch, expected " + msgCounter2 + " found = " + message);
                msgCounter2++;
            }

            if (message.contains("2_fe_group") || message.contains("2_in_group")) {
                //group in insequence and foreach sequence
                assertTrue(message.contains("Group2"), "Group mismatch, expected Group1 found = " + message);
            }

            if (message.contains("2_in_count")) {
                //counter at the end of foreach in insequence
                assertTrue(message.contains("in_count = " + 4), "Final counter mismatch, expected 4 found = " + message);
                msgCounter2++;
            }

            if (message.contains("2_in_payload")) {
                //final payload in insequence and payload in outsequence
                String payload = message;
                String search = "<m0:checkPrice(.*)</m0:checkPrice>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(payload);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "checkPrice element not found. Instead found : " + payload);

                if (matchFound) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String quote = payload.substring(start, end);

                    assertTrue(quote.contains(
                                    "<m0:group>Group2</m0:group>"),
                            "Group Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_Group2_IBM</m0:symbol>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_Group2_WSO2</m0:symbol>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_Group2_MSFT</m0:symbol>"),
                            "MSTF Element not found");
                    assertTrue(quote.contains(
                                    "<m0:symbol>Group1_Group2_SUN</m0:symbol>"),
                            "SUN Element not found");
                }
            }
        }
    }

    @AfterClass
    public void close() throws Exception {
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
