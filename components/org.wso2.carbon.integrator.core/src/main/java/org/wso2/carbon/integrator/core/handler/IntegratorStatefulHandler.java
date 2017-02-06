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
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.dispatchers.RequestURIBasedServiceDispatcher;
import org.apache.axis2.engine.AbstractDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

/**
 *
 */
public class IntegratorStatefulHandler extends AbstractDispatcher {
    public static final String NAME = "IntegratorStatefulHandler";
    private static final Log log = LogFactory.getLog(IntegratorSynapseHandler.class);
    private static Object synapseHandler;
    private static Method synapseHandler_findOperationMethod;
    private static Method synapseHandler_findServiceMethod;
    private RequestURIBasedServiceDispatcher rubsd = new RequestURIBasedServiceDispatcher();

    public IntegratorStatefulHandler() {
    }

    static {
        try {
            Class<?> synapseHandlerClass = IntegratorStatefulHandler.class.getClassLoader().loadClass("org.apache.synapse.core.axis2.SynapseDispatcher");
            synapseHandler = synapseHandlerClass.newInstance();
            synapseHandler_findServiceMethod = synapseHandlerClass.getMethod("findService", MessageContext.class);
            synapseHandler_findOperationMethod = synapseHandlerClass.getMethod("findOperation", AxisService.class, MessageContext.class);
        } catch (Exception e) {
            log.error("Error occurred while initializing IntegratorStatefulHandler");
        }
    }

    @Override
    public AxisOperation findOperation(AxisService axisService, MessageContext messageContext) throws AxisFault {
        if (isStatefulService(axisService) && messageContext.getProperty("transport.http.servletRequest") == null) {
            try {
                messageContext.setAxisService((AxisService) synapseHandler_findServiceMethod.invoke(synapseHandler, messageContext));
                messageContext.setProperty("raplacedAxisService","true");
                return (AxisOperation) synapseHandler_findOperationMethod.invoke(synapseHandler, messageContext.getAxisService(), messageContext);
            } catch (Exception e) {
                log.error("Error occurred while invoking stateful service.");
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public AxisService findService(MessageContext messageContext) throws AxisFault {
        return this.rubsd.findService(messageContext);
    }

    @Override
    public void initDispatcher() {
        this.init(new HandlerDescription(NAME));
    }

    private boolean isStatefulService(AxisService axisService) {
        Parameter parameter = axisService.getParameter("adminService");
        return parameter != null && ("transportsession".equals(axisService.getScope()) || "true".equals(axisService.getParameter("adminService").getValue()));
    }

}
