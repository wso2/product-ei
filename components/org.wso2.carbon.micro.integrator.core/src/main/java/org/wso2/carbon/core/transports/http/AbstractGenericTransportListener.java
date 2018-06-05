/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.core.transports.http;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.transport.TransportListener;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.carbon.utils.SessionContextUtil;

import java.net.SocketException;

/**
 * This is the abstract transport listener that should be used in writing generic listeners.
 */
public abstract class AbstractGenericTransportListener implements TransportListener {
    protected int proxyPort = -1;

    protected ConfigurationContext configurationContext;

    public AbstractGenericTransportListener() {
    }

    public AbstractGenericTransportListener(ConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

    public void destroy() {
        this.configurationContext = null;
    }

    public SessionContext getSessionContext(MessageContext messageContext) {
        return SessionContextUtil.createSessionContext(messageContext);
    }


    public void stop() throws AxisFault {

    }

    protected EndpointReference genEpr(String protocol, String ip, int port,
                                       String serviceContextPath,
                                       String serviceName) throws AxisFault {
        try {
            if (ip == null) {
                ip = NetworkUtils.getLocalHostname();
            }
            String tmp = protocol + "://" + ip;
            if (proxyPort == 80 || proxyPort == 443) {
                tmp += serviceContextPath + "/" + serviceName;
            } else if (proxyPort > -1) {
                tmp += ":" + proxyPort + serviceContextPath + "/" + serviceName;
            } else {
                tmp += ":" + port + serviceContextPath + "/" + serviceName;
            }
            return new EndpointReference(tmp + "/");
        } catch (SocketException e) {
            throw AxisFault.makeFault(e);
        }
    }


}
