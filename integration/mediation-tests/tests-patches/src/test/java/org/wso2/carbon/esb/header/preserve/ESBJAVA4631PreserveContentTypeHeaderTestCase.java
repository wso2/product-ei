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

package org.wso2.carbon.esb.header.preserve;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * When we set "http.headers.preserve=Content-Type" in "passthru-http.properties" file, then that header shouldn't
 * get changed when sending requests to back ends. This test is to test that behavior (this property is already
 * there in "passthru-http.properties" files in latest ESBs)
 */
public class ESBJAVA4631PreserveContentTypeHeaderTestCase extends ESBIntegrationTest {

    WireMonitorServer wireMonitorServer;

    @BeforeTest(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        wireMonitorServer = new WireMonitorServer(6770);
        wireMonitorServer.start();

        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/transport/header/PreserveContentTypeHeaderTest.xml");
    }

    @Test(groups = "wso2.esb", description = "Test to check whether the Content-Type header is preserved when sending " +
                                             "requests to back end")
    public void testPreserveContentTypeHeader() throws Exception {

        String proxyServiceUrl = getProxyServiceURLHttp("PreserveContentTypeHeaderTest");

        String requestPayload = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' >"
                                + "<soapenv:Body>"
                                + "</soapenv:Body></soapenv:Envelope> ";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Soapaction", "urn:mediate");
        headers.put("Content-type", "application/xml");

        HttpRequestUtil.doPost(new URL(proxyServiceUrl), requestPayload, headers);

        String wireResponse = wireMonitorServer.getCapturedMessage();
        String[] wireResponseList = wireResponse.split(System.lineSeparator());

        Assert.assertTrue(wireResponse.contains("Content-Type"));
        for (String line : wireResponseList) {
            if (line.contains("Content-Type")) {
                if (line.contains(";")) {
                    Assert.fail("Content-Type header was modified - " + line);
                }
            }
        }
        //coming to this line means content type header is in expected state, hence passing the test
        Assert.assertTrue(true);
    }

    @AfterTest(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
