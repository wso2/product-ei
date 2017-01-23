/*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package samples.userguide;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.Constants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HTTPConstants;

import java.util.Date;
import java.io.File;
import java.net.URL;


public class FIXClient {

    public static void main(String[] args) {
        try {
            executeClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }

    public static void executeClient() throws Exception {
        Options options = new Options();
        ServiceClient serviceClient;
        ConfigurationContext configContext = null;

        String symbol = getProperty("symbol", "IBM");
        String mode = getProperty("mode", "buy");

        String addUrl = getProperty("addurl", null);
        String trpUrl = getProperty("trpurl", null);
        String prxUrl = getProperty("prxurl", null);
        String repo = getProperty("repository", "client_repo");
        String qty = getProperty("qty", "1");


        String side = "1";
        if (mode.equals("sell")) {
            side = "2";
        }

        if (repo != null && !"null".equals(repo)) {
            configContext =
                    ConfigurationContextFactory.
                            createConfigurationContextFromFileSystem(repo,
                                    repo + File.separator + "conf" + File.separator + "axis2.xml");
            serviceClient = new ServiceClient(configContext, null);
        } else {
            serviceClient = new ServiceClient();
        }

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement message = factory.createOMElement("message", null);
        message.addChild(getHeader(factory));
        message.addChild(getBody(factory, symbol, side, qty));
        message.addChild(factory.createOMElement("trailer", null));

        // set addressing, transport and proxy url
        if (addUrl != null && !"null".equals(addUrl)) {
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
        }
        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        }
        if (prxUrl != null && !"null".equals(prxUrl)) {
            HttpTransportProperties.ProxyProperties proxyProperties =
                    new HttpTransportProperties.ProxyProperties();
            URL url = new URL(prxUrl);
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
        System.out.println("Response Received: " + response.toString());

        try {
            if (configContext != null) {
                configContext.terminate();
            }
        } catch (Exception ignore) { }

    }

    private static OMElement getHeader(OMFactory factory) {
        OMElement header = factory.createOMElement("header", null);

        OMElement msgType = factory.createOMElement("field", null);
        msgType.addAttribute(factory.createOMAttribute("id", null, "35"));
        factory.createOMText(msgType, "D");
        header.addChild(msgType);

        OMElement sendingTime  = factory.createOMElement("field", null);
        sendingTime.addAttribute(factory.createOMAttribute("id", null, "52"));
        factory.createOMText(sendingTime, new Date().toString());
        header.addChild(sendingTime);

        return header;
    }

    private static OMElement getBody(OMFactory factory, String text, String mode, String qtyValue) {
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

}
