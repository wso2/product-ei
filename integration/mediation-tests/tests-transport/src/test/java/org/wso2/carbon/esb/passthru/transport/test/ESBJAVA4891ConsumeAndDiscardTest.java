/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static org.testng.Assert.assertTrue;

/**
 * Test case for Jira ESBJAVA-4891.
 */
public class ESBJAVA4891ConsumeAndDiscardTest  extends ESBIntegrationTest {

    public static final String EXPECTED_ERROR = "<Exception>DATA IS ENCODED IMPROPERLY</Exception>";
    private String input;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        input = FileUtils.readFileToString(new File(getESBResourceLocation() + "/passthru/transport/inputESBJAVA4891.xml"), "ISO-8859-1");
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA-4891.xml");
    }

    /**
     *  There was an infinite read waiting in the pipe, when OMException happened in Payload mediator, because we do consume and discard, without checking the remaining elements in the buffer.
     *  So In this test case, we create an exception in payload mediator and check the response.
     *  The fix was done to check the buffer and do the consume and discard.
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Testing with a Payload Mediator")
    public void testPartialReadErrorWithPayloadMediator() throws Exception {
        URL endpoint = new URL(getProxyServiceURLHttp("TestProxy"));
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/xml");
        HttpResponse httpResponse = doPost(endpoint, input, header);
        assertTrue(EXPECTED_ERROR.equals(httpResponse.getData()), "Expected error message not received");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    private static HttpResponse doPost(URL endpoint, String postBody, Map<String, String> headers)
            throws AutomationFrameworkException, IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new AutomationFrameworkException("Shouldn't happen: HttpURLConnection doesn't support POST?? " +
                                                       e.getMessage(), e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setReadTimeout(10000);
            for (Map.Entry<String, String> e : headers.entrySet()) {
                urlConnection.setRequestProperty(e.getKey(), e.getValue());
            }
            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                writer.write(postBody);
                writer.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while posting data " + e.getMessage(), e);
            }
            StringBuilder sb = new StringBuilder();
            BufferedReader rd;
            try {
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            }
            Iterator<String> itr = urlConnection.getHeaderFields().keySet().iterator();
            Map<String, String> responseHeaders = new HashMap<>();
            String key;
            while (itr.hasNext()) {
                key = itr.next();
                if (key != null) {
                    responseHeaders.put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode(),  responseHeaders);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), Charset.defaultCharset()));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
