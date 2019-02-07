/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test.common.http;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.wso2.esb.integration.common.utils.HttpDeleteWithEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Simple HTTP client for REST operations
 */
public class RESTClient {

    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 30000;
    private DefaultHttpClient httpclient;

    /**
     * Constructor for RESTClient
     */
    public RESTClient() {
        setup();
    }

    /**
     * Constructor for RESTClient
     *
     * @param connectionTimeout connection time out in milliseconds
     * @param socketTimeout socket time out in milliseconds
     */
    public RESTClient(int connectionTimeout, int socketTimeout) {
        setup();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, socketTimeout);
    }

    /**
     * Sets up the client
     */
    private void setup() {
        httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        httpclient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                return false;
            }
        });
    }

    /**
     * Sends a HTTP GET request to the specified URL.
     *
     * @param url     Target endpoint URL
     * @param headers Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doGet(String url, Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpGet(url);
        HTTPUtils.setHTTPHeaders(headers, request);
        return httpclient.execute(request);
    }

    /**
     * Sends an HTTP GET request to the specified URL.
     *
     * @param url target endpoint URL
     * @return returned HTTP response
     * @throws IOException if an error occurs while making the invocation
     */
    public HttpResponse doGet(String url) throws IOException {
        HttpUriRequest request = new HttpGet(url);
        return httpclient.execute(request);
    }

    /**
     * Sends a HTTP POST request to the specified URL.
     *
     * @param url         Target endpoint URL
     * @param headers     Any HTTP headers that should be added to the request
     * @param payload     Content payload that should be sent
     * @param contentType Content-type of the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doPost(String url, final Map<String, String> headers,
                               final String payload, String contentType) throws IOException {
        HttpUriRequest request = new HttpPost(url);
        prepareRequest(headers, payload, contentType, request);
        return httpclient.execute(request);
    }


    /**
     * Sends a HTTP POST request to the specified URL.
     *
     * @param url         Target endpoint URL
     * @param payload     Content payload that should be sent
     * @param contentType Content-type of the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doPost(String url, final String payload, String contentType) throws IOException {
        HttpUriRequest request = new HttpPost(url);
        prepareRequest(null, payload, contentType, request);
        return httpclient.execute(request);
    }

    /**
     * Sends a HTTP PUT request to the specified URL.
     *
     * @param url         Target endpoint URL
     * @param headers     Any HTTP headers that should be added to the request
     * @param payload     Content payload that should be sent
     * @param contentType Content-type of the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doPut(String url, final Map<String, String> headers,
                              final String payload, String contentType) throws IOException {
        HttpUriRequest request = new HttpPut(url);
        prepareRequest(headers, payload, contentType, request);
        return httpclient.execute(request);
    }

    /**
     * Sends a HTTP DELETE request to the specified URL.
     *
     * @param url     Target endpoint URL
     * @param headers Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doDelete(String url, final Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpDelete(url);
        HTTPUtils.setHTTPHeaders(headers, request);
        return httpclient.execute(request);
    }

    /**
     * Sends an HTTP DELETE request to the specified URL.
     *
     * @param url target endpoint URL
     * @return returned HTTP response
     * @throws IOException if an error occurs while making the invocation
     */
    public HttpResponse doDelete(String url) throws IOException {
        HttpUriRequest request = new HttpDelete(url);
        return httpclient.execute(request);
    }

    /**
     * Sends a HTTP PATCH request to the specified URL.
     *
     * @param url         Target endpoint URL
     * @param headers     Any HTTP headers that should be added to the request
     * @param payload     Content payload that should be sent
     * @param contentType Content-type of the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doPatch(String url, final Map<String, String> headers,
                                final String payload, String contentType) throws IOException {
        HttpUriRequest request = new HttpPatch(url);
        prepareRequest(headers, payload, contentType, request);
        return httpclient.execute(request);
    }

    /**
     * Sends a HTTP OPTIONS request to the specified URL.
     *
     * @param url         Target endpoint URL
     * @param headers     Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doOptions(String url, final Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpOptions(url);
        HTTPUtils.setHTTPHeaders(headers, request);
        return httpclient.execute(request);
    }

    /**
     * Sends a HTTP Head request to the specified URL.
     *
     * @param url     Target endpoint URL
     * @param headers Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doHead(String url, final Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpHead(url);
        HTTPUtils.setHTTPHeaders(headers, request);
        return httpclient.execute(request);
    }

    /**
     * Sends a HTTP DELETE request with entity body to the specified URL.
     *
     * @param url     Target endpoint URL
     * @param headers Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doDeleteWithPayload(String url, final Map<String, String> headers,
                                            final String payload, String contentType) throws IOException {

        boolean zip = false;
        HttpUriRequest request = new HttpDeleteWithEntity(url);
        HTTPUtils.setHTTPHeaders(headers, request);
        HttpEntityEnclosingRequest entityEncReq = (HttpEntityEnclosingRequest) request;

        //check if content encoding required
        if (headers != null && "gzip".equals(headers.get(HttpHeaders.CONTENT_ENCODING))) {
            zip = true;
        }

        EntityTemplate ent = new EntityTemplate(new EntityContentProducer(payload, zip));
        ent.setContentType(contentType);

        if (zip) {
            ent.setContentEncoding("gzip");
        }
        entityEncReq.setEntity(ent);
        return httpclient.execute(request);
    }

    /**
     * Prepares a HttpUriRequest to be sent.
     *
     * @param headers HTTP headers to be set as a MAP <Header name, Header value>
     * @param payload Final payload to be sent
     * @param contentType Content-type (i.e application/json)
     * @param request HttpUriRequest request to be prepared
     */
    private void prepareRequest(Map<String, String> headers, final String payload, String contentType,
                                HttpUriRequest request) {
        HTTPUtils.setHTTPHeaders(headers, request);
        HttpEntityEnclosingRequest entityEncReq = (HttpEntityEnclosingRequest) request;
        final boolean zip = headers != null && "gzip".equals(headers.get(HttpHeaders.CONTENT_ENCODING));

        EntityTemplate ent = new EntityTemplate(new ContentProducer() {
            public void writeTo(OutputStream outputStream) throws IOException {
                OutputStream out = outputStream;
                if (zip) {
                    out = new GZIPOutputStream(outputStream);
                }
                out.write(payload.getBytes());
                out.flush();
                out.close();
            }
        });
        ent.setContentType(contentType);
        if (zip) {
            ent.setContentEncoding("gzip");
        }
        entityEncReq.setEntity(ent);
    }


    /**
     * {@link ContentProducer} implementation
     */
    private static class EntityContentProducer implements ContentProducer {

        private boolean zip = false;
        private String payload = null;

        EntityContentProducer(String entityBody, boolean createGZipStream) {
            this.zip = createGZipStream;
            this.payload = entityBody;
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            OutputStream out = outputStream;
            if (zip) {
                out = new GZIPOutputStream(outputStream);
            }
            out.write(payload.getBytes());
            out.flush();
            out.close();
        }
    }

}
