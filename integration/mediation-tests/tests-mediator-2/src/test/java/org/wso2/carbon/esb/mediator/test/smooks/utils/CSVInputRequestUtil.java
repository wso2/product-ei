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
package org.wso2.carbon.esb.mediator.test.smooks.utils;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;



public class CSVInputRequestUtil {
    public OMElement sendReceive(String trpUrl, String csvInput)
            throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response;

        sender = new ServiceClient();
        options = new Options();
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        options.setAction("urn:mediate");

        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        }

        //set Message Formatting to text/plain
        options.setProperty("messageType","text/plain");
        options.setProperty("ContentType","text/plain");

        sender.setOptions(options);

        response = sender.sendReceive(getCustomPayload(csvInput));

        return response;
    }

    private OMElement getCustomPayload(String csvInput) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/commons/ns/payload", "ns");
        OMElement payload = fac.createOMElement("text", omNs);
        payload.setText(csvInput);
        return payload;
    }
}
