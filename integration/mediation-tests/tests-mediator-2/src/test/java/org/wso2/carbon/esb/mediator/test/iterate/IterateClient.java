/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.iterate;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/*
 * This class can be used to send a simple stock quote request and get the
 * response as a string. Response will contain entire SOAP envelope.
 * Default StockQuote client gives only the first OMElement. So this can be used
 * in situations which we need all the SOAP envelope.
 */

public class IterateClient {

    /**
     * @param address
     * @param symbol
     * @return
     * @throws java.io.IOException
     */
    public String getResponse(String address, String symbol) throws IOException {
        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(address, null, createSimpleQuoteRequestBody(symbol), "urn:getQuote");
        } finally {
            operationClient.destroy();
        }
        Assert.assertNotNull(response, "Response null");
        return response.toString();

    }

    public String send(String address, OMElement payload, String soapAction) throws IOException {
        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(address, null, payload, soapAction);
        } finally {
            operationClient.destroy();
        }
        Assert.assertNotNull(response, "Response null");
        return response.toString();

    }

    /**
     * This is used to send a multiple stockquote request
     *
     * @param address service address
     * @param symbol  stock quote symbol
     * @return
     * @throws java.io.IOException
     */
    public String getMultipleResponse(String address, String symbol, int iterations)
            throws IOException {

        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(address, null, createMultipleQuoteRequestBody(symbol, iterations), "urn:getQuote");
        } finally {
            operationClient.destroy();
        }
        Assert.assertNotNull(response, "Response null");

        return response.toString();

    }

    /**
     * This is used to send a multiple custom stockquote request
     *
     * @param address service address
     * @param symbol  stock quote symbol
     * @return
     * @throws java.io.IOException
     */
    public String getMultipleCustomResponse(String address, String symbol, int iterations)
            throws IOException {

        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(address, null, createMultipleCustomQuoteRequestBody(symbol, iterations), "urn:getQuote");
        } finally {
            operationClient.destroy();
        }
        Assert.assertNotNull(response, "Response null");

        return response.toString();

    }

    /**
     * This is used to send a request which contains several getquote elements
     *
     * @param address service address
     * @param symbol  stock quote symbol
     * @return
     * @throws java.io.IOException
     */
    public String getGetQuotesResponse(String address, String symbol, int iterations)
            throws IOException {

        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(address, null, createGetQuotesRequestBody(symbol, iterations), "urn:getQuote");
        } finally {
            operationClient.destroy();
        }
        Assert.assertNotNull(response, "Response null");

        return response.toString();

    }

    private OMElement createSimpleQuoteRequestBody(String symbol) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);
        OMElement value1 = fac.createOMElement("request", omNs);
        OMElement value2 = fac.createOMElement("symbol", omNs);
        value2.addChild(fac.createOMText(value1, symbol));
        value1.addChild(value2);
        method.addChild(value1);
        return method;
    }


    public OMElement toOMElement(String s) throws XMLStreamException {
        return AXIOMUtil.stringToOM(s);
    }

    private OMElement createMultipleQuoteRequestBody(String symbol, int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
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

    private OMElement createMultipleCustomQuoteRequestBody(String symbol, int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement chkPrice = fac.createOMElement("CheckPriceRequest", omNs);
            OMElement code = fac.createOMElement("Code", omNs);
            chkPrice.addChild(code);
            code.setText(symbol);
            method.addChild(chkPrice);
        }
        return method;
    }


    private OMElement createGetQuotesRequestBody(String symbol, int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement top = fac.createOMElement("getQuotes", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement method = fac.createOMElement("getQuote", omNs);
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, symbol));
            value1.addChild(value2);
            method.addChild(value1);
            top.addChild(method);
        }

        return top;
    }

}
