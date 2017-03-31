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

package org.wso2.carbon.integrator.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.HTTP;
import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2Sender;
import org.apache.synapse.mediators.builtin.SendMediator;
import org.apache.synapse.transport.passthru.core.PassThroughSenderManager;
import org.wso2.carbon.integrator.core.Utils;
import org.wso2.carbon.webapp.mgt.WebApplication;

import java.util.Set;
import java.util.TreeMap;

/**
 * This handler is written to dispatch messages to tomcat servlet transport.
 */
public class IntegratorSynapseHandler extends AbstractSynapseHandler {

    private static final Log log = LogFactory.getLog(IntegratorSynapseHandler.class);

    public IntegratorSynapseHandler() {
        this.sendMediator = new SendMediator();
    }

    private SendMediator sendMediator;
    private PassThroughSenderManager passThroughSenderManager = PassThroughSenderManager.getInstance();

    private static final String MESSAGE_DISPATCHED = "MessageDispatched";

    private static final String RESPONSE_WRITTEN = "RESPONSE_WRITTEN";

    @Override
    public boolean handleRequestInFlow(MessageContext messageContext) {
        boolean isPreserveHeadersContained = false;
        try {
            org.apache.axis2.context.MessageContext axis2MessageContext =
                    ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            Object isODataService = axis2MessageContext.getProperty("IsODataService");
            // In this if block we are skipping proxy services, inbound related message contexts & api.
            if (axis2MessageContext.getProperty("TransportInURL") != null && isODataService != null ) {
                String uri = axis2MessageContext.getProperty("TransportInURL").toString();
                //OData web apps related logic
                WebApplication webApplication = Utils.getStartedWebapp(uri);
                if (webApplication != null) {
                    String protocol = (String) messageContext.getProperty("TRANSPORT_IN_NAME");
                    String host;
                    Object headers = axis2MessageContext.getProperty("TRANSPORT_HEADERS");
                    if (headers instanceof TreeMap) {
                        host = Utils.getHostname((String) ((TreeMap) headers).get("Host"));
                        isPreserveHeadersContained = true;
                        String endpoint = protocol + "://" + host + ":" + Utils.getProtocolPort(protocol);
                        return dispatchMessage(endpoint, uri, messageContext);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            this.handleException("Error occurred in integrator handler.", e, messageContext);
            return true;
        } finally {
            if (isPreserveHeadersContained) {
                if (passThroughSenderManager != null &&
                        passThroughSenderManager.getSharedPassThroughHttpSender() != null) {
                    try {
                        passThroughSenderManager.getSharedPassThroughHttpSender()
                                .removePreserveHttpHeader(HTTP.USER_AGENT);
                        // This catch is added when there is no preserve headers in the PassthoughHttpSender.
                    } catch (ArrayIndexOutOfBoundsException e) {
                        if (log.isDebugEnabled()) {
                            log.debug(
                                    "ArrayIndexOutOfBoundsException exception occurred, when removing preserve " +
                                            "headers.");
                        }
                    }
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

        if ("true".equals(messageContext.getProperty(MESSAGE_DISPATCHED))) {
            //remove the "MessageDispatched" property
            Set keySet = messageContext.getPropertyKeySet();
            keySet.remove(MESSAGE_DISPATCHED);

            // In here, We are rewriting the location header which comes from the particular registered endpoints.
            Object headers =
                    ((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");
            if (headers instanceof TreeMap) {
                String locationHeader = (String) ((TreeMap) headers).get("Location");
                if (locationHeader != null) {
                    Utils.rewriteLocationHeader(locationHeader, messageContext);
                }
            }

            messageContext.setTo(null);
            messageContext.setResponse(true);
            Axis2MessageContext axis2smc = (Axis2MessageContext) messageContext;
            org.apache.axis2.context.MessageContext axis2MessageCtx = axis2smc.getAxis2MessageContext();
            axis2MessageCtx.getOperationContext().setProperty(RESPONSE_WRITTEN, "SKIP");
            Axis2Sender.sendBack(messageContext);
            return false;
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
            if (log.isDebugEnabled()) {
                log.debug("message's REST_URL_POSTFIX is changing from " +
                        messageContext.getProperty("REST_URL_POSTFIX") + " to " + to);
            }
            messageContext.setProperty("REST_URL_POSTFIX", to);
        }
    }

    /**
     * In this method we are dispatching the message to tomcat transport.
     *
     * @param endpoint       Endpoint
     * @param uri            uri
     * @param messageContext message context
     * @return boolean
     */
    private boolean dispatchMessage(String endpoint, String uri, MessageContext messageContext) {
        // Adding preserver Headers
        if (passThroughSenderManager != null && passThroughSenderManager.getSharedPassThroughHttpSender() != null) {
            try {
                passThroughSenderManager.getSharedPassThroughHttpSender().addPreserveHttpHeader(HTTP.USER_AGENT);
                // This catch is added when there is no preserve headers in the PassthoughHttpSender.
            } catch (ArrayIndexOutOfBoundsException e) {
                if (log.isDebugEnabled()) {
                    log.debug("ArrayIndexOutOfBoundsException exception occurred, when adding preserve headers.");
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Dispatching message to " + uri);
        }
        messageContext.setProperty(MESSAGE_DISPATCHED, "true");
        Utils.setIntegratorHeader(messageContext, uri);
        setREST_URL_POSTFIX(((Axis2MessageContext) messageContext).getAxis2MessageContext(), uri);
        sendMediator.setEndpoint(Utils.createEndpoint(endpoint, messageContext.getEnvironment()));
        return sendMediator.mediate(messageContext);
    }
}
