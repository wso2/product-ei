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
public class NestedForEachPropertiesTestCase extends ESBIntegrationTest {

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
    }

    @Test(groups = "wso2.esb", description = "Test foreach properties in a nested foreach constructs with id specified")
    public void testNestedForEachPropertiesWithID() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/nested_foreach_property.xml");

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        String request =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:m0=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                        "    <soap:Header/>\n" +
                        "    <soap:Body>\n" +
                        "        <m0:getQuote>\n" +
                        "            <m0:group>Group</m0:group>\n" +
                        "            <m0:request><m0:symbol>IBM</m0:symbol></m0:request>\n" +
                        "            <m0:request><m0:symbol>WSO2</m0:symbol></m0:request>\n" +
                        "            <m0:request><m0:symbol>MSFT</m0:symbol></m0:request>\n" +
                        "        </m0:getQuote>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>\n";

        sendRequest(getMainSequenceURL(), request);

        int msgCounterOuter = 0;
        int msgCounterInner = 0;

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        // Verify logs to check that the order of symbols is same as in the payload. The symbols should be as SYM[1-10]
        // as in payload. Since loop iterates from the last log onwards, verifying whether the symbols are in SYM[10-1] order
        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();

            //*** MESSAGES FOR OUTER FOREACH ****
            if (message.contains("outer_fe_originalpayload") || message.contains("outer_in_originalpayload")) {
                //fe : original payload while in foreach
                //in : original payload outside foreach
                String search = "<m0:getQuote>(.*)</m0:getQuote>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(message);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "getQuote element not found");

                int start = matcher.start();
                int end = matcher.end();
                String quote = message.substring(start, end);

                assertEquals(quote, "<m0:getQuote>\n" +
                        "            <m0:group>Group</m0:group>\n" +
                        "            <m0:request><m0:symbol>IBM</m0:symbol></m0:request>\n" +
                        "            <m0:request><m0:symbol>WSO2</m0:symbol></m0:request>\n" +
                        "            <m0:request><m0:symbol>MSFT</m0:symbol></m0:request>\n" +
                        "        </m0:getQuote>", "original payload is incorrect");
            } else if (message.contains("outer_fe_count")) {
                //counter in foreach sequence
                assertTrue(message.contains("outer_fe_count = " + msgCounterOuter), "Counter mismatch, expected " + msgCounterOuter + " found = " + message);
                msgCounterOuter++;
            } else if (message.contains("outer_in_count")) {
                //counter at the end of outer foreach in insequence
                assertTrue(message.contains("outer_in_count = " + 3), "Final counter mismatch, expected 3 found = " + message);
                msgCounterOuter++;
            } else if (message.contains("outer_fe_group") || message.contains("outer_in_group")) {
                //group in insequence and foreach sequence
                assertTrue(message.contains("Group"), "Group mismatch, expected Group found = " + message);
            }

            //*** MESSAGES FOR INNER FOREACH ***

            else if (message.contains("inner_fe_originalpayload") || message.contains("inner_in_originalpayload")) {
                //fe : original payload while in foreach
                //in : original payload outside foreach
                String search = "<m0:checkPrice(.*)</m0:checkPrice>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(message);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "checkPrice element not found. Instead found : " + message);

                int start = matcher.start();
                int end = matcher.end();
                String quote = message.substring(start, end);

                if (message.contains("<m0:group>NewGroup0</m0:group>")) {
                    assertTrue(quote.contains(
                                    "<m0:code>IBM-1</m0:code>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>IBM-2</m0:code>"),
                            "IBM Element not found");
                } else if (message.contains("<m0:group>NewGroup1</m0:group>")) {

                    assertTrue(quote.contains(
                                    "<m0:code>WSO2-1</m0:code>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>WSO2-2</m0:code>"),
                            "WSO2 Element not found");
                } else if (message.contains("<m0:group>NewGroup2</m0:group>")) {
                    assertTrue(quote.contains(
                                    "<m0:code>MSFT-1</m0:code>"),
                            "MSTF Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>MSFT-2</m0:code>"),
                            "MSTF Element not found");
                } else {
                    assertTrue(false, "Payload not found");
                }
            } else if (message.contains("inner_fe_count")) {
                //counter in foreach sequence
                assertTrue(message.contains("inner_fe_count = " + msgCounterInner), "Counter mismatch, expected " + msgCounterInner + " found = " + message);
                if (msgCounterInner == 1) {
                    msgCounterInner = 0;
                } else {
                    msgCounterInner++;
                }
            } else if (message.contains("inner_fe_group")) {
                //group in inner foreach sequence
                assertTrue(message.contains("NewGroup" + (msgCounterOuter - 1)), "Group mismatch, expected NewGroup" + (msgCounterOuter - 1) + " found = " + message);
            } else if (message.contains("inner_in_group")) {
                //group in insequence for inner foreach
                assertTrue(message.contains("NewGroup2"), "Group mismatch, expected NewGroup2 found = " + message);
            } else if (message.contains("inner_in_count")) {
                //counter at the end of foreach in insequence
                assertTrue(message.contains("inner_in_count = " + 2), "Final counter mismatch, expected 2 found = " + message);
                msgCounterInner++;
            } else if (message.contains("inner_fe_end_originalpayload")) {
                //at end of inner foreach

                String search = "<m0:checkPrice(.*)</m0:checkPrice>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(message);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "checkPrice element not found. Instead found : " + message);

                int start = matcher.start();
                int end = matcher.end();
                String quote = message.substring(start, end);

                if (message.contains("<m0:group>NewGroup0</m0:group>")) {
                    assertTrue(quote.contains(
                                    "<m0:code>IBM-1</m0:code>"),
                            "IBM Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>IBM-2</m0:code>"),
                            "IBM Element not found");
                } else if (message.contains("<m0:group>NewGroup1</m0:group>")) {

                    assertTrue(quote.contains(
                                    "<m0:code>WSO2-1</m0:code>"),
                            "WSO2 Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>WSO2-2</m0:code>"),
                            "WSO2 Element not found");
                } else if (message.contains("<m0:group>NewGroup2</m0:group>")) {
                    assertTrue(quote.contains(
                                    "<m0:code>MSFT-1</m0:code>"),
                            "MSTF Element not found");
                    assertTrue(quote.contains(
                                    "<m0:code>MSFT-2</m0:code>"),
                            "MSTF Element not found");
                } else {
                    assertTrue(false, "Payload not found");
                }

            } else if (message.contains("inner_fe_end_group")) {
                //at end of inner foreach
                assertTrue(message.contains("NewGroup" + (msgCounterOuter - 1)), "Group mismatch, expected NewGroup" + (msgCounterOuter - 1) + " found = " + message);
            } else if (message.contains("inner_fe_end_count")) {
                //counter at the end of foreach in insequence
                assertTrue(message.contains("inner_fe_end_count = " + 2), "Final counter mismatch, expected 2 found = " + message);
            } else if (message.contains("in_payload")) {
                //final payload in insequence and payload in outsequence
                String search = "<m0:getQuote>(.*)</m0:getQuote>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(message);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "checkPrice element not found. Instead found : " + message);


                int start = matcher.start();
                int end = matcher.end();
                String quote = message.substring(start, end);

                assertTrue(quote.contains(
                                "<m0:group>Group</m0:group>"),
                        "Group Element not found");
                assertTrue(quote.contains(
                                "<m0:checkPrice><m0:group>NewGroup0</m0:group><m0:symbol>Group_NewGroup0_IBM-1</m0:symbol><m0:symbol>Group_NewGroup0_IBM-2</m0:symbol></m0:checkPrice>"),
                        "IBM Element not found");
                assertTrue(quote.contains(
                                "<m0:checkPrice><m0:group>NewGroup1</m0:group><m0:symbol>Group_NewGroup1_WSO2-1</m0:symbol><m0:symbol>Group_NewGroup1_WSO2-2</m0:symbol></m0:checkPrice>"),
                        "WSO2 Element not found");
                assertTrue(quote.contains(
                                "<m0:checkPrice><m0:group>NewGroup2</m0:group><m0:symbol>Group_NewGroup2_MSFT-1</m0:symbol><m0:symbol>Group_NewGroup2_MSFT-2</m0:symbol></m0:checkPrice>"),
                        "MSTF Element not found");

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
