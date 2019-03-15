/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
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
 * This class tests Iterate Mediator Jsonpath support with Json Payloads
 */
public class IterateJsonPathTest extends ESBIntegrationTest {

    private String input;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        input = FileUtils.readFileToString(new File(getESBResourceLocation() + "/json/inputESBIterateJson.json"));
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_jsonpath.xml");
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with basic configuration")
    public void testBasicIterateMediatorFlow() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample1") + "/iteratejson1");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with PreservePayload attribute")
    public void testIterateMediatorFlowWithPreservePayload() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample2") + "/iteratejson2");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with AttachPath attribute")
    public void testIterateMediatorFlowWithAttachPath() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample3") + "/iteratejson3");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with Different AttachPath attribute")
    public void testIterateMediatorFlowWithDifferentAttachPath() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample4") + "/iteratejson4");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with AttachPath as `$`")
    public void testIterateMediatorFlowWithRootAttachPath() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample5") + "/iteratejson5");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with ContinueParent attribute")
    public void testIterateMediatorFlowWithContinueParent() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample6") + "/iteratejson6");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with Call mediator inside target")
    public void testIterateMediatorFlowWithCallMediator() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample7") + "/iteratejson7");
        executeSequenceAndAssertResponse(endpoint);
    }

    @Test(groups = "wso2.esb", description = "Testing Iterate mediator json support with Sequential attribute")
    public void testIterateMediatorFlowWithSequential() throws Exception {
        URL endpoint = new URL(getApiInvocationURL("iteratesample8") + "/iteratejson8");
        executeSequenceAndAssertResponse(endpoint);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }

    private void executeSequenceAndAssertResponse(URL endpoint) throws Exception {

        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");

        HttpResponse httpResponse = doPost(endpoint, input, header);

        assertTrue(httpResponse.getData().contains("Alice"), "Required element not found in aggregrated payload");
        assertTrue(httpResponse.getData().contains("Bob"), "Required element not found in aggregrated payload");
        assertTrue(httpResponse.getData().contains("Camry"), "Required element not found in aggregrated payload");
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
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(out, "UTF-8");
                writer.write(postBody);
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while posting data " + e.getMessage(), e);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            Iterator<String> itr = urlConnection.getHeaderFields().keySet().iterator();
            Object responseHeaders = new HashMap();
            String key;
            while (itr.hasNext()) {
                key = itr.next();
                if (key != null) {
                    ((Map) responseHeaders).put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode(), (Map) responseHeaders);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), Charset.defaultCharset()));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

}
