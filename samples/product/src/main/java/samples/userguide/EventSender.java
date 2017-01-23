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
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;

import java.io.File;


public class EventSender {

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

        String addUrl = getProperty("addurl", "http://localhost:8280/services/EventingProxy");
        String repo = getProperty("repository", "client_repo");
        String symbol = getProperty("symbol", "GOOG");
        String price = getProperty("price", "10.10");
        String qty = getProperty("qty", "1000");
        String topic = getProperty("topic", "synapse/event/test");
        String action = getProperty("action", "urn:event");
        String topicns = getProperty("topicns", "http://apache.org/aip");

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

        OMNamespace nsaip = factory.createOMNamespace(topicns, "aip");

        // set the target topic
        OMElement topicOm = factory.createOMElement("Topic", nsaip);
        factory.createOMText(topicOm, topic);

        // set addressing, transport and proxy url

        serviceClient.engageModule("addressing");
        options.setTo(new EndpointReference(addUrl));
        options.setAction(action);
        options.setProperty(MessageContext.CLIENT_API_NON_BLOCKING,
                Boolean.FALSE); // set for fire and foget
        serviceClient.setOptions(options);
        serviceClient.addHeader(topicOm);
        OMElement payload =
                AXIOMUtil.stringToOM("<m:placeOrder xmlns:m=\"http://services.samples\">\n" +
                        "    <m:order>\n" +
                        "        <m:price>" + price + "</m:price>\n" +
                        "        <m:quantity>" + qty + "</m:quantity>\n" +
                        "        <m:symbol>" + symbol + "</m:symbol>\n" +
                        "    </m:order>\n" +
                        "</m:placeOrder>");

        System.out.println("Sending Event : \n" + payload.toString());
        serviceClient.fireAndForget(payload);
        System.out.println("Event sent to topic " + topic);
        Thread.sleep(1000);
        if (configContext != null) {
            configContext.terminate();
        }
    }
}