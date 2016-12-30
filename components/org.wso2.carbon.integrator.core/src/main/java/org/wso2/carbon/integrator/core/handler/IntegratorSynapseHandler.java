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

import org.apache.axis2.addressing.EndpointReference;
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
import org.wso2.carbon.webapp.mgt.WebApplication;

import java.util.TreeMap;

public class IntegratorSynapseHandler extends AbstractSynapseHandler {

    private static final Log log = LogFactory.getLog(IntegratorSynapseHandler.class);

    public IntegratorSynapseHandler()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        this.sendMediator = new SendMediator();
    }

    private SendMediator sendMediator;
    private PassThroughSenderManager configuration = PassThroughSenderManager.getInstance();

    @Override
    public boolean handleRequestInFlow(MessageContext messageContext) {
        try {
            String uri = messageContext.getTo().getAddress();
            String protocol = (String) messageContext.getProperty("TRANSPORT_IN_NAME");
            String host;
            String contextPath = Utils.getContext(uri);
            configuration.getSharedPassThroughHttpSender().addPreserveHttpHeader(HTTP.USER_AGENT);
            if (((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty("TRANSPORT_HEADERS") instanceof TreeMap && contextPath != null) {
                host = Utils.getHostname((String) ((TreeMap) ((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty("TRANSPORT_HEADERS")).get("Host"));
                if ("/odata".equals(contextPath)) {
                    Utils.setIntegratorHeader(messageContext);
                    messageContext.setTo(new EndpointReference(protocol + "://" + host + ":" + Utils.getProtocolPort(protocol) + uri));
                    return sendMediator.mediate(messageContext);
                } else {
                    WebApplication webApplication = Utils.getStartedWebapp(contextPath, host);
                    if (webApplication != null) {
                        Utils.setIntegratorHeader(messageContext);
                        messageContext.setTo(new EndpointReference(protocol + "://" + host + ":" + Utils.getProtocolPort(protocol) + uri));
                        return sendMediator.mediate(messageContext);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            this.handleException("Errr", e, messageContext);
            return true;
        } finally {
            configuration.getSharedPassThroughHttpSender().removePreserveHttpHeader(HTTP.USER_AGENT);
        }
    }

    @Override
    public boolean handleRequestOutFlow(MessageContext messageContext) {
        return true;
    }

    @Override
    public boolean handleResponseInFlow(MessageContext messageContext) {
        return true;
    }

    @Override
    public boolean handleResponseOutFlow(MessageContext messageContext) {
        return true;
    }


    private void handleException(String msg, Exception e, MessageContext msgContext) {
        this.log.error(msg, e);
        if (msgContext.getServiceLog() != null) {
            msgContext.getServiceLog().error(msg, e);
        }
        throw new SynapseException(msg, e);
    }
}
