/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.util.XMLUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.config.xml.XMLConfigConstants;
import org.apache.synapse.config.xml.endpoints.EndpointFactory;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.endpoints.Endpoint;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.CarbonConfigurationContextFactory;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import org.wso2.carbon.integrator.core.handler.EndpointHolder;
import org.wso2.carbon.integrator.core.internal.IntegratorComponent;
import org.wso2.carbon.tomcat.api.CarbonTomcatService;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.carbon.webapp.mgt.WebApplication;
import org.wso2.carbon.webapp.mgt.WebApplicationsHolder;
import org.wso2.carbon.webapp.mgt.utils.WebAppUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;

public class Utils {

    private static OMFactory fac = OMAbstractFactory.getOMFactory();
    private static final QName ENDPOINT_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "endpoint");
    private static final QName ADDRESS_Q = new QName(SynapseConstants.SYNAPSE_NAMESPACE, "address");
    private static OMElement endpoint = fac.createOMElement(ENDPOINT_Q);
    private static OMElement address = fac.createOMElement(ADDRESS_Q);
    private static OMElement axis2Config;

    public static int getProtocolPort(String protocol) {
        CarbonTomcatService webAppAdminService;
        webAppAdminService = IntegratorComponent.getCarbonTomcatService();
        if (webAppAdminService == null) {
            throw new RuntimeException("CarbonTomcatService service is not available.");
        }
        return webAppAdminService.getPort(protocol);
    }

    /**
     * Get the details of a deployed webApp
     *
     * @param path URI path
     * @return meta data for webapp
     */
    public static WebApplication getStartedWebapp(String path) {
        Map<String, WebApplicationsHolder> webApplicationsHolderMap = WebAppUtils.getAllWebappHolders
                (CarbonConfigurationContextFactory.getConfigurationContext());
        WebApplication matchedWebApplication;
        for (WebApplicationsHolder webApplicationsHolder : webApplicationsHolderMap.values()) {
            for (WebApplication webApplication : webApplicationsHolder.getStartedWebapps().values()) {
                if (path.contains(webApplication.getContextName())) {
                    matchedWebApplication = webApplication;
                    return matchedWebApplication;
                }
            }
        }
        return null;
    }

    /**
     * Get the details of a deployed webapp for tenants
     *
     * @param path URI path
     * @return meta data for webapp
     */
    public static WebApplication getStartedTenantWebapp(String tenantDomain, String path) {
        ConfigurationContextService contextService = IntegratorComponent.getContextService();
        ConfigurationContext configContext;
        ConfigurationContext tenantContext;
        if (null != contextService) {
            // Getting server's configContext instance
            configContext = contextService.getServerConfigContext();
            tenantContext = TenantAxisUtils.getTenantConfigurationContext(tenantDomain, configContext);
            Map<String, WebApplicationsHolder> webApplicationsHolderMap = WebAppUtils.getAllWebappHolders(tenantContext);
            WebApplication matchedWebApplication;
            for (WebApplicationsHolder webApplicationsHolder : webApplicationsHolderMap.values()) {
                for (WebApplication webApplication : webApplicationsHolder.getStartedWebapps().values()) {
                    if (path.contains(webApplication.getContextName())) {
                        matchedWebApplication = webApplication;
                        return matchedWebApplication;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the details of a deployed webapp for tenants
     *
     * @param serviceURL URI path
     * @return meta data for webapp
     */
    private static AxisService getTenantAxisService(String tenant, String serviceURL) throws AxisFault {
        ConfigurationContextService contextService = IntegratorComponent.getContextService();
        ConfigurationContext configContext;
        ConfigurationContext tenantContext;
        if (null != contextService) {
            // Getting server's configContext instance
            configContext = contextService.getServerConfigContext();
            String[] urlparts = serviceURL.split("/");
            //urlpart[0] is tenant domain
            tenantContext = TenantAxisUtils.getTenantConfigurationContext(tenant, configContext);
            AxisService tenantAxisService = tenantContext.getAxisConfiguration().getService(urlparts[1]);
            if (tenantAxisService == null) {
                AxisServiceGroup axisServiceGroup = tenantContext.getAxisConfiguration().getServiceGroup(urlparts[1]);
                if (axisServiceGroup != null) {
                    return axisServiceGroup.getService(urlparts[2]);
                } else {
                    // for dss samples
                    return tenantContext.getAxisConfiguration().getService(urlparts[2]);
                }
            } else {
                return tenantAxisService;
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
            return "/".concat(temp[1]).toLowerCase(Locale.getDefault());
        } else {
            return null;
        }
    }

    private static String getUniqueRequestID(String uri) {
        String input = uri + System.getProperty(CarbonConstants.START_TIME);
        return UUID.nameUUIDFromBytes(input.getBytes(Charset.defaultCharset())).toString();
    }

    public static String getDSSJsonBuilder() throws IOException, XMLStreamException {
        String dssJsonBuilder = getPropertyFromAxisConf(Constants.DATASERVICE_JSON_BUILDER);
        if (dssJsonBuilder == null) {
            return "org.apache.axis2.json.gson.JsonBuilder";
        } else {
            return dssJsonBuilder;
        }
    }

    public static Endpoint createEndpoint(String addressURI, SynapseEnvironment environment) {
        if (EndpointHolder.getInstance().getEndpoint(addressURI) != null) {
            return EndpointHolder.getInstance().getEndpoint(addressURI);
        } else {
            address.addAttribute("uri", addressURI, null);
            endpoint.addChild(address);
            Endpoint ep = EndpointFactory.getEndpointFromElement(endpoint, true, null);
            ep.init(environment);
            EndpointHolder.getInstance().putEndpoint(addressURI, ep);
            return ep;
        }
    }

    public static String getPassThroughJsonBuilder() throws IOException, XMLStreamException {
        String psJsonBuilder = getPropertyFromAxisConf(Constants.PASSTHRU_JSON_BUILDER);
        if (psJsonBuilder == null) {
            return "org.apache.synapse.commons.json.JsonStreamBuilder";
        } else {
            return psJsonBuilder;
        }
    }

    public static String getDSSJsonFormatter() throws IOException, XMLStreamException {
        String dssJsonFormatter = getPropertyFromAxisConf(Constants.DATASERVICE_JSON_FORMATTER);
        if (dssJsonFormatter == null) {
            return "org.apache.axis2.json.gson.JsonFormatter";
        } else {
            return dssJsonFormatter;
        }
    }

    public static String getPassThroughJsonFormatter() throws IOException, XMLStreamException {
        String psJsonFormatter = getPropertyFromAxisConf(Constants.PASSTHRU_JSON_FORMATTER);
        if (psJsonFormatter == null) {
            return "org.apache.synapse.commons.json.JsonStreamFormatter";
        } else {
            return psJsonFormatter;
        }
    }

    private static String getPassThruHttpPort() {
        return CarbonConfigurationContextFactory.getConfigurationContext().getAxisConfiguration().getTransportIn("http").
                getParameter("port").getValue().toString();
    }

    private static String getPassThruHttpsPort() {
        return CarbonConfigurationContextFactory.getConfigurationContext().getAxisConfiguration().getTransportIn
                ("https").getParameter("port").getValue().toString();
    }

    static boolean validateHeader(String key, String uri) {
        String input = uri + System.getProperty(CarbonConstants.START_TIME);
        return (UUID.nameUUIDFromBytes(input.getBytes(Charset.defaultCharset())).toString().equals(key));
    }

    public static void setIntegratorHeader(MessageContext synCtx, String uri) {
        Axis2MessageContext axis2smc = (Axis2MessageContext) synCtx;
        org.apache.axis2.context.MessageContext axis2MessageCtx = axis2smc.getAxis2MessageContext();
        Object headers = axis2MessageCtx.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        if (headers != null && headers instanceof Map) {
            Map headersMap = (Map) headers;
            headersMap.put(Constants.INTEGRATOR_HEADER, Utils.getUniqueRequestID(uri));
        }
        if (headers == null) {
            Map<String, String> headersMap = new HashMap<String, String>();
            headersMap.put(Constants.INTEGRATOR_HEADER, Utils.getUniqueRequestID(uri));
            axis2MessageCtx.setProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS, headersMap);
        }

    }

    public static boolean isDataService(org.apache.axis2.context.MessageContext messageContext) throws AxisFault {
        AxisService axisService = messageContext.getAxisService();
        if (axisService != null) {
            URL file = axisService.getFileName();
            if (file != null) {
                String filePath = file.getPath();
                return filePath.endsWith(".dbs");
            }
        }else {
            return isTenantDataService(messageContext);
        }
        return false;
    }


    public static boolean isTenantDataService(org.apache.axis2.context.MessageContext messageContext) throws
            AxisFault {
        String url = (String) messageContext.getProperty("TransportInURL");
        if (url != null) {
            if (url.contains(messageContext.getConfigurationContext().getServicePath() + "/t/")) {
                String tenantDomain = TenantAxisUtils.getTenantDomain(url);
                try {
                    PrivilegedCarbonContext.startTenantFlow();
                    PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext
                            .getThreadLocalCarbonContext();
                    privilegedCarbonContext.setTenantDomain(tenantDomain, true);
                    url = url.substring(url.indexOf(tenantDomain));
                    AxisService axisService = getTenantAxisService(tenantDomain, url);
                    if (axisService != null) {
                        URL file = axisService.getFileName();
                        if (file != null) {
                            String filePath = file.getPath();
                            return filePath.endsWith(".dbs");
                        }
                    }
                } finally {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
        }
        return false;
    }

    /**
     * In this method we rewrite the location header which comes from the tomcat transport.
     * @param location location url
     * @param messageContext message context.
     */
    public static void rewriteLocationHeader(String location, MessageContext messageContext) {
        if (location.contains(":")) {
            String[] tmp = location.split(":");
            if (tmp.length <= 2) {
                return;
            }
            String protocol = tmp[0];
            String host = null;
            for (String tmpname : tmp[1].split("/")) {
                if (!tmpname.isEmpty()) {
                    host = tmpname;
                    break;
                }
            }
            String newPort;
            String port = null;
            if ("http".equals(protocol)) {
                newPort = getPassThruHttpPort();
            } else {
                newPort = getPassThruHttpsPort();
            }
            if (tmp.length > 2) {
                port = tmp[2].substring(0, tmp[2].indexOf("/"));
            }
            String oldEndpoint = protocol + "://" + host + ":" + port;
            // In this block we check whether this endpoint is already known endpoint.
            if (EndpointHolder.getInstance().containsEndpoint(oldEndpoint)) {
                location = location.replace(port, newPort);
                Object headers = ((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty
                        ("TRANSPORT_HEADERS");
                if (headers instanceof TreeMap) {
                    ((TreeMap) headers).put("Location", location);
                }
            }
        }
    }

    /**
     * In this method check we check whether that particular service is a admin service or session enabled service.
     *
     * @param axisService AxisService
     * @return isStatefulService boolean
     */
    public static boolean isStatefulService(AxisService axisService) {
        Parameter parameter = axisService.getParameter("adminService");
        return (parameter != null && "true".equals(parameter.getValue())) || ("transportsession".equals(axisService.getScope()));
    }

    private static String getPropertyFromAxisConf(String parameter) throws IOException, XMLStreamException {
        try (InputStream file = new FileInputStream(Paths.get(CarbonBaseUtils.getCarbonConfigDirPath(), "axis2",
                "axis2.xml").toString())) {
           if(axis2Config == null) {
               OMElement element = (OMElement) XMLUtils.toOM(file);
               element.build();
               axis2Config = element;
           }
            Iterator parameters = axis2Config.getChildrenWithName(new QName("parameter"));
            while (parameters.hasNext()) {
                OMElement parameterElement = (OMElement) parameters.next();
                if (parameter.equals(parameterElement.getAttribute(new QName("name")).getAttributeValue())) {
                    return parameterElement.getText();
                }
            }
            return null;
        } catch (IOException | XMLStreamException e) {
            throw e;
        }
    }

}
