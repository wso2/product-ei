/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.carbon.esb.message.processor.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;

import static java.io.File.separator;

/**
 * Testcase to test to verify SOAP headers kept intact when message is picked from the message store
 *
 * Git Issue: https://github.com/wso2/product-ei/issues/1031
 */
public class MessageProcessorSOAPHeadersTestcase extends ESBIntegrationTest {

    private WireMonitorServer wireServer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        //create wireMonitor server and start it
        wireServer = new WireMonitorServer(8991);
        wireServer.start();

    }

    @Test(groups = "wso2.esb", description = "Testcase to check preservation of SOAP headers during MSMP scenario")
    public void testPreservationOfSoapHeadersMSMPScennario()
            throws IOException, InterruptedException {
        String payload ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/>"
                + "<soapenv:Body/></soapenv:Envelope>";

        String expectedSOAPHeaderSnippetAtMP =
                "<wsa:To xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">http://localhost:8991/test</wsa:To>";

        boolean expectedHeaderFound = false;
        Map<String, String> headers = new HashMap<String, String>();

        //Add HTTP Headers
        headers.put("Content-Type", "application/xml");
        headers.put("Authorization", "Basic YWRtaW46YWRtaW4=");
        headers.put("Content-Type", "text/xml;charset=UTF-8");
        headers.put("SOAPAction", "urn:mediate");

        //Create HTTP client and invoke the proxy
        SimpleHttpClient httpClient = new SimpleHttpClient();
        httpClient.doPost(getProxyServiceURLHttps("messageProcessorSoapHeaderTestProxy"), headers, payload, "application/xml");

        String response = wireServer.getCapturedMessage();
        log.info("Response from Wire monitor:" + response);

        if (response.contains(expectedSOAPHeaderSnippetAtMP)) {
            expectedHeaderFound = true;
        }
        Assert.assertTrue(expectedHeaderFound, "Expected SOAP Header not available at Message Processor sequence");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
