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


import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * MeteredServletRequest is to use to track the request message sizes and the bandwidth.
 * Here getInputStream method is overloaded and returning a custom InputStream which count
 * the number of bytes transferred. The getReadSize() method returns the size of the request
 * in bytes. 
 * @see MeteringInputStream
 */
public class MeteredServletRequest implements HttpServletRequest {
	HttpServletRequest wrappedHttpServletRequest;
	MeteringInputStream wrappedInputStream;


	public MeteredServletRequest(HttpServletRequest wrappedHttpServletRequest) {
		this.wrappedHttpServletRequest = wrappedHttpServletRequest;
	}

	public String getAuthType() {
	    return wrappedHttpServletRequest.getAuthType();
	}

	public String getContextPath() {
	    return wrappedHttpServletRequest.getContextPath();
	}

	public Cookie[] getCookies() {
	    return wrappedHttpServletRequest.getCookies();
	}

	public long getDateHeader(String arg0) {
	    return wrappedHttpServletRequest.getDateHeader(arg0);
	}

	public String getHeader(String arg0) {
	    return wrappedHttpServletRequest.getHeader(arg0);
	}

	public Enumeration getHeaderNames() {
	    return wrappedHttpServletRequest.getHeaderNames();
	}

	public Enumeration getHeaders(String arg0) {
	    return wrappedHttpServletRequest.getHeaders(arg0);
	}

	public int getIntHeader(String arg0) {
	    return wrappedHttpServletRequest.getIntHeader(arg0);
	}

	public String getMethod() {
	    return wrappedHttpServletRequest.getMethod();
	}

	public String getPathInfo() {
	    return wrappedHttpServletRequest.getPathInfo();
	}

	public String getPathTranslated() {
	    return wrappedHttpServletRequest.getPathTranslated();
	}

	public String getQueryString() {
	    return wrappedHttpServletRequest.getQueryString();
	}

	public String getRemoteUser() {
	    return wrappedHttpServletRequest.getRemoteUser();
	}

	public String getRequestedSessionId() {
	    return wrappedHttpServletRequest.getRequestedSessionId();
	}

	public String getRequestURI() {
	    return wrappedHttpServletRequest.getRequestURI();
	}

	public StringBuffer getRequestURL() {
	    return wrappedHttpServletRequest.getRequestURL();
	}

	public String getServletPath() {
	    return wrappedHttpServletRequest.getServletPath();
	}

	public HttpSession getSession() {
	    return wrappedHttpServletRequest.getSession();
	}

	public HttpSession getSession(boolean arg0) {
	    return wrappedHttpServletRequest.getSession(arg0);
	}

	public Principal getUserPrincipal() {
	    return wrappedHttpServletRequest.getUserPrincipal();
	}

	public boolean isRequestedSessionIdFromCookie() {
	    return wrappedHttpServletRequest.isRequestedSessionIdFromCookie();
	}

	public boolean isRequestedSessionIdFromUrl() {
	    return wrappedHttpServletRequest.isRequestedSessionIdFromUrl();
	}

    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return wrappedHttpServletRequest.authenticate(httpServletResponse);
    }

    public void login(String s, String s1) throws ServletException {
        wrappedHttpServletRequest.login(s, s1);
    }

    public void logout() throws ServletException {
        wrappedHttpServletRequest.logout();
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        return wrappedHttpServletRequest.getParts();
    }

    public Part getPart(String s) throws IOException, ServletException {
        return wrappedHttpServletRequest.getPart(s);
    }

    public boolean isRequestedSessionIdFromURL() {
	    return wrappedHttpServletRequest.isRequestedSessionIdFromURL();
	}

	public boolean isRequestedSessionIdValid() {
	    return wrappedHttpServletRequest.isRequestedSessionIdValid();
	}

	public boolean isUserInRole(String arg0) {
	    return wrappedHttpServletRequest.isUserInRole(arg0);
	}

	public Object getAttribute(String arg0) {
	    return wrappedHttpServletRequest.getAttribute(arg0);
	}

	public Enumeration getAttributeNames() {
	    return wrappedHttpServletRequest.getAttributeNames();
	}

	public String getCharacterEncoding() {
	    return wrappedHttpServletRequest.getCharacterEncoding();
	}

	public int getContentLength() {
	    return wrappedHttpServletRequest.getContentLength();
	}

	public String getContentType() {
	    return wrappedHttpServletRequest.getContentType();
	}

	public ServletInputStream getInputStream() throws IOException {
		if (wrappedInputStream == null) {
			ServletInputStream is = wrappedHttpServletRequest.getInputStream();
			wrappedInputStream = new MeteringInputStream(is);
		}
		return wrappedInputStream;
	}

	public String getLocalAddr() {
	    return wrappedHttpServletRequest.getLocalAddr();
	}

	public Locale getLocale() {
	    return wrappedHttpServletRequest.getLocale();
	}

	public Enumeration getLocales() {
	    return wrappedHttpServletRequest.getLocales();
	}

	public String getLocalName() {
	    return wrappedHttpServletRequest.getLocalName();
	}

	public int getLocalPort() {
	    return wrappedHttpServletRequest.getLocalPort();
	}

    public ServletContext getServletContext() {
        return wrappedHttpServletRequest.getServletContext();
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return wrappedHttpServletRequest.startAsync();
    }

    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return wrappedHttpServletRequest.startAsync();
    }

    public boolean isAsyncStarted() {
        return wrappedHttpServletRequest.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        return wrappedHttpServletRequest.isAsyncSupported();
    }

    public AsyncContext getAsyncContext() {
        return wrappedHttpServletRequest.getAsyncContext();
    }

    public DispatcherType getDispatcherType() {
        return wrappedHttpServletRequest.getDispatcherType();
    }

    public String getParameter(String arg0) {
	    return wrappedHttpServletRequest.getParameter(arg0);
	}

	public Map getParameterMap() {
	    return wrappedHttpServletRequest.getParameterMap();
	}

	public Enumeration getParameterNames() {
	    return wrappedHttpServletRequest.getParameterNames();
	}

	public String[] getParameterValues(String arg0) {
	    return wrappedHttpServletRequest.getParameterValues(arg0);
	}

	public String getProtocol() {
	    return wrappedHttpServletRequest.getProtocol();
	}

	public BufferedReader getReader() throws IOException {
	    return wrappedHttpServletRequest.getReader();
	}

	public String getRealPath(String arg0) {
	    return wrappedHttpServletRequest.getRealPath(arg0);
	}

	public String getRemoteAddr() {
	    return wrappedHttpServletRequest.getRemoteAddr();
	}

	public String getRemoteHost() {
	    return wrappedHttpServletRequest.getRemoteHost();
	}

	public int getRemotePort() {
	    return wrappedHttpServletRequest.getRemotePort();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
	    return wrappedHttpServletRequest.getRequestDispatcher(arg0);
	}

	public String getScheme() {
	    return wrappedHttpServletRequest.getScheme();
	}

	public String getServerName() {
	    return wrappedHttpServletRequest.getServerName();
	}

	public int getServerPort() {
	    return wrappedHttpServletRequest.getServerPort();
	}

	public boolean isSecure() {
	    return wrappedHttpServletRequest.isSecure();
	}

	public void removeAttribute(String arg0) {
	    wrappedHttpServletRequest.removeAttribute(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
	    wrappedHttpServletRequest.setAttribute(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
	    wrappedHttpServletRequest.setCharacterEncoding(arg0);
	}

	/**
	 * Get the read size
	 * @return the size of the read content
	 */
	public long getReadSize() {
		return wrappedInputStream.getReadSize();
	}
}
