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

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.CarbonConfigurationContextFactory;
import org.wso2.carbon.tomcat.api.CarbonTomcatService;
import org.wso2.carbon.webapp.mgt.WebApplication;
import org.wso2.carbon.webapp.mgt.WebApplicationsHolder;
import org.wso2.carbon.webapp.mgt.utils.WebAppUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {

    public static int getProtocolPort(String protocol) {
        CarbonTomcatService webAppAdminService;
        webAppAdminService = (CarbonTomcatService) PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(CarbonTomcatService.class, null);
        if (webAppAdminService == null) {
            throw new RuntimeException("CarbonTomcatService service is not available.");
        }
        return webAppAdminService.getPort(protocol);
    }

    /**
     * Get the details of a deplyed webapp
     *
     * @param path
     * @param hostName
     * @return meta data for webapp
     */
    public static WebApplication getStartedWebapp(String path, String hostName) {
        Map<String, WebApplicationsHolder> webApplicationsHolderMap = WebAppUtils.getAllWebappHolders(CarbonConfigurationContextFactory.getConfigurationContext());
        WebApplication matchedWebApplication = null;
        for (WebApplicationsHolder webApplicationsHolder : webApplicationsHolderMap.values()) {
            for (WebApplication webApplication : webApplicationsHolder.getStartedWebapps().values()) {
                if (webApplication.getContextName().equals(path) && webApplication.getHostName().equals(hostName)) {
                    matchedWebApplication = webApplication;
                    return matchedWebApplication;
                }
            }
        }
        return null;
    }

    public static String getHostname(String host) {
        return host.split(":")[0];
    }

    public static String getContext(String uri) {
        String[] temp = uri.split("/");
        if (temp.length >= 2) {
            return "/".concat(temp[1]).toLowerCase();
        } else {
            return null;
        }
    }

    public static String getUniqueRequestID(String uri) {
        String input = uri + System.getProperty(CarbonConstants.START_TIME);
        return UUID.nameUUIDFromBytes(input.getBytes()).toString();
    }

    public static boolean isClustered() {
        return false;
    }

    public static String getServicePath() {
        return "/" + CarbonConfigurationContextFactory.getConfigurationContext().getAxisConfiguration().getParameter(Constants.SERVICE_PATH).getValue().toString();
    }

    public static boolean validateHeader(String key, String uri) {
        String input = uri + System.getProperty(CarbonConstants.START_TIME);
        return (UUID.nameUUIDFromBytes(input.getBytes()).toString().equals(key));
    }

    public static void setIntegratorHeader(MessageContext synCtx) {
        String uri = synCtx.getTo().getAddress();
        Axis2MessageContext axis2smc = (Axis2MessageContext) synCtx;
        org.apache.axis2.context.MessageContext axis2MessageCtx = axis2smc.getAxis2MessageContext();
        Object headers = axis2MessageCtx.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        if (headers != null && headers instanceof Map) {
            Map headersMap = (Map) headers;
            headersMap.put(Constants.INTEGRATOR_HEADER, Utils.getUniqueRequestID(uri));
        }
        if (headers == null) {
            Map headersMap = new HashMap();
            headersMap.put(Constants.INTEGRATOR_HEADER, Utils.getUniqueRequestID(uri));
            axis2MessageCtx.setProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS, headersMap);
        }

    }

    public static boolean isDataService(org.apache.axis2.context.MessageContext messageContext) {
        String filePath = messageContext.getAxisService().getFileName().getPath();
        return filePath.contains("dataservices");
    }

}
