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
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.local.LocalTransportSender;
import org.wso2.carbon.base.ServletRequestHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CarbonLocalTransportSender extends LocalTransportSender {
    public void finalizeSendWithToAddress(MessageContext msgContext, ByteArrayOutputStream out)
            throws AxisFault {
        try {
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            ByteArrayOutputStream response = new ByteArrayOutputStream();

//            CarbonLocalTransportReceiver localTransportReceiver =
//                    new CarbonLocalTransportReceiver(this, isNonBlocking());
            CarbonLocalTransportReceiver localTransportReceiver =
                    new CarbonLocalTransportReceiver(msgContext.getConfigurationContext(),
                                                     isNonBlocking());
            //set the servlet requests here
            msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST,
                    ServletRequestHolder.getServletRequest());
            
            localTransportReceiver.processMessage(msgContext, in, response);

            in.close();
            out.close();
            if (response.size() > 0) {
                in = new ByteArrayInputStream(response.toByteArray());
                msgContext.setProperty(MessageContext.TRANSPORT_IN, in);
            }
        } catch (IOException e) {
            throw AxisFault.makeFault(e);
        }
    }
}
