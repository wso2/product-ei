/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.esb.passthru.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This test class will test the Host name header replacing with REQUEST_HOST_HEADER.
 * After first call in service chaining, This property is not working.
 * <property name="REQUEST_HOST_HEADER" value="new-host-name1:8280" scope="axis2"/>
 * https://wso2.org/jira/browse/ESBJAVA-4326
 */

public class ESBJAVA4326OverridingHostHeaderTestCase extends ESBIntegrationTest {

    WireMonitorServer wireMonitorServer = new WireMonitorServer(8456);

    @BeforeClass(alwaysRun = true)
    public void deployAPI() throws Exception {

        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/ESBJAVA-4326.xml");

    }

    @Test(groups = {"wso2.esb"}, description = "replacing the host header after first call")
    public void settingHostHeaderTest() throws Exception {
        wireMonitorServer.start();
        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                         "                                          xmlns:xsd=\"http://services.samples/xsd\"\n" +
                         "                                          xmlns:ser=\"http://services.samples\">\n" +
                         "                            <soapenv:Header/>\n" +
                         "                            <soapenv:Body>\n" +
                         "                                <ser:getQuote>\n" +
                         "                                    <ser:request>\n" +
                         "                                        <xsd:symbol>WSO2-LK</xsd:symbol>\n" +
                         "                                    </ser:request>\n" +
                         "                                </ser:getQuote>\n" +
                         "                            </soapenv:Body>\n" +
                         "                        </soapenv:Envelope>";
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "text/xml");
        requestHeader.put("SOAPAction", "urn:getQuote");
        try {
            HttpResponse response = HttpRequestUtil.doPost(new URL(getApiInvocationURL("products/"))
                    , payload, requestHeader);
        }catch (Exception e) {
            //expected
        }

        String outMessage = wireMonitorServer.getCapturedMessage();
        Assert.assertTrue(outMessage.contains("Host: new-host-name2:8280"),
                          "Host name header not replaced with new-host-name2:8280 in out message\n"
                          + outMessage);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupSynapseConfig() throws Exception {
        super.cleanup();
    }
}
