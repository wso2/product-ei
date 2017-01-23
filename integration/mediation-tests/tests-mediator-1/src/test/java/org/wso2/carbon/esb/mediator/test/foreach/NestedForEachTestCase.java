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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.mediator.test.iterate.IterateClient;
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
 * Test that a nested foreach will transform the payload.
 */
public class NestedForEachTestCase extends ESBIntegrationTest {

    private IterateClient client;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        client = new IterateClient();
    }

    @Test(groups = {"wso2.esb"},
            description = "Transforming a Message Using a Nested ForEach Construct")
    public void testNestedForEach() throws Exception {

        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/nested_foreach.xml");

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

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

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();

            if (message.contains("foreach = after")) {
                String search = "<m0:getQuote>(.*)</m0:getQuote>";
                Pattern pattern = Pattern.compile(search, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(message);
                boolean matchFound = matcher.find();

                assertTrue(matchFound, "getQuote element not found");

                int start = matcher.start();
                int end = matcher.end();
                String quote = message.substring(start, end);

                assertTrue(quote.contains(
                                "<m0:checkPriceRequest><m0:symbol>IBM-1</m0:symbol><m0:symbol>IBM-2</m0:symbol></m0:checkPriceRequest>"),
                        "IBM Element not found");
                assertTrue(quote.contains(
                                "<m0:checkPriceRequest><m0:symbol>WSO2-1</m0:symbol><m0:symbol>WSO2-2</m0:symbol></m0:checkPriceRequest>"),
                        "WSO2 Element not found");
                assertTrue(quote.contains(
                                "<m0:checkPriceRequest><m0:symbol>MSFT-1</m0:symbol><m0:symbol>MSFT-2</m0:symbol></m0:checkPriceRequest>"),
                        "MSFT Element not found");

            }
        }
    }

    @Test(groups = "wso2.esb", description = "Transforming a Message Using a Nested ForEach Construct with Iterate/Aggregate Sending Payload to backend")
    public void testNestedForEachMediatorWithIterate() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/nested_foreach_iterate.xml");
        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        String response = client.send(getMainSequenceURL(), createMultipleSymbolPayLoad(10),
                "urn:getQuote");
        Assert.assertNotNull(response);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        int forEachOuterCount = 0;
        int forEachInnerCount = 0;

        // Verify logs to check that the order of symbols is same as in the payload. The symbols should be as SYM[1-10]
        // as in payload. Since loop iterates from the last log onwards, verifying whether the symbols are in SYM[10-1] order
        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();
            if (message.contains("foreach = outer")) {
                if (!message.contains("SYM" + forEachOuterCount)) {
                    Assert.fail("Incorrect message entered outer ForEach scope. Could not find symbol SYM" + forEachOuterCount + " Found : " + message);
                }
                forEachOuterCount++;
            } else if (message.contains("foreach = inner")) {
                if (!message.contains("SYM" + forEachInnerCount)) {
                    Assert.fail("Incorrect message entered inner ForEach scope. Could not find symbol SYM" + forEachInnerCount + " Found : " + message);
                }
                forEachInnerCount++;
            }
        }

        Assert.assertEquals(forEachOuterCount, 10, "Count of messages entered outer ForEach scope is incorrect");
        Assert.assertEquals(forEachInnerCount, 10, "Count of messages entered inner ForEach scope is incorrect");
    }

    private OMElement createMultipleSymbolPayLoad(int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement chkPrice = fac.createOMElement("CheckPriceRequest", omNs);
            OMElement code = fac.createOMElement("Code", omNs);
            chkPrice.addChild(code);
            code.setText("SYM" + i);
            method.addChild(chkPrice);
        }
        return method;
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

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        client = null;
        super.cleanup();
    }
}
