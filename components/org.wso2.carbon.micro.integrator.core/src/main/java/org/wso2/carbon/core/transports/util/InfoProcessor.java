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

package org.wso2.carbon.core.transports.util;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.wso2.carbon.core.transports.CarbonHttpRequest;
import org.wso2.carbon.core.transports.CarbonHttpResponse;
import org.wso2.carbon.core.transports.HttpGetRequestProcessor;

/**
 *
 */
public class InfoProcessor implements HttpGetRequestProcessor {

    public void process(CarbonHttpRequest request,
                        CarbonHttpResponse response,
                        ConfigurationContext configurationContext) throws Exception {
        String requestURI = request.getRequestURI();
        String contextPath = configurationContext.getServiceContextPath();
        String serviceName =
                requestURI.substring(requestURI.indexOf(contextPath) + contextPath.length() + 1);
        AxisService axisService =
                configurationContext.getAxisConfiguration().getServiceForActivation(serviceName);
        if (!RequestProcessorUtil.canExposeServiceMetadata(axisService)) {
            response.setError(HttpStatus.SC_FORBIDDEN,
                              "Access to service metadata for service: " + serviceName +
                              " has been forbidden");
            return;
        }
        String serviceHtml = ServiceHTMLProcessor.printServiceHTML(
                serviceName, configurationContext);
        if (serviceHtml != null) {
            response.setStatus(HttpStatus.SC_OK);
            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
            response.getOutputStream().write(serviceHtml.getBytes());
        }
    }
}
