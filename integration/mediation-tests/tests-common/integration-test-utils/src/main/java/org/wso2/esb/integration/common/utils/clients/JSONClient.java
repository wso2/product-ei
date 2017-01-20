/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.esb.integration.common.utils.clients;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class JSONClient {
    private static final Log log = LogFactory.getLog(JSONClient.class);


    /**
     * Send simple get quote request in json format
     *
     * @param trpUrl
     * @param symbol
     * @return
     * @throws java.io.IOException
     * @throws JSONException
     */
    public JSONObject sendSimpleStockQuoteRequest(String trpUrl, String symbol)
            throws IOException, JSONException {
        String query = "{\"getQuote\":{\"request\":{\"symbol\":\"" + symbol + "\"}}}";
        return sendRequest(trpUrl, query);
    }

    public JSONObject sendSimpleStockQuoteRequest(String trpUrl, String symbol, String operation)
            throws IOException, JSONException {
        String query = "{\"getQuote\":{\"request\":{\"symbol\":\"" + symbol + "\"}}}";
        return sendRequest(trpUrl, query, operation);
    }

    public JSONObject sendRequest(String trpUrl, JSONObject payload, String operation)
            throws IOException, JSONException {
        return sendRequest(trpUrl, payload.toString(), operation);
    }

    /**
     * Send user define request to ESB in json format.User should specify the query in JSON format
     *
     * @param addUrl
     * @param query
     * @return
     * @throws java.io.IOException
     * @throws JSONException
     */
    public JSONObject sendUserDefineRequest(String addUrl, String query)
            throws IOException, JSONException {
        return sendRequest(addUrl, query);
    }


    private JSONObject sendRequest(String addUrl, String query)
            throws IOException, JSONException {
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

        JSONObject jsonObject = new JSONObject(out);

        return jsonObject;
    }

    private JSONObject sendRequest(String addUrl, String query, String action)
            throws IOException, JSONException {
        String charset = "UTF-8";
        URLConnection connection = new URL(addUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("SOAPAction", action);
        connection.setRequestProperty("Content-Type",
                                      "application/json;charset=" + charset);
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

        JSONObject jsonObject = new JSONObject(out);

        return jsonObject;
    }
}



