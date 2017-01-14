/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * When ESB received requests with content-type with charset, ES should pass the charset to the backend along with the
 * content type. This test is to test that behavior.(https://wso2.org/jira/browse/ESBJAVA-4931)
 */
public class ESBJAVA4931PreserveContentTypeHeaderCharSetTestCase extends ESBIntegrationTest {

    WireMonitorServer wireMonitorServer;

    @BeforeTest(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        wireMonitorServer = new WireMonitorServer(6780);
        wireMonitorServer.start();
        File sourceFile = new File(getESBResourceLocation() + File.separator + "passthru" + File.separator +
                "transport" + File.separator + "ESBJAVA4931" + File.separator + "sample_proxy_3.wsdl");
        File targetFile = new File(System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository"
                + File.separator + "samples" + File.separator + "resources" + File.separator + "proxy" +
                File.separator + "sample_proxy_3.wsdl");
        FileUtils.copyFile(sourceFile, targetFile);
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA4931/PreserveContentTypeHeaderCharSetTest.xml");
    }

    @Test(groups = "wso2.esb", description = "Test to check whether the Content-Type header charset is preserved when sending " +
            "requests to back end")
    public void testPreserveContentTypeHeader() throws Exception {

        String proxyServiceUrl = getProxyServiceURLHttp("PreserveContentTypeHeaderCharSetTest");

        String requestPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <p:getSimpleQuote xmlns:p=\"http://services.samples\">\n" +
                "      <xs:symbol xmlns:xs=\"http://services.samples\">IBM</xs:symbol>\n" +
                "   </p:getSimpleQuote>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("SOAPAction", "urn:mediate");
        headers.put("Content-type", "text/xml;charset=UTF-8");

        HttpRequestUtil.doPost(new URL(proxyServiceUrl), requestPayload, headers);

        String wireResponse = wireMonitorServer.getCapturedMessage();
        String[] wireResponseList = wireResponse.split(System.lineSeparator());

        Assert.assertTrue(wireResponse.contains("Content-Type"), "Request to the backend doesn't contain Content-Type header");
        boolean isCharSetPreserved = false;
        for (String line : wireResponseList) {
            if (line.contains("Content-Type")) {
                if (line.contains("text/xml; charset=UTF-8")) {
                    isCharSetPreserved = true;
                }
            }
        }
        Assert.assertTrue(isCharSetPreserved, "Charset has been dropped from Content-Type header");
    }

    @AfterTest(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
