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
import org.apache.axis2.description.PolicyInclude;
import org.apache.axis2.util.ExternalPolicySerializer;
import org.apache.http.protocol.HTTP;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyReference;
import org.apache.neethi.PolicyRegistry;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.core.transports.CarbonHttpRequest;
import org.wso2.carbon.core.transports.CarbonHttpResponse;
import org.wso2.carbon.core.transports.HttpGetRequestProcessor;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.carbon.utils.deployment.GhostDeployerUtils;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
 */
public class PolicyProcessor implements HttpGetRequestProcessor {

    public void process(CarbonHttpRequest request,
                        CarbonHttpResponse response,
                        ConfigurationContext configCtx) throws Exception {
        String requestURI = request.getRequestURI();
        String contextPath = configCtx.getServiceContextPath();
        String serviceName = requestURI.substring(requestURI.indexOf(contextPath) + contextPath.length() + 1);
        AxisService axisService =
                configCtx.getAxisConfiguration().getServiceForActivation(serviceName);
        OutputStream outputStream = response.getOutputStream();
        if (axisService != null) {
            if (GhostDeployerUtils.isGhostService(axisService)) {
                // if the existing service is a ghost service, deploy the actual one
                axisService = GhostDeployerUtils.deployActualService(configCtx
                        .getAxisConfiguration(), axisService);
            }
            if (!axisService.isActive()) {
                response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                outputStream.write(("<h4>Service " +
                                    serviceName +
                                    " is inactive. Cannot display policies.</h4>").getBytes());
                outputStream.flush();
            } else {
                PolicyInclude policyInclude = axisService.getPolicyInclude();
                if (policyInclude == null) {
                    response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                    outputStream.write("<h4>Policy element is not found!</h4>".getBytes());
                    outputStream.flush();
                    return;

                }
                ArrayList policyElements = policyInclude.getPolicyElements();

                if (policyElements == null) {
                    response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                    outputStream.write("<h4>Policy elements not found!</h4>".getBytes());
                    outputStream.flush();
                    return;
                }

                PolicyRegistry reg = policyInclude.getPolicyRegistry();

                ExternalPolicySerializer serializer = new ExternalPolicySerializer();
                if (configCtx.getAxisConfiguration()
                        .getLocalPolicyAssertions() != null) {
                    serializer.setAssertionsToFilter(
                            configCtx.getAxisConfiguration().getLocalPolicyAssertions());
                }


                if (policyElements.size() == 1) {
                    response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
                    if (policyElements.get(0) instanceof Policy) {
                        Policy policy = (Policy) policyElements.get(0);
                        serializer.serialize(policy, outputStream);
                    } else if (policyElements.get(0) instanceof PolicyReference) {
                        String key = ((PolicyReference) policyElements.get(0)).getURI();
                        if (key.startsWith("#")) {
                            key = key.substring(key.indexOf('#') + 1);
                        }

                        Policy p = reg.lookup(key);
                        if (p == null) {
                            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                            outputStream.write("<h4>Policy element not found!</h4>".getBytes());
                        } else {
                            serializer.serialize(p, outputStream);
                        }
                    }
                } else {
                    // for many policies
                    String idParam = request.getParameter("id");
                    if (idParam != null) {
                        Object policyObject = policyElements.get(Integer.parseInt(idParam));
                        if (policyObject != null) {
                            response.addHeader(HTTP.CONTENT_TYPE, "text/xml");
                            if (policyObject instanceof Policy) {
                                Policy policy = (Policy) policyObject;
                                serializer.serialize(policy, outputStream);
                            } else if (policyObject instanceof PolicyReference) {
                                String key = ((PolicyReference) policyObject).getURI();
                                if (key.startsWith("#")) {
                                    key = key.substring(key.indexOf('#') + 1);
                                }
                                Policy p = reg.lookup(key);
                                if (p == null) {
                                    response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                                    outputStream
                                            .write("<h4>Policy element not found!</h4>".getBytes());
                                } else {
                                    serializer.serialize(p, outputStream);
                                }
                            }
                        } else {
                            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                            outputStream.write("<h4>Policy not found!</h4>".getBytes());
                        }
                    } else {
                        if (policyElements.size() == 0) {
                            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
                            outputStream.write("<h4>Policy not found!</h4>".getBytes());
                        } else {
                            String ipAddress = "http://" + NetworkUtils.getLocalHostname() + ":" +
                                               CarbonUtils.getTransportPort(configCtx, "http");
                            ServerConfigurationService serverCofig =
                                    CarbonCoreDataHolder.getInstance().getServerConfigurationService();
                            outputStream.write(("<html><head>" +
                                                "<title>WSO2 Server v" +
                                                serverCofig.getFirstProperty("Version") +
                                                " Management Console - " +
                                                axisService.getName() +
                                                " Service Policies</title>" +
                                                "</head>" +
                                                "<body>" +
                                                "<b>Policies for " +
                                                axisService.getName() +
                                                " service</b><br/><br/>").getBytes());
                            if (policyElements.size() != 0) {
                                String serviceContextPath = RequestProcessorUtil.getServiceContextPath(configCtx);
                                for (int i = 0; i < policyElements.size(); i++) {
                                    String st = "<a href=\"" + ipAddress +
                                                serviceContextPath + "/" +
                                                axisService.getName() + "?policy&id=" + i +
                                                "\">Policy " + i +
                                                "</a><br/>";
                                    outputStream.write(st.getBytes());
                                }
                            } else {
                                outputStream.write("<h4>No policies found</h4>".getBytes());
                            }
                            outputStream.write("</body></html>".getBytes());
                        }

                    }
                }
            }
        } else { // Service is null
            response.addHeader(HTTP.CONTENT_TYPE, "text/html");
            outputStream.write(("<h4>Service " + serviceName +
                                " not found. Cannot display policies.</h4>").getBytes());
            outputStream.flush();
        }
        outputStream.flush();
    }
}
