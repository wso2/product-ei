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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * This class is written to wrap HTTP Servlet Request. because we had to remove the integration header.
 */
public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    /**
     * construct a wrapper for this request.
     *
     * @param request servlet Request
     */
    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (Constants.INTEGRATOR_HEADER.equals(name)) {
            return null;
        }
        return headerValue;
    }

    /**
     * get the Header names
     */
    @Override
    public Enumeration getHeaderNames() {
        ArrayList names = Collections.list(super.getHeaderNames());
        names.remove(Constants.INTEGRATOR_HEADER);
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration getHeaders(String name) {
        ArrayList values = Collections.list(super.getHeaders(name));
        values.remove(Constants.INTEGRATOR_HEADER);
        return Collections.enumeration(values);
    }
}
