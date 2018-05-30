/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

package org.wso2.carbon.core.transports.http;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.transport.AbstractTransportListener;

/**
 *
 */
public class HttpTransportListener extends AbstractTransportListener {
    private Log log = LogFactory.getLog(org.wso2.carbon.core.transports.http.HttpTransportListener.class);

    public void init(ConfigurationContext configContext,
                     TransportInDescription transportIn) throws AxisFault {
        super.init(configContext, transportIn);
    }

    @Override
    public void start() throws AxisFault {
        super.start();
        if (System.getProperty(ServerConstants.REPO_WRITE_MODE, "false").equals("false") &&
                port != -1) {
            log.info("HTTP port        : " + port);
        }
    }

    public EndpointReference getEPRForService(String serviceName,
                                              String ip) throws AxisFault {
        return getEPR(ServerConstants.HTTP_TRANSPORT, serviceName, ip);
    }

    public EndpointReference[] getEPRsForService(String serviceName,
                                                 String ip) throws AxisFault {
        return new EndpointReference[]{getEPRForService(serviceName, ip)};
    }
}
