/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.core.transports;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class abstracts a HttpResponse for Carbon framework. By using this class, we can handle Http
 * Responses for different HTTP transports in a unified way.
 */
public class CarbonHttpResponse {
    /** HTTP headers */
    private Map<String, String> headerMap = new HashMap<String, String>();
    /** Output stream */
    private OutputStream os;
    /** Writer created created by wrapping the output stream */
    private PrintWriter writer;
    /** HTTP Status code */
    private int statusCode = 200;
    /** HTTP Status message */
    private String statusMessage;
    /** Weather this is an error */
    private boolean error = false;
    /** true if this is a HTTP redirect */
    private boolean redirect = false;
    /** Redirect URI */
    private String sendRedirect;

    /**
     * Create the CarbonHttpResponse for a incoming HTTP message into carbon.
     * @param os output stream to write data to     
     */
    public CarbonHttpResponse(OutputStream os) {
        this.os = os;
    }

    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    public void removeHeader(String name) {
        headerMap.remove(name);
    }

    public Map<String, String> getHeaders() {
        return headerMap;
    }

    public void setStatus(int statusCode) {        
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public PrintWriter getWriter() {
        return new PrintWriter(writer);
    }

    public void setWriter(PrintWriter out) {
        this.writer = out;
    }

    /**
     * Returns whether an error is created or not
     * @return if this response is in error
     */
    public boolean isError() {
        return error;
    }

    /**
     * Set this response is an HTTP Error response. The status code should be set
     * @param error weather this response is an error response
     */
    public void setError(boolean error) {
        this.error = error;
    }

    public void setError(int errorCode, String errorMessage) {
        error = true;

        this.statusCode = errorCode;
        this.statusMessage = errorMessage;
    }

    public void setError(int errorCode) {
        error = true;
        this.statusCode = errorCode;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public String getRedirect() {
        return sendRedirect;
    }

    public void setRedirect(String sendRedirect) {
        redirect = true;
        this.sendRedirect = sendRedirect;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
