/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.ei.dataservice.integration.test.util;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.CommonsTransportHeaders;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.httpclient.Header;

import java.util.ArrayList;

/**
 * helper class to send parallel boxcarring requests to the backend
 */
public class ParallelRequestHelper extends Thread {
    private ServiceClient sender;
    private OMElement payload;

    /**
     * constructor for parallel request helper
     * we can use this for the initial begin boxcarring request as well.(sending sessionCookie null)
     *
     * @param sessionCookie
     * @param operation
     * @param payload
     * @param serviceEndPoint
     * @throws org.apache.axis2.AxisFault
     */
    public ParallelRequestHelper(String sessionCookie, String operation, OMElement payload, String serviceEndPoint) throws AxisFault {
        this.payload = payload;
        sender = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(serviceEndPoint));
        options.setProperty("__CHUNKED__", Boolean.FALSE);
        options.setTimeOutInMilliSeconds(45000L);
        options.setAction("urn:" + operation);
        sender.setOptions(options);
        if (sessionCookie != null && !sessionCookie.isEmpty()) {
            Header header = new Header("Cookie", sessionCookie);
            ArrayList headers = new ArrayList();
            headers.add(header);
            sender.getOptions().setProperty(HTTPConstants.HTTP_HEADERS, headers);
        }
    }

    @Override
    public void run() {
        try {
            sender.sendRobust(payload);
            LockHolder.getInstance().updateRequests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * helper method to send begin boxcar request and return the session
     *
     * @return cookie
     * @throws org.apache.axis2.AxisFault
     */
    public String beginBoxcarReturningSession() throws AxisFault {
        sender.sendReceive(payload);
        MessageContext msgCtx = sender.getLastOperationContext().getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

        CommonsTransportHeaders commonsTransportHeaders = (CommonsTransportHeaders) msgCtx.getProperty(MessageContext.TRANSPORT_HEADERS);
        String cookie = (String) commonsTransportHeaders.get("Set-Cookie");
        return cookie;
    }

    /**
     * helper method to send request and return the result
     *
     * @return response
     * @throws org.apache.axis2.AxisFault
     */
    public OMElement sendRequestAndReceiveResult() throws AxisFault {
        OMElement response = sender.sendReceive(payload);
        return response;
    }
}
