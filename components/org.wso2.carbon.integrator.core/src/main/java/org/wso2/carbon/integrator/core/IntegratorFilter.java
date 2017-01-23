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

package org.wso2.carbon.integrator.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IntegratorFilter implements Filter {

    private static final Log log = LogFactory.getLog(IntegratorFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String uri = httpRequest.getRequestURI();
        if (!validateURI(uri)) {
            String integratorHeader = httpRequest.getHeader(Constants.INTEGRATOR_HEADER);
            if (Utils.validateHeader(integratorHeader, uri)) {
                filterChain.doFilter(new HeaderMapRequestWrapper(httpRequest), servletResponse);
            } else {
                ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "the error message");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }

    private boolean validateURI(String contextPath) {
        //skipping carbon context uri
        if(contextPath.contains("/carbon")) {
            return true;
        }

        if(contextPath.contains("/fileupload")) {
            return true;
        }

        if(contextPath.endsWith("?tryit")) {
            return true;
        }

        return false;
    }
}
