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

package org.wso2.esb.integration.common.utils.clients;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Load balance failover client with session affinity
 */
public class LoadBalanceSessionFullClient {

    protected static final Log log = LogFactory.getLog(LoadBalanceSessionFullClient.class);

    protected ServiceClient serviceClient;

    private final static String DEFAULT_CLIENT_REPO = "client_repo";
    private final static String COOKIE = "Cookie";
    private final static String SET_COOKIE = "Set-Cookie";

    private SOAPEnvelope[] envelopes = null;
    private long sleepTime = -1;

    public LoadBalanceSessionFullClient() throws IOException {
        init();
        buildSoapEnvelopesWithClientSession();
    }

    private void init() throws IOException {
        String repositoryPath =
            System.getProperty(ESBTestConstant.CARBON_HOME) + File.separator + "samples" + File.separator +
            "axis2Client" + File.separator + DEFAULT_CLIENT_REPO;

        File repository = new File(repositoryPath);
        if (log.isDebugEnabled()) {
            log.debug("Axis2 repository path: " + repository.getAbsolutePath());
        }

        ConfigurationContext configurationContext =
            ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                repository.getCanonicalPath(), null);
        serviceClient = new ServiceClient(configurationContext, null);
        log.info("LoadBalanceSessionFullClient initialized successfully...");
    }

    private void buildSoapEnvelopesWithClientSession() {
        Date date = new Date();
        SOAPEnvelope env1 = buildSoapEnvelope("" + (date.getTime() + 10), "v1");
        SOAPEnvelope env2 = buildSoapEnvelope("" + (date.getTime() + 20), "v1");
        SOAPEnvelope env3 = buildSoapEnvelope("" + (date.getTime() + 30), "v1");

        envelopes = new SOAPEnvelope[] { env1, env2, env3 };
    }

    /**
     * Send load balance request
     *
     * @param trpUrl     transport URL
     * @param addUrl     address URL
     * @param prxUrl     proxy URL
     * @param iterations number of requests
     * @return list of ResponseData
     * @throws org.apache.axis2.AxisFault if error occurs when sending request
     */
    public List<ResponseData> sendLoadBalanceRequest(String trpUrl, String addUrl, String prxUrl, int iterations)
        throws AxisFault {
        updateServiceClientOptions(trpUrl, addUrl, prxUrl);
        return makeRequest(null, iterations, sleepTime, envelopes, serviceClient);
    }

    /**
     * Send load balance request
     *
     * @param trpUrl     transport URL
     * @param addUrl     address URL
     * @param prxUrl     proxy URL
     * @param session    session
     * @param iterations number of requests
     * @return list of ResponseData
     * @throws org.apache.axis2.AxisFault if error occurs when sending request
     */
    public List<ResponseData> sendLoadBalanceRequest(String trpUrl, String addUrl, String prxUrl, String session,
                                                     int iterations) throws AxisFault {
        updateServiceClientOptions(trpUrl, addUrl, prxUrl);
        return makeRequest(session, iterations, sleepTime, envelopes, serviceClient);
    }

    /**
     * Send load balance request
     *
     * @param trpUrl     transport URL
     * @param addUrl     address URL
     * @param prxUrl     proxy URL
     * @param session    session
     * @param iterations number of requests
     * @param sleepTime  sleep time
     * @return list of ResponseData
     * @throws org.apache.axis2.AxisFault if error occurs when sending request
     */
    public List<ResponseData> sendLoadBalanceRequest(String trpUrl, String addUrl, String prxUrl, String session,
                                                     int iterations, long sleepTime) throws AxisFault {
        updateServiceClientOptions(trpUrl, addUrl, prxUrl);
        return makeRequest(session, iterations, sleepTime, envelopes, serviceClient);
    }

    private void updateServiceClientOptions(String trpUrl, String addUrl, String prxUrl) throws AxisFault {
        Options options = new Options();
        options.setTo(new EndpointReference(trpUrl));
        options.setAction("urn:sampleOperation");
        options.setTimeOutInMilliSeconds(10000000);

        // set addressing, transport and proxy url
        if (addUrl != null && !"null".equals(addUrl)) {
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
        }
        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        } else {
            serviceClient.engageModule("addressing");
        }
        serviceClient.engageModule("addressing");
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
                String msg = "Error while creating proxy URL";
                log.error(msg, e);
                throw new AxisFault(msg, e);
            }
        }
        serviceClient.setOptions(options);
    }

    private List<ResponseData> makeRequest(String session, int iterations, long sleepTime, SOAPEnvelope[] envelopes,
                                           ServiceClient client) throws AxisFault {
        List<ResponseData> responseList = new ArrayList<ResponseData>();

        int i = 0;
        int sessionNumber;
        String[] cookies = new String[3];
        boolean httpSession = session != null && "http".equals(session);
        int cookieNumber;

        while (i < iterations) {

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
                if (log.isDebugEnabled()) {
                    log.debug(
                        "Request: " + i + " with Session ID: " + (httpSession ? cookie : sessionNumber) + " ---- " +
                        "Response : with  " + (httpSession && receivedCookie != null ?
                                               (receivedSetCookie != null ? receivedSetCookie : receivedCookie) :
                                               " ") + " " + vElement.getText());
                }

                responseList
                    .add(new ResponseData(true, "" + (httpSession ? cookie : sessionNumber), vElement.getText()));

            } catch (AxisFault axisFault) {
                if (log.isDebugEnabled()) {
                    log.debug("Request with session id " + (httpSession ? cookie : sessionNumber), axisFault);
                }

                responseList.add(
                    new ResponseData(false, "" + (httpSession ? cookie : sessionNumber), axisFault.getMessage()));
            }
        }

        return responseList;
    }

    private int getSessionTurn(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    protected String extractSessionID(MessageContext axis2MessageContext) {

        Object o = axis2MessageContext.getProperty(MessageContext.TRANSPORT_HEADERS);

        if (o instanceof Map) {
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

        if (o instanceof Map) {
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

        OMNamespace synNamespace = soapFactory.createOMNamespace(
            "http://ws.apache.org/ns/synapse", "syn");
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
}
