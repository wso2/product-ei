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

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisBindingOperation;
import org.apache.axis2.description.AxisEndpoint;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.dispatchers.HTTPLocationBasedDispatcher;
import org.apache.axis2.dispatchers.RequestURIBasedDispatcher;
import org.apache.axis2.dispatchers.RequestURIBasedServiceDispatcher;
import org.apache.axis2.dispatchers.RequestURIOperationDispatcher;
import org.apache.axis2.engine.AbstractDispatcher;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.util.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.core.axis2.SynapseDispatcher;
import org.apache.synapse.transport.passthru.util.RelayUtils;
import org.wso2.carbon.integrator.core.Utils;
import org.wso2.carbon.webapp.mgt.WebApplication;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Map;

/**
 * This Axis2 handler is written to dispatch messages to synapse environment, when the message is
 * received for a stateful service.
 */
public class IntegratorStatefulHandler extends AbstractDispatcher {
    private static final String BUILDER_INVOKED = "message.builder.invoked";
    private static final String NAME = "IntegratorStatefulHandler";
    private static final Log log = LogFactory.getLog(IntegratorSynapseHandler.class);
    private SynapseDispatcher synapseDispatcher = new SynapseDispatcher();
    private RequestURIBasedServiceDispatcher rubsd = new RequestURIBasedServiceDispatcher();
    private static RequestURIOperationDispatcher requestURIOperationDispatcher = new RequestURIOperationDispatcher();
    private static HTTPLocationBasedDispatcher httpLocationBasedDispatcher = new HTTPLocationBasedDispatcher();
    private static RequestURIBasedDispatcher requestDispatcher = new RequestURIBasedDispatcher();

    public IntegratorStatefulHandler() {
    }

    @Override
    public AxisOperation findOperation(AxisService axisService, MessageContext messageContext) throws AxisFault {
        return null;
    }

    @Override
    public InvocationResponse invoke(MessageContext msgctx) throws AxisFault {

        String uri = (String) msgctx.getProperty("TransportInURL");

        if (msgctx.getAxisService() == null) {
            msgctx.setAxisService(rubsd.findService(msgctx));
        }

        if (msgctx.getProperty("transport.http.servletRequest") == null && uri != null && uri.contains("/odata/")) {
            //since all the tenant related requests are handling from the odata web app,
            // tenant request needs to be routed to the web app
            WebApplication webApplication;
            webApplication = Utils.getStartedWebapp(msgctx.getProperty("TransportInURL").toString());
            if (webApplication != null) {
                msgctx.setProperty("IsODataService", true);
                setSynapseContext(msgctx, msgctx.getAxisService());
            }
        } else if (Utils.isDataService(msgctx)) {
            try {
                // dispatchAndVerify need to call to find out the service and the operation.
                dispatchAndVerify(msgctx);
                if (!Boolean.TRUE.equals(msgctx.getProperty(BUILDER_INVOKED))) {
                    RelayUtils.buildMessage(msgctx);
                    msgctx.setProperty(BUILDER_INVOKED, Boolean.TRUE);
                }
                String type = null;
                // message type.
                // If thats not present,
                // Setting the received content-type as the messageType to make
                // sure that we respond using the received message serialization format.
                AxisConfiguration configuration = msgctx.getConfigurationContext().getAxisConfiguration();

                Object contentNegotiation = configuration
                        .getParameterValue(Constants.Configuration.ENABLE_HTTP_CONTENT_NEGOTIATION);
                if (JavaUtils.isTrueExplicitly(contentNegotiation)) {
                    Map transportHeaders = (Map) msgctx.getProperty(MessageContext.TRANSPORT_HEADERS);
                    if (transportHeaders != null) {
                        String acceptHeader = (String) transportHeaders.get(HTTPConstants.HEADER_ACCEPT);
                        if (acceptHeader != null) {
                            int index = acceptHeader.indexOf(";");
                            if (index > 0) {
                                acceptHeader = acceptHeader.substring(0, index);
                            }
                            String[] headers = acceptHeader.split(",");
                            for (String header : headers) {
                                String accept = header.trim();
                                // We dont want dynamic content negotoatin to work on text.xml as its
                                // ambiguos as to whether the user requests SOAP 1.1 or POX response
                                if (!HTTPConstants.MEDIA_TYPE_TEXT_XML.equals(accept) &&
                                        configuration.getMessageFormatter(accept) != null) {
                                    type = header;
                                    break;
                                }
                            }
                        }
                    }
                }
                msgctx.setProperty(Constants.Configuration.MESSAGE_TYPE, type);
            } catch (IOException e) {
                throw new AxisFault("I/O  Exception occured while building the data service request", e);
            } catch (XMLStreamException e) {
                throw new AxisFault("Exception occured while building the data service request as an XML", e);
            }
        }
        return InvocationResponse.CONTINUE;
    }

    private void setSynapseContext(MessageContext messageContext, AxisService originalAxisService) throws AxisFault {
        AxisService axisService = synapseDispatcher.findService(messageContext);
        if (log.isDebugEnabled()) {
            log.debug("AxisService is changing from " + originalAxisService.getName() + " to " +
                    axisService.getName());
        }
        messageContext.setAxisService(axisService);
        messageContext.setAxisOperation(synapseDispatcher.findOperation(messageContext.getAxisService(),
                messageContext));
    }

    @Override
    public AxisService findService(MessageContext messageContext) throws AxisFault {
        return this.rubsd.findService(messageContext);
    }

    @Override
    public void initDispatcher() {
        this.init(new HandlerDescription(NAME));
    }

    /**
     * Finds axis Service and the Operation for DSS requests
     *
     * @param msgContext request message context
     * @throws AxisFault if any exception occurs while finding axis service or operation
     */
    private static void dispatchAndVerify(MessageContext msgContext) throws AxisFault {
        requestDispatcher.invoke(msgContext);
        AxisService axisService = msgContext.getAxisService();
        if (axisService != null) {
            httpLocationBasedDispatcher.invoke(msgContext);
            if (msgContext.getAxisOperation() == null) {
                requestURIOperationDispatcher.invoke(msgContext);
            }

            AxisOperation axisOperation;
            if ((axisOperation = msgContext.getAxisOperation()) != null) {
                AxisEndpoint axisEndpoint =
                        (AxisEndpoint) msgContext.getProperty(WSDL2Constants.ENDPOINT_LOCAL_NAME);
                if (axisEndpoint != null) {
                    AxisBindingOperation axisBindingOperation = (AxisBindingOperation) axisEndpoint
                            .getBinding().getChild(axisOperation.getName());
                    msgContext.setProperty(Constants.AXIS_BINDING_OPERATION, axisBindingOperation);
                }
                msgContext.setAxisOperation(axisOperation);
            }
        }
    }

}
