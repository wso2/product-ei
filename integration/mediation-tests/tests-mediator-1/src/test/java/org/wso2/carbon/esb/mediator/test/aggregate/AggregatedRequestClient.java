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

package org.wso2.carbon.esb.mediator.test.aggregate;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisOperationClient;

import java.io.IOException;

public class AggregatedRequestClient {

    private String proxyServiceUrl, symbol;
    private int no_of_iterations = 1;

    public void setNoOfIterations(int iterations) {
        this.no_of_iterations = iterations;
    }

    public void setProxyServiceUrl(String url) {
        this.proxyServiceUrl = url;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public String getResponse() throws IOException {
        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(proxyServiceUrl, null,
                                            createMultipleQuoteRequestBody(symbol, no_of_iterations), "urn:getQuote");
        } finally {
            operationClient.destroy();
        }
        Assert.assertNotNull(response, "Response Message is null");
        return response.toString();

    }

    public OMElement getResponsenew() throws IOException {
        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(proxyServiceUrl, null,
                                            createMultipleQuoteRequestBodynew(symbol, no_of_iterations), "urn:getQuote");
        } finally {
            operationClient.destroy();
        }

        return response;

    }

    public String getResponse(OMElement payload) throws IOException {
        AxisOperationClient operationClient = new AxisOperationClient();
        OMElement response = null;
        try {
            response = operationClient.send(proxyServiceUrl, null,
                                            payload, "urn:getQuote");
        } finally {
            operationClient.destroy();
        }
        Assert.assertNotNull(response, "Response Message is null");
        return response.toString();

    }


    private OMElement createMultipleQuoteRequestBody(String symbol, int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method1 = fac.createOMElement("getQuotes", omNs);
        OMElement method2 = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, symbol));
            value1.addChild(value2);
            method2.addChild(value1);
            method1.addChild(method2);
        }
        return method1;
    }

    private OMElement createMultipleQuoteRequestBodynew(String symbol, int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method2 = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement value1 = fac.createOMElement("request", omNs);
            OMElement value2 = fac.createOMElement("symbol", omNs);
            value2.addChild(fac.createOMText(value1, symbol));
            value1.addChild(value2);
            method2.addChild(value1);
        }
        return method2;
    }
}
