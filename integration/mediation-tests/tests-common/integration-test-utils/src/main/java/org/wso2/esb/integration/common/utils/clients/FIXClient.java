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

package org.wso2.esb.integration.common.utils.clients;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;


public class FIXClient {

    private String addUrl = null;
    private String trpUrl = null;
    private String prxyUrl = null;
    private String symbol;
    private String mode;
    private String qty;
    private String repo;

    private Options options;
    private ServiceClient serviceClient;
    private ConfigurationContext configContext = null;
    private String pathToRepo;

    public FIXClient() throws IOException {

        options = new Options();

        repo = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "clients";

        pathToRepo = (new File(repo)).getCanonicalPath();
    }

    //send the request and get the response as a string
    public String send(String symbol, String mode, String qty, String addUrl, String trpUrl,
                       String prxUrl) throws Exception {

        setSymbol(symbol);
        setMode(mode);
        setQty(qty);
        setAddUrl(addUrl);
        setTrpUrl(trpUrl);
        setPrxyUrl(prxUrl);

        String side = "1";
        if (getMode().equals("sell")) {
            side = "2";
        }

        if (pathToRepo != null && !"null".equals(pathToRepo)) {
            configContext =
                    ConfigurationContextFactory.
                            createConfigurationContextFromFileSystem(pathToRepo, null);
            serviceClient = new ServiceClient(configContext, null);
        } else {
            serviceClient = new ServiceClient();
        }

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement message = factory.createOMElement("message", null);
        message.addChild(getHeader(factory));
        message.addChild(getBody(factory, getSymbol(), side, getQty()));
        message.addChild(factory.createOMElement("trailer", null));

        // set addressing, transport and proxy url
        if (getAddUrl() != null && !"null".equals(getAddUrl())) {
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(getAddUrl()));
        }
        if (getTrpUrl() != null && !"null".equals(getTrpUrl())) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, getTrpUrl());
        }
        if (getPrxyUrl() != null && !"null".equals(getPrxyUrl())) {
            HttpTransportProperties.ProxyProperties proxyProperties =
                    new HttpTransportProperties.ProxyProperties();
            URL url = new URL(getPrxyUrl());
            proxyProperties.setProxyName(url.getHost());
            proxyProperties.setProxyPort(url.getPort());
            proxyProperties.setUserName("");
            proxyProperties.setPassWord("");
            proxyProperties.setDomain("");
            options.setProperty(HTTPConstants.PROXY, proxyProperties);
        }

        options.setAction("urn:mediate");
        serviceClient.setOptions(options);
        OMElement response = serviceClient.sendReceive(message);
        Thread.sleep(5000);

        try {
            if (configContext != null) {
                configContext.terminate();
            }
        } catch (Exception ignore) {
        }


        return response.toString();
    }

    private OMElement getHeader(OMFactory factory) {
        OMElement header = factory.createOMElement("header", null);

        OMElement msgType = factory.createOMElement("field", null);
        msgType.addAttribute(factory.createOMAttribute("id", null, "35"));
        factory.createOMText(msgType, "D");
        header.addChild(msgType);

        OMElement sendingTime = factory.createOMElement("field", null);
        sendingTime.addAttribute(factory.createOMAttribute("id", null, "52"));
        factory.createOMText(sendingTime, new Date().toString());
        header.addChild(sendingTime);

        return header;
    }

    private OMElement getBody(OMFactory factory, String text, String mode, String qtyValue) {
        OMElement body = factory.createOMElement("body", null);

        OMElement clordID = factory.createOMElement("field", null);
        clordID.addAttribute(factory.createOMAttribute("id", null, "11"));
        factory.createOMText(clordID, "122333");
        body.addChild(clordID);

        OMElement handleIns = factory.createOMElement("field", null);
        handleIns.addAttribute(factory.createOMAttribute("id", null, "21"));
        factory.createOMText(handleIns, "1");
        body.addChild(handleIns);

        OMElement qty = factory.createOMElement("field", null);
        qty.addAttribute(factory.createOMAttribute("id", null, "38"));
        factory.createOMText(qty, qtyValue);
        body.addChild(qty);

        OMElement ordType = factory.createOMElement("field", null);
        ordType.addAttribute(factory.createOMAttribute("id", null, "40"));
        factory.createOMText(ordType, "1");
        body.addChild(ordType);

        OMElement side = factory.createOMElement("field", null);
        side.addAttribute(factory.createOMAttribute("id", null, "54"));
        factory.createOMText(side, mode);
        body.addChild(side);

        OMElement symbol = factory.createOMElement("field", null);
        symbol.addAttribute(factory.createOMAttribute("id", null, "55"));
        factory.createOMText(symbol, text);
        body.addChild(symbol);

        OMElement timeInForce = factory.createOMElement("field", null);
        timeInForce.addAttribute(factory.createOMAttribute("id", null, "59"));
        factory.createOMText(timeInForce, "0");
        body.addChild(timeInForce);

        return body;
    }


    public String getAddUrl() {
        return addUrl;
    }

    public void setAddUrl(String addUrl) {
        this.addUrl = addUrl;
    }

    public String getTrpUrl() {
        return trpUrl;
    }

    public void setTrpUrl(String trpUrl) {
        this.trpUrl = trpUrl;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrxyUrl() {
        return prxyUrl;
    }

    public void setPrxyUrl(String prxyUrl) {
        this.prxyUrl = prxyUrl;
    }
}

