/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.esb.integration.common.utils.clients.axis2client;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

import javax.xml.namespace.QName;


public class AxisServiceClient {
    private static final Log log = LogFactory.getLog(AxisServiceClient.class);

    public OMElement sendReceive(OMElement payload, String endPointReference, String operation)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setTimeOutInMilliSeconds(45000);
            options.setAction("urn:" + operation);
            sender.setOptions(options);

            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        Assert.assertNotNull(response);
        return response;
    }

    // axis2 clients with header setting
    public OMElement sendReceive(OMElement payload, String endPointReference, String operation,
                                 String localName, String ns, String value) throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            sender.addStringHeader(new QName(ns, localName), value);  // Set headers
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setAction("urn:" + operation);

            sender.setOptions(options);

            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        Assert.assertNotNull(response);
        return response;
    }

    public OMElement sendReceive(OMElement payload, String endPointReference, String operation,
                                 String contentType)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setTimeOutInMilliSeconds(45000);
            options.setAction("urn:" + operation);
            options.setProperty(Constants.Configuration.MESSAGE_TYPE, contentType);
            sender.setOptions(options);

            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        Assert.assertNotNull(response);
        return response;
    }

    //one way communication
    public void sendRobust(OMElement payload, String endPointReference, String operation)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        if (log.isDebugEnabled()) {
            log.info("Service Endpoint : " + endPointReference);
            log.info("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setAction("urn:" + operation);

            sender.setOptions(options);

            sender.sendRobust(payload);

        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }

    }

    //one way communication
    public void fireAndForget(OMElement payload, String endPointReference, String operation)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        if (log.isDebugEnabled()) {
            log.info("Service Endpoint : " + endPointReference);
            log.info("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setAction("urn:" + operation);

            sender.setOptions(options);

            sender.fireAndForget(payload);

        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }

    }
}
