/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.property;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This checks the functionality of FORCE_HTTP_CONTENT_LENGTHT and FORCE_HTTP_CONTENT_LENGTH
 * properties functionality
 */
public class PropertyIntegrationForceHttpContentLengthPropertyTestCase
        extends ESBIntegrationTest {

    public WireMonitorServer wireServer;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        wireServer = new WireMonitorServer(8991);
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }


    @Test(groups = "wso2.esb", description = "Tests when both properties are enabled")
    public void testWithEnableFORCE_HTTP_CONTENT_LENGTHAndCOPY_CONTENT_LENGTH_FROM_INCOMINGTest()
            throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/EnableFORCE_HTTP_CONTENT_LENGTH.xml");

        wireServer.start();

        HttpClient httpclient = new DefaultHttpClient();

        StringEntity strEntity = new StringEntity("<soapenv:Envelope xmlns:soapenv=\"http://schemas." +
                                                  "xmlsoap.org/soap/envelope/\" xmlns:ser=\"" +
                                                  "http://services.samples\" xmlns:xsd=\"" +
                                                  "http://services.samples/xsd\">\n" +
                                                  "   <soapenv:Header/>\n" +
                                                  "   <soapenv:Body>\n" +
                                                  "      <ser:getQuote>\n" +
                                                  "         <!--Optional:-->\n" +
                                                  "         <ser:request>\n" +
                                                  "            <!--Optional:-->\n" +
                                                  "            <xsd:symbol>WSO2</xsd:symbol>\n" +
                                                  "         </ser:request>\n" +
                                                  "      </ser:getQuote>\n" +
                                                  "   </soapenv:Body>\n" +
                                                  "</soapenv:Envelope>", "text/xml", "UTF-8");

        HttpPost post = new HttpPost(getProxyServiceURLHttp("Axis2ProxyService"));
        post.setHeader("SOAPAction","urn:getQuote");
        post.setEntity(strEntity);

        // Execute request
        httpclient.execute(post);

        assertTrue(wireServer.getCapturedMessage().contains("Content-Length"),
                   "Content-Length not found in the out-going message");
   }

    @Test(groups = "wso2.esb", description = "Tests when both properties are disabled",
    dependsOnMethods =
            "testWithEnableFORCE_HTTP_CONTENT_LENGTHAndCOPY_CONTENT_LENGTH_FROM_INCOMINGTest")
    public void testWithDisableFORCE_HTTP_CONTENT_LENGTHAndCOPY_CONTENT_LENGTH_FROM_INCOMINGTest
            () throws Exception {

        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/mediatorconfig/property/DisableFORCE_HTTP_CONTENT_LENGTH.xml");

        wireServer.start();

        HttpClient httpclient = new DefaultHttpClient();

        StringEntity strEntity = new StringEntity("<soapenv:Envelope xmlns:soapenv=\"http://schemas." +
                                                  "xmlsoap.org/soap/envelope/\" xmlns:ser=\"" +
                                                  "http://services.samples\" xmlns:xsd=\"" +
                                                  "http://services.samples/xsd\">\n" +
                                                  "   <soapenv:Header/>\n" +
                                                  "   <soapenv:Body>\n" +
                                                  "      <ser:getQuote>\n" +
                                                  "         <!--Optional:-->\n" +
                                                  "         <ser:request>\n" +
                                                  "            <!--Optional:-->\n" +
                                                  "            <xsd:symbol>WSO2</xsd:symbol>\n" +
                                                  "         </ser:request>\n" +
                                                  "      </ser:getQuote>\n" +
                                                  "   </soapenv:Body>\n" +
                                                  "</soapenv:Envelope>", "text/xml", "UTF-8");
        HttpPost post = new HttpPost(getProxyServiceURLHttp("Axis2ProxyService"));
        post.setHeader("SOAPAction","urn:getQuote");
        post.setEntity(strEntity);

        // Execute request
        httpclient.execute(post);

        assertFalse(wireServer.getCapturedMessage().contains("Content-Length"),
                    "Content-Length found in the out-going message");
    }

}
