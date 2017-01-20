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

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.Constants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.namespace.QName;
import java.io.File;
import java.net.URL;

public class JSONClient {

    public static void main(String[] args) {
        try {
            executeClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
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

        String addUrl = getProperty("addurl", "http://localhost:8280/services/JSONProxy");
        String trpUrl = getProperty("trpurl", null);
        String prxUrl = getProperty("prxurl", null);
        String repo = getProperty("repository", "client_repo");
        String symbol = getProperty("symbol", "IBM");

        if (repo != null && !"null".equals(repo)) {
            configContext =
                    ConfigurationContextFactory.
                            createConfigurationContextFromFileSystem(repo,
                                    repo + File.separator + "conf" + File.separator + "axis2.xml");
            serviceClient = new ServiceClient(configContext, null);
        } else {
            serviceClient = new ServiceClient();
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

        serviceClient.engageModule("addressing");
        options.setTo(new EndpointReference(addUrl));
        options.setAction("urn:getQuote");
        options.setProperty(Constants.Configuration.MESSAGE_TYPE, "application/json");
        serviceClient.setOptions(options);
        OMElement payload =
                AXIOMUtil.stringToOM("<getQuote><request><symbol>" + symbol + "</symbol>" +
                        "</request></getQuote>");

        OMElement response = serviceClient.sendReceive(payload);
        if (response.getLocalName().equals("getQuoteResponse")) {
            OMElement last = response.getFirstElement().getFirstChildWithName(new QName("last"));
            System.out.println("Standard :: Stock price = $" + last.getText());
        } else {
            throw new Exception("Unexpected response : " + response);
        }
        Thread.sleep(1000);
        if (configContext != null) {
            configContext.terminate();
        }
    }
}
