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

package org.wso2.carbon.integrator.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.HTTP;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.builtin.SendMediator;
import org.apache.synapse.transport.passthru.core.PassThroughSenderManager;
import org.wso2.carbon.integrator.core.Utils;
import org.wso2.carbon.integrator.core.internal.IntegratorComponent;
import org.wso2.carbon.webapp.mgt.WebApplication;

import java.util.TreeMap;

/**
 * This
 */
public class IntegratorSynapseHandler extends AbstractSynapseHandler {

    private static final Log log = LogFactory.getLog(IntegratorSynapseHandler.class);

    public IntegratorSynapseHandler() {
        this.sendMediator = new SendMediator();
    }

    private SendMediator sendMediator;
    private PassThroughSenderManager passThroughSenderManager = PassThroughSenderManager.getInstance();

    @Override
    public boolean handleRequestInFlow(MessageContext messageContext) {
        boolean isPreserveHeadersContained = false;
        try {
            org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            if (axis2MessageContext.getProperty("TransportInURL") != null) {
                String uri = axis2MessageContext.getProperty("TransportInURL").toString();
                String protocol = (String) messageContext.getProperty("TRANSPORT_IN_NAME");
                String host;
                String contextPath = Utils.getContext(uri);
                Object headers = ((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");
                if (headers instanceof TreeMap) {
                    host = Utils.getHostname((String) ((TreeMap) headers).get("Host"));
                    if (validateWhiteListsWithUri(uri) || "/odata".equals(contextPath) || uri.endsWith("?tryit")) {
                        isPreserveHeadersContained = true;
                        String endpoint = protocol + "://" + host + ":" + Utils.getProtocolPort(protocol);
                        return dispatchMessage(endpoint, uri, messageContext);
                    } else if (axis2MessageContext.getProperty("raplacedAxisService") != null) {
                        isPreserveHeadersContained = true;
                        String endpoint = protocol + "://" + host + ":" + Utils.getProtocolPort(protocol) + messageContext.getTo().getAddress();
                        return dispatchMessage(endpoint, uri, messageContext);
                    } else {
                        Object tenantDomain = ((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty("tenantDomain");
                        if (tenantDomain != null) {
                            WebApplication webApplication = Utils.getStartedTenantWebapp(tenantDomain.toString(), uri);
                            if (webApplication != null) {
                                isPreserveHeadersContained = true;
                                String endpoint = protocol + "://" + host + ":" + Utils.getProtocolPort(protocol);
                                return dispatchMessage(endpoint, uri, messageContext);
                            }
                        } else {
                            WebApplication webApplication = Utils.getStartedWebapp(uri);
                            if (webApplication != null) {
                                isPreserveHeadersContained = true;
                                String endpoint = protocol + "://" + host + ":" + Utils.getProtocolPort(protocol);
                                return dispatchMessage(endpoint, uri, messageContext);
                            }
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            this.handleException("Error occurred in integrator handler.", e, messageContext);
            return true;
        } finally {
            if (isPreserveHeadersContained) {
                if (passThroughSenderManager != null && passThroughSenderManager.getSharedPassThroughHttpSender() != null) {
                    passThroughSenderManager.getSharedPassThroughHttpSender().removePreserveHttpHeader(HTTP.USER_AGENT);
                }
            }
        }
    }

    @Override
    public boolean handleRequestOutFlow(MessageContext messageContext) {
        return true;
    }

    @Override
    public boolean handleResponseInFlow(MessageContext messageContext) {
        Object headers = ((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");
        if (headers instanceof TreeMap) {
            String locationHeader = (String) ((TreeMap) headers).get("Location");
            if (locationHeader != null) {
                Utils.rewriteLocationHeader(locationHeader, messageContext);
            }
        }
        return true;
    }

    @Override
    public boolean handleResponseOutFlow(MessageContext messageContext) {
        return true;
    }

    private void handleException(String msg, Exception e, MessageContext msgContext) {
        log.error(msg, e);
        if (msgContext.getServiceLog() != null) {
            msgContext.getServiceLog().error(msg, e);
        }
        throw new SynapseException(msg, e);
    }

    private void setREST_URL_POSTFIX(org.apache.axis2.context.MessageContext messageContext, String to) {
        if (messageContext.getProperty("REST_URL_POSTFIX") != null) {
            messageContext.setProperty("REST_URL_POSTFIX", to);
        }
    }

    private boolean dispatchMessage(String endpoint, String uri, MessageContext messageContext) {
        if (passThroughSenderManager != null && passThroughSenderManager.getSharedPassThroughHttpSender() != null) {
            passThroughSenderManager.getSharedPassThroughHttpSender().addPreserveHttpHeader(HTTP.USER_AGENT);
        }
        Utils.setIntegratorHeader(messageContext, uri);
        setREST_URL_POSTFIX(((Axis2MessageContext) messageContext).getAxis2MessageContext(), uri);
        sendMediator.setEndpoint(Utils.createEndpoint(endpoint, messageContext.getEnvironment()));
        return sendMediator.mediate(messageContext);
    }

    private boolean validateWhiteListsWithUri(String uri) {
        for (String contextPath : IntegratorComponent.getWhiteListContextPaths()) {
            if (uri.contains(contextPath)) {
                return true;
            }
        }
        return false;
    }
}
