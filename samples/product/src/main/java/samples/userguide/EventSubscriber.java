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
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;

import javax.xml.namespace.QName;
import java.io.File;


public class EventSubscriber {

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

        String addUrl = getProperty("addurl", "http://localhost:8280/services/SampleEventSource");
        String trpUrl = getProperty("trpurl", null);
        String prxUrl = getProperty("prxurl", null);
        String repo = getProperty("repository", "client_repo");
        String topic = getProperty("topic", "synapse/event/test");
        String address =
                getProperty("address", "http://localhost:9000/services/SimpleStockQuoteService");
        String mode = getProperty("mode", "subscribe");
        String identifier = getProperty("identifier", "90000");
        String expires = getProperty("expires", "*"); //Format: 2020-12-31T21:07:00.000-08:00

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

        OMNamespace nsxmlins =
                factory.createOMNamespace("http://www.w3.org/2001/XMLSchema", "xmlns");
        OMNamespace nss11 =
                factory.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope", "s11");
        OMNamespace nswsa = factory.createOMNamespace(
                "http://schemas.xmlsoap.org/ws/2004/08/addressing", "wsa");
        OMNamespace nswse =
                factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2004/08/eventing", "wse");

        if (mode.equals("subscribe")) {
            OMElement subscribeOm = factory.createOMElement("Subscribe", nswse);
            OMElement deliveryOm = factory.createOMElement("Delivery", nswse);
            deliveryOm.addAttribute(factory.createOMAttribute("Mode", null,
                    "http://schemas.xmlsoap.org/ws/2004/08/eventing/DeliveryModes/Push"));
            OMElement notifyToOm = factory.createOMElement("NotifyTo", nswse);
            OMElement addressOm = factory.createOMElement("Address", nswsa);
            factory.createOMText(addressOm, address);
            OMElement expiresOm = factory.createOMElement("Expires", nswse);
            factory.createOMText(expiresOm, expires);
            OMElement filterOm = factory.createOMElement("Filter", nswse);
            filterOm.addAttribute(factory.createOMAttribute("Dialect", null,
                    "http://synapse.apache.org/eventing/dialect/topicFilter"));
            factory.createOMText(filterOm, topic);


            notifyToOm.addChild(addressOm);
            deliveryOm.addChild(notifyToOm);
            subscribeOm.addChild(deliveryOm);
            if (!(expires.equals("*"))) {
                subscribeOm.addChild(expiresOm); // Add only if the value provided 
            }
            subscribeOm.addChild(filterOm);

            // set addressing, transport and proxy url

            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));

            options.setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/Subscribe");
            serviceClient.setOptions(options);
            System.out.println("Subscribing \n" + subscribeOm.toString());
            try {
                OMElement response = serviceClient.sendReceive(subscribeOm);
                System.out.println("Subscribed to topic " + topic);
                Thread.sleep(1000);
                System.out.println("Response Received: " + response.toString());
                String subId =
                        response.getFirstChildWithName(
                                new QName(nswse.getNamespaceURI(), "SubscriptionManager"))
                                .getFirstChildWithName(
                                        new QName(nswsa.getNamespaceURI(), "ReferenceParameters"))
                                .getFirstChildWithName(
                                        new QName(nswse.getNamespaceURI(), "Identifier")).getText();
                System.out.println("Subscription identifier: " + subId);
            } catch (AxisFault e) {
                System.out.println("Fault Received : " + e.toString());
                System.out.println("Fault Code     : " + e.getFaultCode().toString());
            }
        } else if (mode.equals("unsubscribe")) {
            /** Send unsubscribe message
             (01) <s12:Envelope
             (02)     xmlns:s12="http://www.w3.org/2003/05/soap-envelope"
             (03)     xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
             (04)     xmlns:wse="http://schemas.xmlsoap.org/ws/2004/08/eventing"
             (05)     xmlns:ow="http://www.example.org/oceanwatch" >
             (06)   <s12:Header>
             (07)     <wsa:Action>
             (08)       http://schemas.xmlsoap.org/ws/2004/08/eventing/Unsubscribe
             (09)     </wsa:Action>
             (10)     <wsa:MessageID>
             (11)       uuid:2653f89f-25bc-4c2a-a7c4-620504f6b216
             (12)     </wsa:MessageID>
             (13)     <wsa:ReplyTo>
             (14)      <wsa:Address>http://www.example.com/MyEventSink</wsa:Address>
             (15)     </wsa:ReplyTo>
             (16)     <wsa:To>
             (17)       http://www.example.org/oceanwatch/SubscriptionManager
             (18)     </wsa:To>
             (19)     <wse:Identifier>
             (20)       uuid:22e8a584-0d18-4228-b2a8-3716fa2097fa
             (21)     </wse:Identifier>
             (22)   </s12:Header>
             (23)   <s12:Body>
             (24)     <wse:Unsubscribe />
             (25)   </s12:Body>
             (26) </s12:Envelope>*/
            OMElement subscribeOm = factory.createOMElement("Unsubscribe", nswse);
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
            options.setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/Unsubscribe");
            OMElement identifierOm = factory.createOMElement("Identifier", nswse);
            factory.createOMText(identifierOm, identifier);
            serviceClient.addHeader(identifierOm);
            serviceClient.setOptions(options);
            System.out.println("UnSubscribing \n" + subscribeOm.toString());
            try {
                OMElement response = serviceClient.sendReceive(subscribeOm);
                System.out.println("UnSubscribed to ID " + identifier);
                Thread.sleep(1000);
                System.out.println("UnSubscribe Response Received: " + response.toString());
            } catch (AxisFault e) {
                System.out.println("Fault Received : " + e.toString());
                System.out.println("Fault Code     : " + e.getFaultCode().toString());
            }

        } else if (mode.equals("renew")) {
            /**
             * (01) <s12:Envelope
             (02)     xmlns:s12="http://www.w3.org/2003/05/soap-envelope"
             (03)     xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
             (04)     xmlns:wse="http://schemas.xmlsoap.org/ws/2004/08/eventing"
             (05)     xmlns:ow="http://www.example.org/oceanwatch" >
             (06)   <s12:Header>
             (07)     <wsa:Action>
             (08)       http://schemas.xmlsoap.org/ws/2004/08/eventing/Renew
             (09)     </wsa:Action>
             (10)     <wsa:MessageID>
             (11)       uuid:bd88b3df-5db4-4392-9621-aee9160721f6
             (12)     </wsa:MessageID>
             (13)     <wsa:ReplyTo>
             (14)      <wsa:Address>http://www.example.com/MyEventSink</wsa:Address>
             (15)     </wsa:ReplyTo>
             (16)     <wsa:To>
             (17)       http://www.example.org/oceanwatch/SubscriptionManager
             (18)     </wsa:To>
             (19)     <wse:Identifier>
             (20)       uuid:22e8a584-0d18-4228-b2a8-3716fa2097fa
             (21)     </wse:Identifier>
             (22)   </s12:Header>
             (23)   <s12:Body>
             (24)     <wse:Renew>
             (25)       <wse:Expires>2004-06-26T21:07:00.000-08:00</wse:Expires>
             (26)     </wse:Renew>
             (27)   </s12:Body>
             (28) </s12:Envelope>
             */
            OMElement subscribeOm = factory.createOMElement("Renew", nswse);
            OMElement expiresOm = factory.createOMElement("Expires", nswse);
            factory.createOMText(expiresOm, expires);
            subscribeOm.addChild(expiresOm);
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
            options.setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/Renew");
            OMElement identifierOm = factory.createOMElement("Identifier", nswse);
            factory.createOMText(identifierOm, identifier);
            serviceClient.addHeader(identifierOm);
            serviceClient.setOptions(options);
            System.out.println("SynapseSubscription Renew \n" + subscribeOm.toString());
            try {
                OMElement response = serviceClient.sendReceive(subscribeOm);
                System.out.println("SynapseSubscription Renew to ID " + identifier);
                Thread.sleep(1000);
                System.out.println(
                        "SynapseSubscription Renew Response Received: " + response.toString());
            } catch (AxisFault e) {
                System.out.println("Fault Received : " + e.toString());
                System.out.println("Fault Code     : " + e.getFaultCode().toString());
            }

        } else if (mode.equals("getstatus")) {
            /**
             * (01) <s12:Envelope
             (02)     xmlns:s12="http://www.w3.org/2003/05/soap-envelope"
             (03)     xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
             (04)     xmlns:wse="http://schemas.xmlsoap.org/ws/2004/08/eventing"
             (05)     xmlns:ow="http://www.example.org/oceanwatch" >
             (06)   <s12:Header>
             (07)     <wsa:Action>
             (08)       http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatus
             (09)     </wsa:Action>
             (10)     <wsa:MessageID>
             (11)       uuid:bd88b3df-5db4-4392-9621-aee9160721f6
             (12)     </wsa:MessageID>
             (13)     <wsa:ReplyTo>
             (14)       <wsa:Address>http://www.example.com/MyEventSink</wsa:Address>
             (15)     </wsa:ReplyTo>
             (16)     <wsa:To>
             (17)       http://www.example.org/oceanwatch/SubscriptionManager
             (18)     </wsa:To>
             (19)     <wse:Identifier>
             (20)       uuid:22e8a584-0d18-4228-b2a8-3716fa2097fa
             (21)     </wse:Identifier>
             (22)   </s12:Header>
             (23)   <s12:Body>
             (24)     <wse:GetStatus />
             (25)   </s12:Body>
             (26) </s12:Envelope>
             */
            OMElement subscribeOm = factory.createOMElement("GetStatus", nswse);
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
            options.setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatus");
            OMElement identifierOm = factory.createOMElement("Identifier", nswse);
            factory.createOMText(identifierOm, identifier);
            serviceClient.addHeader(identifierOm);
            serviceClient.setOptions(options);
            System.out.println("GetStatus using \n" + subscribeOm.toString());
            try {
                OMElement response = serviceClient.sendReceive(subscribeOm);
                System.out.println("GetStatus to ID " + identifier);
                Thread.sleep(1000);
                System.out.println("GetStatus Response Received: " + response.toString());
            } catch (AxisFault e) {
                System.out.println("Fault Received : " + e.toString());
                System.out.println("Fault Code     : " + e.getFaultCode().toString());
            }
        }

        try {
            if (configContext != null) {
                configContext.terminate();
            }
        } catch (Exception ignore) {
        }
    }
}