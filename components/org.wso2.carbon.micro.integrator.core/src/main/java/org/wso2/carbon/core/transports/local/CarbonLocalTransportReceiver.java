/*
 * Copyright 2009-2010 WSO2, Inc. (http://wso2.com)
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
package org.wso2.carbon.core.transports.local;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.transport.TransportListener;
import org.apache.axis2.transport.local.LocalTransportReceiver;
import org.apache.axis2.transport.local.LocalTransportSender;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.SessionContextUtil;

public class CarbonLocalTransportReceiver extends LocalTransportReceiver implements TransportListener {

    private ConfigurationContext configurationContext = null;

    public CarbonLocalTransportReceiver(){
        //This constructor will only be invoked by Axis2 when it is initializing transports.
        super(null, false);
    }

    public CarbonLocalTransportReceiver(LocalTransportSender sender, boolean nonBlocking) {
        super(sender, nonBlocking);
    }

    public CarbonLocalTransportReceiver(ConfigurationContext configurationContext, boolean nonBlocking) {
        super(nonBlocking, configurationContext);
    }
    
    public void init(ConfigurationContext configurationContext, TransportInDescription transprtIn) throws AxisFault {
        this.configurationContext = configurationContext;
    }

    public void start() throws AxisFault {
    }

    public void stop() throws AxisFault {
    }

    public EndpointReference getEPRForService(String serviceName, String ip) throws AxisFault {
        if (configurationContext == null) {
            return null;
        }
        String serviceContextPath = configurationContext.getServiceContextPath();
        if (serviceContextPath == null) {
            throw new AxisFault("couldn't find service path");
        }
        String epURL = ServerConstants.LOCAL_TRANSPORT + "://" + serviceContextPath + "/" + serviceName;
        return new EndpointReference(epURL + "/");
    }

    public EndpointReference[] getEPRsForService(String serviceName, String ip) throws AxisFault {
        EndpointReference epr = getEPRForService(serviceName, ip);
        if (epr == null) {
            return new EndpointReference[0];
        } else {
            return new EndpointReference[] {epr};
        }
    }

    public SessionContext getSessionContext(MessageContext messageContext) {
        return SessionContextUtil.createSessionContext(messageContext);
    }

    public void destroy() {
    }
}