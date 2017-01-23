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

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;

import javax.xml.namespace.QName;
import java.util.*;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

public class LoadbalanceFailoverClient {

    private final static String COOKIE = "Cookie";
    private final static String SET_COOKIE = "Set-Cookie";

    private final static String DEFAULT_CLIENT_REPO = "client_repo";

    /**
     * @param args 0: simple | session
     *             1: port
     *             2: iteration
     */
    public static void main(String[] args) {

        String mode = System.getProperty("mode");
        try {
            if (mode != null) {
                if (mode.equalsIgnoreCase("session")) {
                    new LoadbalanceFailoverClient().sessionfullClient();
                } else if (mode.equalsIgnoreCase("simple") || mode.equalsIgnoreCase("")) {
                    new LoadbalanceFailoverClient().sessionlessClient();
                }
            } else {
                // default is simple client
                new LoadbalanceFailoverClient().sessionlessClient();
            }
        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
        }
    }

    public String sessionlessClient() throws AxisFault {

        String synapsePort = "8280";
        int iterations = 100;
        boolean infinite = true;

        String pPort = getProperty("port", synapsePort);
        String pIterations = getProperty("i", null);
        String addUrl = getProperty("addurl", null);
        String trpUrl = getProperty("trpurl", null);
        String prxUrl = getProperty("prxurl", null);

        String sleep = getProperty("sleep", null);


        long sleepTime = -1;
        if (sleep != null) {
            try {
                sleepTime = Long.parseLong(sleep);
            } catch (NumberFormatException ignored) {
            }
        }

        if (pPort != null) {
            try {
                Integer.parseInt(pPort);
                synapsePort = pPort;
            } catch (NumberFormatException e) {
                // run with default value
            }
        }

        if (pIterations != null) {
            try {
                iterations = Integer.parseInt(pIterations);
                if (iterations != -1) {
                    infinite = false;
                }
            } catch (NumberFormatException e) {
                // run with default values
            }
        }

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement value = fac.createOMElement("Value", null);
        value.setText("Sample string");

        Options options = new Options();
        options.setTo(new EndpointReference("http://localhost:" + synapsePort + "/services/LBService1"));

        options.setAction("urn:sampleOperation");

        String repoLocationProperty = System.getProperty("repository");
        String repo =  repoLocationProperty != null ? repoLocationProperty : DEFAULT_CLIENT_REPO;
        ConfigurationContext configContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                        repo, repo + File.separator + "conf" + File.separator + "axis2.xml");

        ServiceClient client = new ServiceClient(configContext, null);
        long timeout = Integer.parseInt(getProperty("timeout", "10000000"));
        System.out.println("timeout=" + timeout);
        options.setTimeOutInMilliSeconds(timeout);

