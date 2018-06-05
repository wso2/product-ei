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
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.core.transports.CarbonHttpRequest;
import org.wso2.carbon.core.transports.CarbonHttpResponse;
import org.wso2.carbon.core.transports.HttpGetRequestProcessor;
import org.wso2.carbon.core.util.SystemFilter;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.deployment.GhostDeployerUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public abstract class AbstractWsdlProcessor implements HttpGetRequestProcessor {

    protected void printWSDL(ConfigurationContext configurationContext,
                             String serviceName,
                             CarbonHttpResponse response,
                             WSDLPrinter wsdlPrinter) throws IOException {
        AxisConfiguration axisConfig = configurationContext.getAxisConfiguration();
        AxisService axisService = axisConfig.getServiceForActivation(serviceName);
        if (GhostDeployerUtils.isGhostService(axisService) && axisConfig != null) {
            String serviceGroupName = null;
            if (axisService != null) {
                if (axisService.getAxisServiceGroup() != null) {
                    serviceGroupName = axisService.getAxisServiceGroup().getServiceGroupName();
                } else {
                    serviceGroupName = axisService.getName();
                }
            }
            // if the existing service is a ghost service, deploy the actual one
            axisService = GhostDeployerUtils.deployActualService(axisConfig, axisService);
            // we have to call the metadata deployer to avoid any qos settings are not
            // getting applied
//            GhostDispatcherUtils.deployServiceMetaFile(serviceGroupName, axisConfig);

        }

        String hideAdminServiceWSDLs =
                CarbonCoreDataHolder.getInstance().
                        getServerConfigurationService().getFirstProperty(CarbonConstants.AXIS2_CONFIG_PARAM +
                                                                         "." + CarbonConstants.HIDE_ADMIN_SERVICE_WSDLS);

        OutputStream outputStream = response.getOutputStream();
        if (axisService != null) {
            if (SystemFilter.isFilteredOutService(axisService) &&
                    "true".equals(hideAdminServiceWSDLs)) {
                response.setError(HttpStatus.SC_FORBIDDEN,
                        "Access to service metadata for service: " + serviceName +
                                " has been forbidden");
                return;
            }

            if(!RequestProcessorUtil.canExposeServiceMetadata(axisService)){
                response.setError(HttpStatus.SC_FORBIDDEN,
                                  "Access to service metadata for service: " + serviceName +
                                  " has been forbidden");
                return;
            }
            if (!axisService.isActive()) {
                response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                outputStream.write(("<h4>Service " +
                                    serviceName +
                                    " is inactive. Cannot display WSDL document.</h4>").getBytes());
                outputStream.flush();
            } else {
                response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
                wsdlPrinter.printWSDL(axisService);
            }
        } else {
            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
            outputStream.write(("<h4>Service " + serviceName +
                    " not found. Cannot display WSDL document.</h4>").getBytes());
            response.setError(HttpStatus.SC_NOT_FOUND);
            outputStream.flush();
        }
    }

    /**
     * This method check for annotation=true query param. If it is available
     * this method return true, otherwise false.
     *
     * @param request HTTP request
     * @return boolean if annotation is present
     */
    protected boolean checkForAnnotation(CarbonHttpRequest request) {
        String parameter = request.getParameter(ServerConstants.HTTPConstants.ANNOTATION);
        if (parameter != null && parameter.length() != 0) {
            if (parameter.equals("true")) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method will return the value of the parameter
     *
     * @param request Http request
     * @param paramName name of the parameter
     * @return String
     */
    protected String getImportedWSDL(CarbonHttpRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);

        if (paramValue != null && paramValue.length() != 0) {
            return paramValue;
        }
        return "";

    }

    protected interface WSDLPrinter {
        void printWSDL(AxisService axisService) throws IOException;
    }
}
