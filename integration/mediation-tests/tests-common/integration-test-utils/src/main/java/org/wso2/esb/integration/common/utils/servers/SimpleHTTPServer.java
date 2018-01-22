/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.esb.integration.common.utils.servers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Simple HTTP server
 * This simple server wraps {@link HttpServer}
 */
public class SimpleHTTPServer {

    private static Log log = LogFactory.getLog(SimpleHTTPServer.class);
    private HttpServer httpServer;

    public SimpleHTTPServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
    }

    /**
     * Creates a HttpContext. A HttpContext represents a mapping from a URI path to a exchange handler on wrapped
     * {@link HttpServer}. This will create handler that respond with provided HTTP headers and HTTP payload
     *
     * @param context context path
     * @param httpHeaders HTTP response headers
     * @param responsePayload HTTP response payload
     * @return creted {@link HttpContext}
     */
    public HttpContext createContext(String context, Map<String, String> httpHeaders, String responsePayload) {
        return httpServer.createContext(context, new DefaultResponseHandler(httpHeaders, responsePayload));
    }

    /**
     * Creates a HttpContext. A HttpContext represents a mapping from a URI path to a exchange handler on wrapped
     * HttpServer. Once created, all requests received by the server for the path will be handled by calling the given
     * handler object.
     *
     * @param context context path
     * @param handler {@link HttpHandler} implementation
     * @return created {@link HttpContext}
     */
    public HttpContext createContext(String context, HttpHandler handler) {
        return httpServer.createContext(context, handler);
    }

    /**
     * Start the HTTP Server
     */
    public void start() {
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();
        log.info("SimpleHTTPServer started at " + httpServer.getAddress().getHostName() + ":" + httpServer.getAddress()
                .getPort());
    }

    /**
     * Stop the HTTP Server
     */
    public void stop() {
        httpServer.stop(0);
        log.info("SimpleHTTPServer stopped");
    }

    /**
     * Unwrap underlying HttpServer instance
     * @return HttpServer instance
     */
    public HttpServer unwrap() {
        return httpServer;
    }

    /**
     * HttpHandler implementation to handle request
     */
    private static class DefaultResponseHandler implements HttpHandler {

        Map<String, String> responseHeaders;
        String responsePayload;

        DefaultResponseHandler(Map<String, String> responseHeaders, String responsePayload) {
            this.responseHeaders = responseHeaders;
            this.responsePayload = responsePayload;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            //Add headers to response message
            if (responseHeaders != null) {
                Headers headers = httpExchange.getResponseHeaders();
                for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
                    headers.add(entry.getKey(), entry.getValue());
                }
            }
            httpExchange.sendResponseHeaders(200, responsePayload.length());

            //Add response payload
            OutputStream responseStream = httpExchange.getResponseBody();
            try {
                responseStream.write(responsePayload.getBytes(Charset.defaultCharset()));
            } finally {
                responseStream.close();
            }
        }
    }
}
