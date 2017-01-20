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
package org.wso2.carbon.esb.tcp.transport.test.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.File;

public class TcpClient {
    private static final Log log = LogFactory.getLog(TcpClient.class);
    private ConfigurationContext cfgCtx;
    private ServiceClient serviceClient;
    public final String CONTENT_TYPE_TEXT_XML = "text/xml";
    public final String CONTENT_TYPE_APPLICATIONS_SOAP_XML = "application/soap+xml";

    public TcpClient() {
        String repositoryPath = /*ProductConstant.getModuleClientPath()*/FrameworkPathUtil.getSystemResourceLocation()+File.separator+"client";


        File repository = new File(repositoryPath);
        try {
            cfgCtx =
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem(repository.getCanonicalPath(),
                                                                                         /*ProductConstant.getResourceLocations(ProductConstant.ESB_SERVER_NAME)*/FrameworkPathUtil.getSystemResourceLocation()+File.separator + "artifacts"+File.separator + "ESB"
                                                                                         + File.separator + "tcp" + File.separator + "transport" + File.separator + "client_axis2.xml");
            serviceClient = new ServiceClient(cfgCtx, null);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public OMElement send12(String trpUrl, String action, OMElement payload, String contentType)
            throws AxisFault {

        Options options = new Options();
        options.setTo(new EndpointReference(trpUrl));
        options.setTransportInProtocol(Constants.TRANSPORT_TCP);
        options.setAction("urn:" + action);

        options.setProperty(Constants.Configuration.MESSAGE_TYPE, contentType);
        options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        serviceClient.engageModule(Constants.MODULE_ADDRESSING);

        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(payload);

        return result;

    }

    public OMElement sendSimpleStockQuote12(String trpUrl, String symbol, String contentType)
            throws AxisFault {
        return send12(trpUrl, "getQuote", createStandardRequest(symbol), contentType);
    }

    private OMElement createStandardRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);
        OMElement value1 = fac.createOMElement("request", omNs);
        OMElement value2 = fac.createOMElement("symbol", omNs);

        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);

        return method;
    }

}
