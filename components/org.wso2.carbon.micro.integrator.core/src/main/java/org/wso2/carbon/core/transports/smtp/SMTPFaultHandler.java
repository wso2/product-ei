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
package org.wso2.carbon.core.transports.smtp;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;

/**
 * This will handle faults for SMTP case.
 */
public class SMTPFaultHandler extends AbstractHandler {
    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        return InvocationResponse.CONTINUE;
    }


    public void flowComplete(MessageContext msgContext) {
        String protocol = msgContext.getIncomingTransportName();
        if (protocol == null) {
            return;
        }

        if (protocol.equalsIgnoreCase(Constants.TRANSPORT_MAIL) && msgContext.isServerSide()) {
            // This will allow the faults to go out.
            msgContext.setServerSide(false);
        }

    }
}
