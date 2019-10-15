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
package org.wso2.carbon.esb.nhttp.transport.json.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.WireMonitorServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This test case is to test the behaviour of when setCharacterEncoding property is set and not.
 * When setCharacterEncoding is true, ESB should append default character encoding(UTF-8) to the
 * outgoing request and when this property is false, ESB should not forcefully append a default
 * character encoding.
 */
public class ESBJAVA4940CharacterEncodingRemovalTestCase extends ESBIntegrationTest {

    @BeforeClass
    public void init() throws Exception {
        super.init();
        verifyProxyServiceExistence("ESBJAVA4940SetEncodingTrue");
        verifyProxyServiceExistence("ESBJAVA4940SetEncodingFalse");
    }

    @Test(groups = "wso2.esb", description = "Test charset encoding removal when SetEncoding is true")
    public void testRemoveCharsetSetEncodingPropertyTrue() throws Exception {
        Assert.assertTrue(isCharsetEncodingPresent("ESBJAVA4940SetEncodingTrue", 8995),
                          "Charset Encoding is not present in the request sent");

    }

    @Test(groups = "wso2.esb", description = "Test charset encoding removal when SetEncoding is false")
    public void testRemoveCharsetSetEncodingPropertyFalse() throws Exception {
        Assert.assertFalse(isCharsetEncodingPresent("ESBJAVA4940SetEncodingFalse", 8996),
                           "Charset Encoding is present in the request sent");
    }

    /**
     * Sends a request to the wire monitor server via the given proxy service URL and checks if transport headers
     * contain the value 'charset=UTF-8'.
     *
     * @param proxyURL         the proxy service to send the request
     * @param portOfWireServer port on which the wire server should be started
     * @return true if the request received byt the wire monitor contains "charset=UTF-8"
     * @throws Exception if an error occurs while sending the request
     */
    private boolean isCharsetEncodingPresent(String proxyURL, int portOfWireServer) throws Exception {

        //Start wire monitor
        WireMonitorServer wireMonitorServer;
        wireMonitorServer = new WireMonitorServer(portOfWireServer);
        wireMonitorServer.start();

        String payload = "{\"sampleJson\" : \"sampleValue\"}";
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-Type", "application/json");
        HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(proxyURL)), payload, requestHeader);

        //checks for the availability of "charset=UTF-8" in the request captured by the wire monitor
        String capturedMessage = wireMonitorServer.getCapturedMessage();
        return capturedMessage.contains("charset=UTF-8");
    }

    @AfterClass
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}
