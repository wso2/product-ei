/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *
 */
package org.wso2.carbon.esb.mediator.test.call;

import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class Util {

    public static HttpResponse sendRequest(URL endpoint, Map<String, String> headers)
            throws AutomationFrameworkException, IOException {

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                throw new AutomationFrameworkException("Shouldn't happen: HttpURLConnection doesn't support GET ?? " + e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setReadTimeout(10000);
            for (Map.Entry<String, String> e : headers.entrySet()) {
                urlConnection.setRequestProperty(e.getKey(), e.getValue());
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
                    ((Map)responseHeaders).put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode());
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

    public static void executeAndAssert(URL endpoint, int statusCode, String statusMessage)
            throws Exception{
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "text/plain");

        HttpResponse httpResponse = sendRequest(endpoint, header);

        assertEquals(httpResponse.getResponseCode(), statusCode, "Expected " +
                statusCode + ", found " + httpResponse.getResponseCode() + " http status code");
        assertTrue(httpResponse.getData().equalsIgnoreCase("Received:" + statusCode + "," + statusMessage),
                "Required payload not found");
    }
}
