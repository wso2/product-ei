/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.carbon.micro.integrator.admin.apis;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.ProxyService;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.rest.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import javax.xml.stream.XMLStreamException;

/**
 * Helper class to retrieve details of the proxy-services and API's running on the runtime.
 */
public class AdminApiHelper extends AbstractMediator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminApiHelper.class);

    private static final String REQUEST_PARAM = "request";

    private static final String GET_ALL_PROXY_SERVICES = "get-all-proxy-services";

    private static final String GET_ALL_APIS = "get-all-apis";

    private static final String ROOT_ELEMENT = "<jsonObject></jsonObject>";

    private static final String PROXY_ROOT_ELEMENT = "<proxyServices></proxyServices>";

    private static final String PROXY_ELEMENT = "<proxyService></proxyService>";

    private static final String API_ROOT_ELEMENT = "<apis></apis>";

    private static final String API_ELEMENT = "<api></api>";

    private static final String NAME_ELEMENT = "<name></name>";

    private static final String URL_ELEMENT = "<url></url>";

    public boolean mediate(MessageContext context) {
        String requestType = (String) context.getProperty(REQUEST_PARAM);

        LOGGER.debug("API details request for {} ", requestType);
        try {
            if (GET_ALL_PROXY_SERVICES.equals(requestType)) {
                populateProxyServiceDetails(context);
            } else if (GET_ALL_APIS.equals(requestType)) {
                populateApiDetails(context);
            } else {
                LOGGER.warn("Invalid request type: {}", requestType);
                return true;
            }
            org.apache.axis2.context.MessageContext axis2MessageContext =
                    ((Axis2MessageContext) context).getAxis2MessageContext();
            axis2MessageContext.removeProperty("NO_ENTITY_BODY");
        } catch (XMLStreamException e) {
            LOGGER.error("Error occurred while processing response", e);
            return false;
        }
        return true;
    }

    private void populateProxyServiceDetails(MessageContext context) throws XMLStreamException {
        SynapseConfiguration configuration = context.getConfiguration();
        Collection<ProxyService> proxyServices = configuration.getProxyServices();
        OMElement rootElement = AXIOMUtil.stringToOM(ROOT_ELEMENT);
        OMElement proxyRootElement = AXIOMUtil.stringToOM(PROXY_ROOT_ELEMENT);
        rootElement.addChild(proxyRootElement);

        org.apache.axis2.context.MessageContext axis2MessageContext =
                ((Axis2MessageContext) context).getAxis2MessageContext();
        Object restUrlPrefix = context.getProperty("REST_URL_PREFIX");
        for (ProxyService proxyService: proxyServices) {
            OMElement proxyElement = AXIOMUtil.stringToOM(PROXY_ELEMENT);
            OMElement nameElement = AXIOMUtil.stringToOM(NAME_ELEMENT);
            OMElement urlElement = AXIOMUtil.stringToOM(URL_ELEMENT);

            nameElement.setText(proxyService.getName());
            urlElement.setText(restUrlPrefix + "/services/" + proxyService.getName() + "?wsdl");
            proxyElement.addChild(nameElement);
            proxyElement.addChild(urlElement);
            proxyRootElement.addChild(proxyElement);
        }
        axis2MessageContext.getEnvelope().getBody().addChild(rootElement);
    }

    private void populateApiDetails(MessageContext context) throws XMLStreamException {
        SynapseConfiguration configuration = context.getConfiguration();

        OMElement rootElement = AXIOMUtil.stringToOM(ROOT_ELEMENT);
        OMElement apiRootElement = AXIOMUtil.stringToOM(API_ROOT_ELEMENT);
        rootElement.addChild(apiRootElement);

        org.apache.axis2.context.MessageContext axis2MessageContext =
                ((Axis2MessageContext) context).getAxis2MessageContext();
        Object restUrlPrefix = context.getProperty("REST_URL_PREFIX");
        Collection<API> apis = configuration.getAPIs();
        for (API api: apis) {

            OMElement apiElement = AXIOMUtil.stringToOM(API_ELEMENT);
            OMElement nameElement = AXIOMUtil.stringToOM(NAME_ELEMENT);
            OMElement urlElement = AXIOMUtil.stringToOM(URL_ELEMENT);

            String apiName = api.getAPIName();
            nameElement.setText(apiName);
            urlElement.setText(restUrlPrefix + api.getContext());

            apiElement.addChild(nameElement);
            apiElement.addChild(urlElement);
            apiRootElement.addChild(apiElement);
        }

        axis2MessageContext.getEnvelope().getBody().addChild(rootElement);
    }

    @Override
    public boolean isContentAltering() {
        return true;
    }

}
