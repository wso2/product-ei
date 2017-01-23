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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test that foreach will process the payload sequentially. Verify the request payload order against processed order.
 */
public class ForEachPropertyMediatorTestCase extends ESBIntegrationTest {

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
    }

    @Test(groups = "wso2.esb", description = "Test multiple foreach constructs with property mediator in flow")
    public void testForEachPropertyMediator() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/foreach_property_mediator.xml");

        LogViewerClient logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        String request =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:m0=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n" +
                        "    <soap:Header/>\n" +
                        "    <soap:Body>\n" +
                        "        <m0:getQuote>\n" +
                        "            <m0:request><m0:code>IBM</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>WSO2</m0:code></m0:request>\n" +
                        "            <m0:request><m0:code>MSFT</m0:code></m0:request>\n" +
                        "        </m0:getQuote>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>\n";

        sendRequest(getMainSequenceURL(), request);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();

        int afterLogSize = logs.length;

        int verifyCount = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();

            if (message.contains("fe_1_verify_in_1")) {
                assertTrue(message.contains("fe_1_verify_in_1 = first property insequence"));
                verifyCount++;
            }
            if (message.contains("in_2_verify_fe_1")) {
                assertTrue(message.contains("in_2_verify_fe_1 = property in first foreach"));
                verifyCount++;
            }
            if (message.contains("fe_2_verify_in_1")) {
                assertTrue(message.contains("fe_2_verify_in_1 = first property insequence"));
                verifyCount++;
            }
            if (message.contains("fe_2_verify_fe_1")) {
                assertTrue(message.contains("fe_2_verify_fe_1 = property in first foreach"));
                verifyCount++;
            }
            if (message.contains("fe_2_verify_in_2")) {
                assertTrue(message.contains("fe_2_verify_in_2 = second property insequence"));
                verifyCount++;
            }
            if (message.contains("in_3_verify_fe_2")) {
                assertTrue(message.contains("in_3_verify_fe_2 = property in second foreach"));
                verifyCount++;
            }
            if (message.contains("in_3_verify_in_1")) {
                assertTrue(message.contains("in_3_verify_in_1 = first property insequence"));
                verifyCount++;
            }
            if (message.contains("in_3_verify_fe_1")) {
                assertTrue(message.contains("in_3_verify_fe_1 = property in first foreach"));
                verifyCount++;
            }
            if (message.contains("in_3_verify_in_2")) {
                assertTrue(message.contains("in_3_verify_in_2 = second property insequence"));
                verifyCount++;
            }
        }

        assertEquals(verifyCount, 20, "Property log count mismatched");
    }


    @Test(groups = "wso2.esb", description = "Test nested foreach constructs with property mediator in flow")
    public void testNestedForEachPropertiesWithID() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/nested_foreach_property_mediator.xml");

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

        int verifyCount = 0;

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();

            if (message.contains("fe_outer_verify_in")) {
                assertTrue(message.contains("fe_outer_verify_in = property insequence"));
                verifyCount++;
            }
            if (message.contains("fe_inner_verify_in")) {
                assertTrue(message.contains("fe_inner_verify_in = property insequence"));
                verifyCount++;
            }
            if (message.contains("fe_inner_verify_fe_outer")) {
                assertTrue(message.contains("fe_inner_verify_fe_outer = property outer foreach"));
                verifyCount++;
            }
            if (message.contains("fe_outer_verify_fe_outer")) {
                assertTrue(message.contains("fe_outer_verify_fe_outer = property outer foreach"));
                verifyCount++;
            }
            if (message.contains("fe_outer_fe_inner")) {
                assertTrue(message.contains("fe_outer_fe_inner = property inner foreach"));
                verifyCount++;
            }
            if (message.contains("in_verify_in")) {
                assertTrue(message.contains("in_verify_in = property insequence"));
                verifyCount++;
            }
            if (message.contains("in_fe_outer")) {
                assertTrue(message.contains("in_fe_outer = property outer foreach"));
                verifyCount++;
            }
            if (message.contains("in_fe_inner")) {
                assertTrue(message.contains("in_fe_inner = property inner foreach"));
                verifyCount++;
            }
        }

        assertEquals(verifyCount, 27, "Property log count mismatched");

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
