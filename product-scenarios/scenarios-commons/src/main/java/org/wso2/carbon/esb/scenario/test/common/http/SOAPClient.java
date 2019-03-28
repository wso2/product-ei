/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test.common.http;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import static org.wso2.carbon.esb.scenario.test.common.http.HttpConstants.SOAP_ACTION;

/**
 * Simple client for SOAP operations
 */
public class SOAPClient {

    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 30000;
    private DefaultHttpClient httpclient;

    /**
     * Constructor for SOAPClient
     */
    public SOAPClient() {
        setup();
    }

    /**
     * Constructor for SOAPClient
     *
     * @param connectionTimeout connection time out in milliseconds
     * @param socketTimeout socket time out in milliseconds
     */
    public SOAPClient(int connectionTimeout, int socketTimeout) {
        setup();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, socketTimeout);
    }

    /**
     * Sets up the client
     */
    private void setup() {
        httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        httpclient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                return false;
            }
        });
    }

    /**
     * Sends a simple SOAP message and returns the response.
     *
     * @param url     HTTP service url to send message
     * @param payload final payload as a String to be sent. Message should adhere to SOAP principles
     * @param action  SOAP Action to set. Specify with "urn:"
     * @param headers HTTP headers to be set as a MAP <Header name, Header value>
     * @return HTTP response after invocation
     * @throws IOException in case of an transport error invoking service
     */
    public HttpResponse sendSimpleSOAPMessage(String url, final String payload, String action,
                                              final Map<String, String> headers) throws IOException {
        HttpUriRequest request = new HttpPost(url);
        HTTPUtils.setHTTPHeader(SOAP_ACTION, action, request);

        if(headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                HTTPUtils.setHTTPHeader(header.getKey(),header.getValue(), request);
            }
        }

        final boolean zip = headers != null && "gzip".equals(headers.get(HttpHeaders.CONTENT_ENCODING));

        HttpEntityEnclosingRequest entityEncReq = (HttpEntityEnclosingRequest) request;
        EntityTemplate ent = new EntityTemplate(new ContentProducer() {
            public void writeTo(OutputStream outputStream) throws IOException {
                OutputStream out = outputStream;
                if (zip) {
                    out = new GZIPOutputStream(outputStream);
                }
                out.write(payload.getBytes());
                out.flush();
                out.close();
            }
        });
        ent.setContentType(HttpConstants.MEDIA_TYPE_TEXT_XML);
        if (zip) {
            ent.setContentEncoding("gzip");
        }

        entityEncReq.setEntity(ent);
        return this.httpclient.execute(request);
    }

    /**
     * Sends a simple SOAP message and returns the response.
     *
     * @param url     HTTP service url to send message
     * @param payload final payload as a String to be sent. Message should adhere to SOAP principles
     * @param action  SOAP Action to set. Specify with "urn:"
     * @return HTTP response after invocation
     * @throws IOException in case of an transport error invoking service
     */
    public HttpResponse sendSimpleSOAPMessage(String url, final String payload, String action) throws IOException {
        return sendSimpleSOAPMessage(url, payload, action, null);
    }

    /**
     * Sends a  SOAP message with WS security headers and returns the response.
     *
     * @param url     HTTP service url to send message
     * @param payload final payload as a String to be sent. Message should adhere to SOAP principles
     * @param action  SOAP Action to set. Specify with "urn:"
     * @return HTTP response after invocation
     */
    public HttpResponse sendSOAPMessageWithWSSecurity(String url, final String payload, String action) {
        throw new UnsupportedOperationException();
    }

    /**
     * Set WS security header to the SOAP message\
     *
     * @param headers WS headers as a map <header name, header value>
     */
    public void setWSHeader(Map<String, String> headers) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends a SOAP message with MTOM attachment and returns response as a OMElement.
     *
     * @param fileName   name of file to be sent as an attachment
     * @param targetEPR  service url
     * @param soapAction SOAP Action to set. Specify with "urn:"
     * @return OMElement response from BE service
     * @throws AxisFault in case of an invocation failure
     */
    public OMElement sendSOAPMessageWithAttachment(String fileName, String targetEPR, String soapAction) throws AxisFault {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://services.samples", "m0");
        OMElement payload = factory.createOMElement("uploadFileUsingMTOM", ns);
        OMElement request = factory.createOMElement("request", ns);
        OMElement image = factory.createOMElement("image", ns);

        FileDataSource fileDataSource = new FileDataSource(new File(fileName));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        OMText textData = factory.createOMText(dataHandler, true);
        image.addChild(textData);
        request.addChild(image);
        payload.addChild(request);

        ServiceClient serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(targetEPR));
        options.setAction(soapAction);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setCallTransportCleanup(true);
        serviceClient.setOptions(options);
        return serviceClient.sendReceive(payload);
    }

}
