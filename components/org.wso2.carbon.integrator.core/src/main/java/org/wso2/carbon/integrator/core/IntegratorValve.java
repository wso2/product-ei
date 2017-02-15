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

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.integrator.core.handler.IntegratorSynapseHandler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * This Valve is written to block the direct requests to the tomcat servlet transport. This valve allows only requests
 * which has the integrator header. This integrator header is set by the synapse handler.
 */
public class IntegratorValve extends ValveBase {
    private static final Log log = LogFactory.getLog(IntegratorSynapseHandler.class);

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            uri = uri + '?' + queryString;
        }
        String integratorHeader = request.getHeader(Constants.INTEGRATOR_HEADER);
        if (Utils.validateHeader(integratorHeader, uri)) {
            getNext().invoke(request, response);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Blocking request :" + request.toString());
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "This port is closed.");
        }
    }
}
