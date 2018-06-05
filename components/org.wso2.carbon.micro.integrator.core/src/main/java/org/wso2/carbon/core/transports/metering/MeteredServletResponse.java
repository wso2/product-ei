/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.core.transports.metering;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * MeteredServletResponse is to use to track the response message sizes and the bandwidth.
 * Here getOutputStream method is overloaded and returning a custom OutputStream which count
 * the number of bytes transferred. The getWrittenSize() method returns the size of the response
 * in bytes after the response is processed and written to the response object. 
 * @see MeteringOutputStream
 */
public class MeteredServletResponse implements HttpServletResponse {
	HttpServletResponse wrappedHttpServletResponse;
	MeteringOutputStream wrappedOutputStream;

	public MeteredServletResponse(HttpServletResponse wrappedHttpServletResponse) {
		this.wrappedHttpServletResponse = wrappedHttpServletResponse;
	}

	public void addCookie(Cookie arg0) {
		wrappedHttpServletResponse.addCookie(arg0);
	}

	public void addDateHeader(String arg0, long arg1) {
		wrappedHttpServletResponse.addDateHeader(arg0, arg1);
	}

	public void addHeader(String arg0, String arg1) {
		wrappedHttpServletResponse.addHeader(arg0, arg1);
	}

	public void addIntHeader(String arg0, int arg1) {
		wrappedHttpServletResponse.addIntHeader(arg0, arg1);
	}

	public boolean containsHeader(String arg0) {
		return wrappedHttpServletResponse.containsHeader(arg0);
	}

	public String encodeRedirectUrl(String arg0) {
		return wrappedHttpServletResponse.encodeRedirectUrl(arg0);
	}

	public String encodeRedirectURL(String arg0) {
		return wrappedHttpServletResponse.encodeRedirectURL(arg0);
	}

	public String encodeUrl(String arg0) {
		return wrappedHttpServletResponse.encodeUrl(arg0);
	}

	public String encodeURL(String arg0) {
		return wrappedHttpServletResponse.encodeURL(arg0);
	}

	public void sendError(int arg0, String arg1) throws IOException {
		wrappedHttpServletResponse.sendError(arg0, arg1);
	}

	public void sendError(int arg0) throws IOException {
		wrappedHttpServletResponse.sendError(arg0);
	}

	public void sendRedirect(String arg0) throws IOException {
		wrappedHttpServletResponse.sendRedirect(arg0);
	}

	public void setDateHeader(String arg0, long arg1) {
		wrappedHttpServletResponse.setDateHeader(arg0, arg1);
	}

	public void setHeader(String arg0, String arg1) {
		wrappedHttpServletResponse.setHeader(arg0, arg1);
	}

	public void setIntHeader(String arg0, int arg1) {
		wrappedHttpServletResponse.setIntHeader(arg0, arg1);
	}

	public void setStatus(int arg0, String arg1) {
		wrappedHttpServletResponse.setStatus(arg0, arg1);
	}

    public int getStatus() {
        return wrappedHttpServletResponse.getStatus();
    }

    public String getHeader(String s) {
        return wrappedHttpServletResponse.getHeader(s);
    }

    public Collection<String> getHeaders(String s) {
        return wrappedHttpServletResponse.getHeaders(s);
    }

    public Collection<String> getHeaderNames() {
        return wrappedHttpServletResponse.getHeaderNames();
    }

    public void setStatus(int arg0) {
		wrappedHttpServletResponse.setStatus(arg0);
	}

	public void flushBuffer() throws IOException {
		wrappedHttpServletResponse.flushBuffer();
	}

	public int getBufferSize() {
		return wrappedHttpServletResponse.getBufferSize();
	}

	public String getCharacterEncoding() {
		return wrappedHttpServletResponse.getCharacterEncoding();
	}

	public String getContentType() {
		return wrappedHttpServletResponse.getContentType();
	}

	public Locale getLocale() {
		return wrappedHttpServletResponse.getLocale();
	}

	public ServletOutputStream getOutputStream() throws IOException {
	    if (wrappedOutputStream == null) {
	    	ServletOutputStream os = wrappedHttpServletResponse.getOutputStream();
	    	wrappedOutputStream = new MeteringOutputStream(os);
	    }
	    return wrappedOutputStream;
	}

	public PrintWriter getWriter() throws IOException {
		return wrappedHttpServletResponse.getWriter();
	}

	public boolean isCommitted() {
		return wrappedHttpServletResponse.isCommitted();
	}

	public void reset() {
		wrappedHttpServletResponse.reset();
	}

	public void resetBuffer() {
		wrappedHttpServletResponse.resetBuffer();
	}

	public void setBufferSize(int arg0) {
        try {
            wrappedHttpServletResponse.setBufferSize(arg0);
        } catch (Exception ignored) {
        }
    }

	public void setCharacterEncoding(String arg0) {
		wrappedHttpServletResponse.setCharacterEncoding(arg0);
	}

	public void setContentLength(int arg0) {
		wrappedHttpServletResponse.setContentLength(arg0);
	}

	public void setContentType(String arg0) {
		wrappedHttpServletResponse.setContentType(arg0);
	}

	public void setLocale(Locale arg0) {
		wrappedHttpServletResponse.setLocale(arg0);
	}

	/**
	 * Get the written size
	 * @return the size of the written content
	 */
	public long getWrittenSize() {
		return wrappedOutputStream.geWrittenSize();
	}
}
