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

package org.wso2.carbon.esb.endpoint.test;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class ESBJAVA3047Soap12EndpointTestCase extends ESBIntegrationTest {

    private WireMonitorServer wireMonitorServer;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        wireMonitorServer = new WireMonitorServer(8888);
        wireMonitorServer.start();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" +
                                          File.separator + "endpoint" + File.separator +
                                          "soap12EndpointConfig" + File.separator + "synapse.xml");

    }

    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Sending a Message to a Soap12 endpoint and checks weather 'action' is present in Content-Type header")
    public void testSoap12EndpointForAction() throws IOException,
                                                     EndpointAdminEndpointAdminException,
                                                     LoginAuthenticationExceptionException,
                                                     XMLStreamException {
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("soap12ActionHeader"),
                                                    null, "WSO2");

        } catch (Exception ex) {
            // ignore the read time out
        }

        String capturedMessage = wireMonitorServer.getCapturedMessage();
        Assert.assertNotNull(capturedMessage,
                             "Wire Moniter server couldn't catch soap message sent to stock quote service");
        String newLineChar = System.getProperty("line.separator");

        String[] tokenizedPayload = StringUtils.split(capturedMessage, newLineChar);
        boolean isContentTypeHeaderFound = false;
        String contentTypeHeader = null;

        for (String headerOrPayLoadFragment : tokenizedPayload) {
            if (headerOrPayLoadFragment.startsWith("Content-Type:")) {
                contentTypeHeader = headerOrPayLoadFragment;
                // we found the content-type http header
                isContentTypeHeaderFound =
                        headerOrPayLoadFragment.contains("action=\"urn:getQuote\"");
                break;
            }

        }

        Assert.assertTrue(isContentTypeHeaderFound,
                          "Unable to find the action in Content-type head for SOAP 1.2 message: header returned [ " +
                          contentTypeHeader + "]");

    }

}
