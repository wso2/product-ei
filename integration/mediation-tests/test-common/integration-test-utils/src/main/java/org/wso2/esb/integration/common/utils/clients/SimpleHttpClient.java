/**
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.esb.integration.common.utils.clients;


import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Simple HTTP client implementation that can be used to send test HTTP requests.
 */
public class SimpleHttpClient {

    private DefaultHttpClient client;

    public SimpleHttpClient() {
        this.client = new DefaultHttpClient(new ThreadSafeClientConnManager());
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        client.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                return false;
            }
        });
    }

    /**
     * Send a HTTP GET request to the specified URL
     *
     * @param url     Target endpoint URL
     * @param headers Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doGet(String url, Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpGet(url);
        setHeaders(headers, request);
        return client.execute(request);
    }

    /**
     * Send a HTTP POST request to the specified URL
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
        setHeaders(headers, request);
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
        return client.execute(request);
    }

    /**
     * Extracts the payload from a HTTP response. For a given HttpResponse object, this
     * method can be called only once.
     *
     * @param response HttpResponse instance to be extracted
     * @return Content payload
     * @throws IOException If an error occurs while reading from the response
     */
    public String getResponsePayload(HttpResponse response) throws IOException {
        if (response.getEntity() != null) {
            InputStream in = response.getEntity().getContent();
            int length;
            byte[] tmp = new byte[2048];
            StringBuilder buffer = new StringBuilder();
            while ((length = in.read(tmp)) != -1) {
                buffer.append(new String(tmp, 0, length));
            }
            return buffer.toString();
        }
        return null;
    }

    /**
     * Send a HTTP PATCH request to the specified URL
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
        setHeaders(headers, request);
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
        return client.execute(request);
    }

    /**
     * Send a HTTP OPTIONS request to the specified URL
     *
     * @param url         Target endpoint URL
     * @param headers     Any HTTP headers that should be added to the request
     * @param payload     Content payload that should be sent
     * @param contentType Content-type of the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doOptions(String url, final Map<String, String> headers,
                                  final String payload, String contentType) throws IOException {
        HttpUriRequest request = new HttpOptions(url);
        setHeaders(headers, request);
        if(payload != null) {
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
        return client.execute(request);
    }

    /**
     * Send a HTTP Head request to the specified URL
     *
     * @param url         Target endpoint URL
     * @param headers     Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doHead(String url, final Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpHead(url);
        setHeaders(headers, request);
        return client.execute(request);
    }

    /**
     * Send a HTTP DELETE request to the specified URL
     *
     * @param url         Target endpoint URL
     * @param headers     Any HTTP headers that should be added to the request
     * @return Returned HTTP response
     * @throws IOException If an error occurs while making the invocation
     */
    public HttpResponse doDelete(String url, final Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpDelete(url);
        setHeaders(headers, request);
        return client.execute(request);
    }

    /**
     * Send a HTTP PUT request to the specified URL
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
        setHeaders(headers, request);
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
        return client.execute(request);
    }

    private void setHeaders(Map<String, String> headers, HttpUriRequest request) {
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }
    }

}
