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

package org.wso2.esb.integration.common.utils.clients.stockquoteclient;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.utils.clients.axis2client.ConfigurationContextProvider;

import java.util.ArrayList;
import java.util.List;

public class StockQuoteClient {

    private static final Log log = LogFactory.getLog(StockQuoteClient.class);
    private final int MAX_TIME = 60000;

    private List<Header> httpHeaders = new ArrayList<Header>();

    public StockQuoteClient() {

    }

/*    public void setHeader(String localName, String ns, String value) throws AxisFault {
//        serviceClient.addStringHeader(new QName(ns, localName), value);
    }*/

    public void addHttpHeader(String name, String value) {
        httpHeaders.add(new Header(name, value));
    }

    public void clearHttpHeader() {
        httpHeaders.clear();
    }

    public OMElement sendSimpleStockQuoteRequest(String trpUrl, String addUrl, String symbol)
            throws AxisFault {

        ServiceClient sc;
        sc=getServiceClient(trpUrl, addUrl);

        try {
            return buildResponse(sc.sendReceive(createStandardRequest(symbol)));
        } finally {
            sc.cleanupTransport();
        }
    }

    public OMElement sendSimpleStockQuoteRequestREST(String trpUrl, String addUrl, String symbol)
            throws AxisFault {
        ServiceClient sc = getRESTEnabledServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(sc.sendReceive(createStandardRequest(symbol)));
        } finally {
            sc.cleanupTransport();
        }

    }

    public OMElement sendSimpleQuoteRequest(String trpUrl, String addUrl, String symbol)
            throws AxisFault {

        ServiceClient sc = getServiceClient(trpUrl, addUrl, "getSimpleQuote");
        try {
            return buildResponse(sc.sendReceive(createStandardSimpleRequest(symbol)));
        } finally {
            sc.cleanupTransport();
        }
    }

    public OMElement sendSimpleQuoteRequestREST(String trpUrl, String addUrl, String symbol)
            throws AxisFault {

        ServiceClient serviceClient = getRESTEnabledServiceClient(trpUrl, addUrl, "getSimpleQuote");
        try {
            return buildResponse(serviceClient.sendReceive(createStandardSimpleRequest(symbol)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement sendSimpleStockQuoteSoap11(String trpUrl, String addUrl, String symbol)
            throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        serviceClient.getOptions()
                .setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        try {
            return buildResponse(serviceClient.sendReceive(createStandardRequest(symbol)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }


    public OMElement sendSimpleStockQuoteSoap12(String trpUrl, String addUrl, String symbol)
            throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        serviceClient.getOptions()
                .setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        try {
            return buildResponse(serviceClient.sendReceive(createStandardRequest(symbol)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement sendSimpleStockQuoteRequest(String trpUrl, String addUrl, OMElement payload)
            throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(serviceClient.sendReceive(payload));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement sendSimpleStockQuoteRequestREST(String trpUrl, String addUrl,
                                                      OMElement payload)
            throws AxisFault {

        ServiceClient serviceClient = getRESTEnabledServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(serviceClient.sendReceive(payload));
        } finally {
            serviceClient.cleanupTransport();
        }
    }


    public OMElement sendCustomQuoteRequest(String trpUrl, String addUrl, String symbol)
            throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(serviceClient.sendReceive(createCustomQuoteRequest(symbol)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement send(String trpUrl, String addUrl, String action, OMElement payload)
            throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl, action);
        try {
            return buildResponse(serviceClient.sendReceive(payload));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    /**
     * This method utilises serviceClient's sendRobust() method
     *
     * @param trpUrl transport url
     * @param addUrl address url
     * @param action action
     * @param payload payload
     * @throws AxisFault if error occurs in sending the request
     */
    public void sendRobust(String trpUrl, String addUrl, String action, OMElement payload) throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl, action);
        try {
            serviceClient.sendRobust(payload);
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement sendCustomQuoteRequestREST(String trpUrl, String addUrl, String symbol)
            throws AxisFault {

        ServiceClient serviceClient = getRESTEnabledServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(serviceClient.sendReceive(createCustomQuoteRequest(symbol)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement sendMultipleCustomQuoteRequest(String trpUrl, String addUrl, String symbol,
                                                    int n) throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(
                    serviceClient.sendReceive(createMultipleCustomQuoteRequest(symbol, n)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement sendMultipleQuoteRequest(String trpUrl, String addUrl, String symbol, int n)
            throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(serviceClient.sendReceive(createMultipleQuoteRequest(symbol, n)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    public OMElement sendMultipleQuoteRequestREST(String trpUrl, String addUrl, String symbol,
                                                   int n)
            throws AxisFault {

        ServiceClient serviceClient = getRESTEnabledServiceClient(trpUrl, addUrl);
        try {
            return buildResponse(serviceClient.sendReceive(createMultipleQuoteRequest(symbol, n)));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    private ServiceClient getServiceClient(String trpUrl, String addUrl) throws AxisFault {

        return getServiceClient(trpUrl, addUrl, "getQuote");
    }

    private ServiceClient getRESTEnabledServiceClient(String trpUrl, String addUrl)
            throws AxisFault {

        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        serviceClient.getOptions().setProperty("enableREST", "true");
        return serviceClient;
    }

    private ServiceClient getServiceClient(String trpUrl, String addUrl, String operation)
            throws AxisFault {

        ServiceClient serviceClient;
        Options options = new Options();

        if (addUrl != null && !"null".equals(addUrl)) {
            serviceClient = new ServiceClient(ConfigurationContextProvider.getInstance().getConfigurationContext(), null);
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
        } else {
            //otherwise it will engage addressing all the time once addressing is engaged by ConfigurationContext to service client
            serviceClient = new ServiceClient();
        }

        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        }

        options.setAction("urn:" + operation);
        if (httpHeaders.size() > 0) {
            options.setProperty(HTTPConstants.HTTP_HEADERS, httpHeaders);
        }
        options.setTimeOutInMilliSeconds(MAX_TIME);
      /*  options.setProperty(HTTPConstants.CHUNKED, Constants.VALUE_FALSE);
        options.setProperty(Constants.Configuration.MESSAGE_TYPE,HTTPConstants.MEDIA_TYPE_APPLICATION_ECHO_XML);
        options.setProperty(Constants.Configuration.DISABLE_SOAP_ACTION,Boolean.TRUE);*/
        serviceClient.setOptions(options);

        return serviceClient;
    }

    private ServiceClient getRESTEnabledServiceClient(String trpUrl, String addUrl,
                                                      String operation)
            throws AxisFault {
        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl, operation);
        serviceClient.getOptions().setProperty("enableREST", "true");

        return serviceClient;
    }

    public void destroy() {
        //to keep backward compatibility
        ConfigurationContextProvider.getInstance().getConfigurationContext().cleanupContexts();
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

    private OMElement createStandardSimpleRequest(String symbol) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getSimpleQuote", omNs);
        OMElement value1 = fac.createOMElement("symbol", omNs);

        value1.addChild(fac.createOMText(method, symbol));
        method.addChild(value1);

        return method;
    }

    private OMElement createMultipleQuoteRequest(String symbol, int iterations) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
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

    private OMElement createCustomQuoteRequest(String symbol) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "ns");
        OMElement chkPrice = factory.createOMElement("CheckPriceRequest", ns);
        OMElement code = factory.createOMElement("Code", ns);
        chkPrice.addChild(code);
        code.setText(symbol);
        return chkPrice;
    }

    private OMElement createMultipleCustomQuoteRequest(String symbol, int iterations) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
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

    private static OMElement buildResponse(OMElement omElement) {
        omElement.build();
        return omElement;
    }

    /**
     * Send place order request
     *
     * @param trpUrl transport url
     * @param addUrl address url
     * @param symbol symbol
     * @throws AxisFault if error occurs when sending request
     */
    public void sendPlaceOrderRequest(String trpUrl, String addUrl, String symbol) throws AxisFault {
        double price = getRandom(100, 0.9, true);
        int quantity = (int) getRandom(10000, 1.0, true);
        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl, "placeOrder");
        try {
            serviceClient.fireAndForget(createPlaceOrderRequest(price, quantity, symbol));
        } finally {
            serviceClient.cleanupTransport();
        }
    }

    private static double getRandom(double base, double varience, boolean onlypositive) {
        double rand = Math.random();
        return (base + ((rand > 0.5 ? 1 : -1) * varience * base * rand))
               * (onlypositive ? 1 : (rand > 0.5 ? 1 : -1));
    }

    /**
     * Create place order request
     *
     * @param purchasePrice purchase price
     * @param qty           quantity
     * @param symbol        symbol
     * @return OMElement of request
     */
    public OMElement createPlaceOrderRequest(double purchasePrice, int qty, String symbol) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement placeOrder = factory.createOMElement("placeOrder", ns);
        OMElement order = factory.createOMElement("order", ns);
        OMElement price = factory.createOMElement("price", ns);
        OMElement quantity = factory.createOMElement("quantity", ns);
        OMElement symb = factory.createOMElement("symbol", ns);
        price.setText(Double.toString(purchasePrice));
        quantity.setText(Integer.toString(qty));
        symb.setText(symbol);
        order.addChild(price);
        order.addChild(quantity);
        order.addChild(symb);
        placeOrder.addChild(order);
        return placeOrder;
    }

    /**
     * Send dual quote request
     *
     * @param trpUrl transport url
     * @param addUrl address url
     * @param symbol symbol
     * @throws AxisFault if error occurs when sending request
     */
    public void sendDualQuoteRequest(String trpUrl, String addUrl, String symbol) throws AxisFault {
        ServiceClient serviceClient = getServiceClient(trpUrl, addUrl);
        serviceClient.getOptions().setUseSeparateListener(true);

        try {
            serviceClient.sendReceiveNonBlocking(createStandardRequest(symbol), new AxisCallback() {
                @Override
                public void onMessage(MessageContext messageContext) {
                    log.info("Response received to the callback");
                }

                @Override
                public void onFault(MessageContext messageContext) {
                    log.info("Fault received to the callback : " + messageContext.getEnvelope().getBody().getFault());
                }

                @Override
                public void onError(Exception e) {
                    log.error("Error inside callback", e);
                }

                @Override
                public void onComplete() {
                    log.info("OnComplete called....");
                }
            });
        } finally {
            serviceClient.cleanupTransport();
        }
    }
}
