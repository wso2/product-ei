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

package org.wso2.carbon.esb.compression.test.gzip;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.transport.http.CommonsTransportHeaders;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

/**
 * This test class test the 'Content-Encoding = gzip' in http header. Compression and decompression in ESB is tested by this class
 * had to remove  Accept-Encoding in proxy services since simple axis2 service does not support compressed response
 */

public class ESBJAVA2262ContentEncodingGzipTestCase extends ESBIntegrationTest {

    private final String TRANSPORT_HEADERS = "TRANSPORT_HEADERS";
    private final String CONTENT_ENCODING = "Content-Encoding";

    @BeforeClass(alwaysRun = true)
    public void deployProxyServices() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/compression/gzip/gzip-compression.xml");

    }

    @Test(groups = {"wso2.esb"}, description = "having 'Content-Encoding = gzip' within insequence")
    public void testGzipCompressionInsideInSequence() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("sendingGZIPCompressedPayloadToBackEnd"), null, "GZIP");
        Assert.assertTrue(response.toString().contains("GZIP"));
        Assert.assertTrue(response.toString().contains("GZIP Company"));
    }

    @Test(groups = {"wso2.esb"}, description = "having 'Content-Encoding = gzip' within outsequence")
    public void gzipCompressionInsideOutSequenceTest() throws Exception {
        ServiceClient client = getServiceClient(getProxyServiceURLHttp("sendingGZIPCompressedPayloadToClient"));
        client.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_ACCEPT_GZIP, true);
        OMElement response = client.sendReceive(Utils.getStockQuoteRequest("GZIP"));
        OperationContext operationContext = client.getLastOperationContext();
        MessageContext inMessageContext = operationContext.getMessageContext(WSDL2Constants.MESSAGE_LABEL_IN);
        CommonsTransportHeaders transportHeaders = (CommonsTransportHeaders) inMessageContext.getProperty(TRANSPORT_HEADERS);
        Assert.assertTrue(transportHeaders.containsKey(CONTENT_ENCODING), "Response Message not encoded");
        Assert.assertEquals(transportHeaders.get(CONTENT_ENCODING), "gzip", "Response Message not gzip encoded");
        Assert.assertTrue(response.toString().contains("GZIP"));
        Assert.assertTrue(response.toString().contains("GZIP Company"));

    }

    @Test(groups = {"wso2.esb"}, description = "having 'Content-Encoding = gzip' within both in and out sequences and accepting gzip response")
    public void gzipCompressionBothInAndOutSequencesTest() throws Exception {
        ServiceClient client = getServiceClient(getProxyServiceURLHttp("sendAndReceiveGZIPCompressedPayload"));
        client.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_ACCEPT_GZIP, true);
        OMElement response = client.sendReceive(Utils.getStockQuoteRequest("GZIP"));
        OperationContext operationContext = client.getLastOperationContext();
        MessageContext inMessageContext = operationContext.getMessageContext(WSDL2Constants.MESSAGE_LABEL_IN);
        CommonsTransportHeaders transportHeaders = (CommonsTransportHeaders) inMessageContext.getProperty(TRANSPORT_HEADERS);
        Assert.assertTrue(transportHeaders.containsKey(CONTENT_ENCODING), "Response Message not encoded");
        Assert.assertEquals(transportHeaders.get(CONTENT_ENCODING), "gzip", "Response Message not gzip encoded");
        Assert.assertTrue(response.toString().contains("GZIP"));
        Assert.assertTrue(response.toString().contains("GZIP Company"));

    }

    @Test(groups = {"wso2.esb"}, description = "Sending gzip compressed payload to ESB")
    public void sendingGzipCompressedRequestToESBTest() throws Exception {
        ServiceClient client = getServiceClient(getProxyServiceURLHttp("acceptGzipPayload"));
        client.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, true);
        OMElement response = client.sendReceive(Utils.getStockQuoteRequest("GZIP"));
        Assert.assertTrue(response.toString().contains("GZIP"));
        Assert.assertTrue(response.toString().contains("GZIP Company"));
    }

    @Test(groups = {"wso2.esb"}, description = "having 'Accept-Encoding = gzip' within both in and out sequences")
    public void sendingAndReceivingGzipCompressedPayloadTest() throws Exception {
        ServiceClient client = getServiceClient(getProxyServiceURLHttp("sendingGZIPCompressedPayloadToClient"));
        client.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, true);
        client.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_ACCEPT_GZIP, true);
        OMElement response = client.sendReceive(Utils.getStockQuoteRequest("GZIP"));
        OperationContext operationContext = client.getLastOperationContext();
        MessageContext inMessageContext = operationContext.getMessageContext(WSDL2Constants.MESSAGE_LABEL_IN);
        CommonsTransportHeaders transportHeaders = (CommonsTransportHeaders) inMessageContext.getProperty(TRANSPORT_HEADERS);
        Assert.assertTrue(transportHeaders.containsKey(CONTENT_ENCODING), "Response Message not encoded");
        Assert.assertEquals(transportHeaders.get(CONTENT_ENCODING), "gzip", "Response Message not gzip encoded");
        Assert.assertTrue(response.toString().contains("GZIP"));
        Assert.assertTrue(response.toString().contains("GZIP Company"));
    }

    @Test(groups = {"wso2.esb"}, description = "sending and accepting gzip compressed payload")
    public void sendingAndReceivingGzipCompressedRequestInAllPathTest() throws Exception {
        ServiceClient client = getServiceClient(getProxyServiceURLHttp("sendAndReceiveGZIPCompressedPayload"));
        client.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, true);
        client.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_ACCEPT_GZIP, true);
        OMElement response = client.sendReceive(Utils.getStockQuoteRequest("GZIP"));
        OperationContext operationContext = client.getLastOperationContext();
        MessageContext inMessageContext = operationContext.getMessageContext(WSDL2Constants.MESSAGE_LABEL_IN);
        CommonsTransportHeaders transportHeaders = (CommonsTransportHeaders) inMessageContext.getProperty(TRANSPORT_HEADERS);
        Assert.assertTrue(transportHeaders.containsKey(CONTENT_ENCODING), "Response Message not encoded");
        Assert.assertEquals(transportHeaders.get(CONTENT_ENCODING), "gzip", "Response Message not gzip encoded");
        Assert.assertTrue(response.toString().contains("GZIP"));
        Assert.assertTrue(response.toString().contains("GZIP Company"));
    }


    @AfterClass(alwaysRun = true)
    public void unDeployProxyServices() throws Exception {
        super.cleanup();
    }

    private ServiceClient getServiceClient(String trpUrl)
            throws AxisFault {

        ServiceClient serviceClient;
        Options options = new Options();
        serviceClient = new ServiceClient();

        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        }

        options.setAction("urn:getQuote");

        serviceClient.setOptions(options);
        return serviceClient;
    }
}
