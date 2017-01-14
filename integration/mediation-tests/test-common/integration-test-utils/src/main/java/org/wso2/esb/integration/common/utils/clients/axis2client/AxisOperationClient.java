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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;

public class AxisOperationClient {
    private static final Log log = LogFactory.getLog(AxisOperationClient.class);
    private MessageContext outMsgCtx;
    private ConfigurationContext cfgCtx;
    private ServiceClient serviceClient;
    private OperationClient operationClient;
    private SOAPFactory fac;
    private SOAPEnvelope envelope;

    public AxisOperationClient() {


        String repositoryPath = System.getProperty(ServerConstants.CARBON_HOME) + File.separator +
                "samples" + File.separator + "axis2Server" + File.separator + "repository";
        File repository = new File(repositoryPath);
        log.info("Using the Axis2 repository path: " + repository.getAbsolutePath());

        try {
            cfgCtx =
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem(repository.getCanonicalPath(),
                            null);
            serviceClient = new ServiceClient(cfgCtx, null);
            log.info("Sample clients initialized successfully...");
        } catch (Exception e) {
            log.error("Error while initializing the Operational Client", e);
        }
    }

    /**
     * @param trpUrl
     * @param addUrl
     * @param symbol
     * @param iterations
     * @return
     * @throws java.io.IOException
     */
    public OMElement sendMultipleQuoteRequest(String trpUrl, String addUrl, String symbol,
                                              int iterations)
            throws IOException {
        return createMultipleQuoteRequest(trpUrl, addUrl, symbol, iterations);
    }

    /**
     *
     * @param trpUrl
     * @param addUrl
     * @param payload
     * @param action
     * @return   soap envelop
     * @throws org.apache.axis2.AxisFault
     */

    public OMElement send(String trpUrl, String addUrl, OMElement payload, String action) throws AxisFault {
        operationClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
        setMessageContext(addUrl, trpUrl, action);
        outMsgCtx.setEnvelope(createSOAPEnvelope(payload));
        operationClient.addMessageContext(outMsgCtx);
        operationClient.execute(true);
        MessageContext inMsgtCtx = operationClient.getMessageContext("In");
        SOAPEnvelope response = inMsgtCtx.getEnvelope();
        return response;
    }

    /**
     * Creating the multiple quote request body
     *
     * @param symbol
     * @param iterations
     * @return
     */
    private OMElement createMultipleQuoteRequestBody(String symbol, int iterations) {
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, symbol));
            value1.addChild(value2);
            method.addChild(value1);
        }
        return method;
    }

    /**
     * Creating the multiple quote request
     *
     * @param trpUrl
     * @param addUrl
     * @param symbol
     * @param iterations
     * @return
     * @throws java.io.IOException
     */
    private OMElement createMultipleQuoteRequest(String trpUrl, String addUrl, String symbol,
                                                 int iterations) throws IOException {
        operationClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
        setMessageContext(addUrl, trpUrl, null);
        outMsgCtx.setEnvelope(createSOAPEnvelope(symbol, iterations));
        operationClient.addMessageContext(outMsgCtx);
        operationClient.execute(true);
        MessageContext inMsgtCtx = operationClient.getMessageContext("In");
        SOAPEnvelope response = inMsgtCtx.getEnvelope();
        return response;

    }

    /**
     * creating the message context of the soap message
     *
     * @param addUrl
     * @param trpUrl
     *  @param action
     */
    private void setMessageContext(String addUrl, String trpUrl, String action) {
        outMsgCtx = new MessageContext();
        //assigning message context&rsquo;s option object into instance variable
        Options options = outMsgCtx.getOptions();
        //setting properties into option
        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        }
        if (addUrl != null && !"null".equals(addUrl)) {
            options.setTo(new EndpointReference(addUrl));
        }
        if(action != null && !"null".equals(action)) {
            options.setAction(action);
        }
    }

    /**
     * Create the soap envelop
     *
     * @param symbol
     * @param iterations
     * @return
     */
    private SOAPEnvelope createSOAPEnvelope(String symbol, int iterations) {
        fac = OMAbstractFactory.getSOAP11Factory();
        envelope = fac.getDefaultEnvelope();
        envelope.getBody().addChild(createMultipleQuoteRequestBody(symbol, iterations));
        return envelope;
    }

    /**
     * @param payload
     * @return
     */

    private SOAPEnvelope createSOAPEnvelope(OMElement payload) {
        fac = OMAbstractFactory.getSOAP11Factory();
        envelope = fac.getDefaultEnvelope();
        envelope.getBody().addChild(payload);
        return envelope;
    }

    /**
     *   Destroy objects
     */
    public void destroy() {
        try {
            serviceClient.cleanup();
            cfgCtx.cleanupContexts();
            cfgCtx.terminate();
        } catch (AxisFault axisFault) {
            log.error("Error while cleaning up the service clients", axisFault);
        }
        outMsgCtx = null;
        serviceClient = null;
        operationClient = null;
        cfgCtx = null;
        envelope = null;
        fac = null;
        
    }
}