        // set addressing, transport and proxy url
        if (addUrl != null && !"null".equals(addUrl)) {
            client.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
        }
        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        } else {
            client.engageModule("addressing");
        }
        if (prxUrl != null && !"null".equals(prxUrl)) {
            HttpTransportProperties.ProxyProperties proxyProperties =
                    new HttpTransportProperties.ProxyProperties();
            try {
                URL url = new URL(prxUrl);
                proxyProperties.setProxyName(url.getHost());
                proxyProperties.setProxyPort(url.getPort());
                proxyProperties.setUserName("");
                proxyProperties.setPassWord("");
                proxyProperties.setDomain("");
                options.setProperty(HTTPConstants.PROXY, proxyProperties);
            } catch (MalformedURLException e) {
                throw new AxisFault("Error creating proxy URL", e);
            }
        }

        client.setOptions(options);
        String testString = "";

        long i = 0;
        while (i < iterations || infinite) {

            if (sleepTime != -1) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {
                }
            }

            client.getOptions().setManageSession(true);
            OMElement responseElement = client.sendReceive(value);
            String response = responseElement.getText();

            i++;
            System.out.println("Request: " + i + " ==> " + response);
            testString = testString.concat(":" + i + ">" + response + ":");
        }

        return testString;
    }

    /**
     * This method creates 3 soap envelopes for 3 different client based sessions. Then it randomly
     * choose one envelope for each iteration and send it to the ESB. ESB should be configured with
     * session affinity load balancer and the SampleClientInitiatedSession dispatcher. This will
     * output request number, session number and the server ID for each iteration. So it can be
     * observed that one session number always associated with one server ID.
     */
    private void sessionfullClient() {

        String synapsePort = "8280";
        int iterations = 100;
        boolean infinite = true;

        String pPort = getProperty("port", synapsePort);
        String pIterations = getProperty("i", null);
        String addUrl = getProperty("addurl", null);
        String trpUrl = getProperty("trpurl", null);
        String prxUrl = getProperty("prxurl", null);
        String sleep = getProperty("sleep", null);
        String session = getProperty("session", null);

        long sleepTime = -1;
        if (sleep != null) {
            try {
                sleepTime = Long.parseLong(sleep);
            } catch (NumberFormatException ignored) {
            }
        }

        if (pPort != null) {
            try {

                Integer.parseInt(pPort);
                synapsePort = pPort;
            } catch (NumberFormatException e) {
                // run with default value
            }
        }

        if (pIterations != null) {
            try {
                iterations = Integer.parseInt(pIterations);
                if (iterations != -1) {
                    infinite = false;
                }
            } catch (NumberFormatException e) {
                // run with default values
            }
        }

        Options options = new Options();
        options.setTo(new EndpointReference("http://localhost:" + synapsePort + "/services/LBService1"));
        options.setAction("urn:sampleOperation");
        options.setTimeOutInMilliSeconds(10000000);


        try {

            SOAPEnvelope env1 = buildSoapEnvelope("c1", "v1");
            SOAPEnvelope env2 = buildSoapEnvelope("c2", "v1");
            SOAPEnvelope env3 = buildSoapEnvelope("c3", "v1");
            SOAPEnvelope[] envelopes = {env1, env2, env3};

            String repoLocationProperty = System.getProperty("repository");
            String repo =  repoLocationProperty != null ? repoLocationProperty : DEFAULT_CLIENT_REPO;
            ConfigurationContext configContext =
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                            repo, repo + File.separator + "conf" + File.separator + "axis2.xml");
            
            ServiceClient client = new ServiceClient(configContext, null);

            // set addressing, transport and proxy url
            if (addUrl != null && !"null".equals(addUrl)) {
                client.engageModule("addressing");
                options.setTo(new EndpointReference(addUrl));
            }
            if (trpUrl != null && !"null".equals(trpUrl)) {
                options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
            } else {
                client.engageModule("addressing");
            }
            if (prxUrl != null && !"null".equals(prxUrl)) {
                HttpTransportProperties.ProxyProperties proxyProperties =
                        new HttpTransportProperties.ProxyProperties();
                try {
                    URL url = new URL(prxUrl);
                    proxyProperties.setProxyName(url.getHost());
                    proxyProperties.setProxyPort(url.getPort());
                    proxyProperties.setUserName("");
                    proxyProperties.setPassWord("");
                    proxyProperties.setDomain("");
                    options.setProperty(HTTPConstants.PROXY, proxyProperties);
                } catch (MalformedURLException e) {
                    throw new AxisFault("Error creating proxy URL", e);
                }
            }
            client.setOptions(options);

            int i = 0;
            int sessionNumber;
            String[] cookies = new String[3];
            boolean httpSession = session != null && "http".equals(session);
            int cookieNumber;
            while (i < iterations || infinite) {

                i++;
                if (sleepTime != -1) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ignored) {
                    }
                }

                MessageContext messageContext = new MessageContext();
                sessionNumber = getSessionTurn(envelopes.length);


                messageContext.setEnvelope(envelopes[sessionNumber]);
                cookieNumber = getSessionTurn(cookies.length);
                String cookie = cookies[cookieNumber];
                if (httpSession) {
                    setSessionID(messageContext, cookie);
                }
                try {
                    OperationClient op = client.createClient(ServiceClient.ANON_OUT_IN_OP);
                    op.addMessageContext(messageContext);
                    op.execute(true);

                    MessageContext responseContext =
                            op.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                    String receivedCookie = extractSessionID(responseContext);
                    String receivedSetCookie = getSetCookieHeader(responseContext);
                    if (httpSession) {

                        if (receivedSetCookie != null && !"".equals(receivedSetCookie)) {
                            cookies[cookieNumber] = receivedCookie;
                        }
                    }

                    SOAPEnvelope responseEnvelope = responseContext.getEnvelope();

                    OMElement vElement =
                            responseEnvelope.getBody().getFirstChildWithName(new QName("Value"));
                    System.out.println(
                            "Request: " + i + " with Session ID: " +
                                    (httpSession ? cookie : sessionNumber) + " ---- " +
                                    "Response : with  " + (httpSession && receivedCookie != null ?
                                    (receivedSetCookie != null ? receivedSetCookie : receivedCookie) : " ") + " " +
                                    vElement.getText());
                } catch (AxisFault axisFault) {
                    System.out.println("Request with session id " +
                            (httpSession ? cookie : sessionNumber) + " " +
                            "- Get a Fault : " + axisFault.getMessage());
                }
            }

        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
        }
    }

    private int getSessionTurn(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    protected String extractSessionID(MessageContext axis2MessageContext) {

        Object o = axis2MessageContext.getProperty(MessageContext.TRANSPORT_HEADERS);

        if (o != null && o instanceof Map) {
            Map headerMap = (Map) o;
            String cookie = (String) headerMap.get(SET_COOKIE);
            if (cookie == null) {
                cookie = (String) headerMap.get(COOKIE);
            } else {
                cookie = cookie.split(";")[0];
            }
            return cookie;
        }
        return null;
    }

    protected String getSetCookieHeader(MessageContext axis2MessageContext) {

        Object o = axis2MessageContext.getProperty(MessageContext.TRANSPORT_HEADERS);

        if (o != null && o instanceof Map) {
            Map headerMap = (Map) o;
            return (String) headerMap.get(SET_COOKIE);
        }
        return null;
    }

    protected void setSessionID(MessageContext axis2MessageContext, String value) {

        if (value == null) {
            return;
        }
        Map map = (Map) axis2MessageContext.getProperty(HTTPConstants.HTTP_HEADERS);
        if (map == null) {
            map = new HashMap();
            axis2MessageContext.setProperty(HTTPConstants.HTTP_HEADERS, map);
        }
        map.put(COOKIE, value);
    }

    private SOAPEnvelope buildSoapEnvelope(String clientID, String value) {
        SOAPFactory soapFactory = OMAbstractFactory.getSOAP12Factory();

        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();

        SOAPHeader header = soapFactory.createSOAPHeader();
        envelope.addChild(header);

        OMNamespace synNamespace = soapFactory.
                createOMNamespace("http://ws.apache.org/ns/synapse", "syn");
        OMElement clientIDElement = soapFactory.createOMElement("ClientID", synNamespace);
        clientIDElement.setText(clientID);
        header.addChild(clientIDElement);

        SOAPBody body = soapFactory.createSOAPBody();
        envelope.addChild(body);

        OMElement valueElement = soapFactory.createOMElement("Value", null);
        valueElement.setText(value);
        body.addChild(valueElement);

        return envelope;
    }

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }
}
