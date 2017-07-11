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
package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This test case is to check if a response with HTTP Header 202 having a body
 * is built properly inside ESB without being dropped.
 */
public class ESBJAVA5135ResponseBodyWith202TestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void deployArtifacts() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/passthru/" +
                "transport/ESBJAVA-5135.xml");
    }

    /**
     * Test response with 202 and body is built by ESB and responds client
     *
     * @throws AxisFault                    in case of an axis2 level issue when sending
     * @throws MalformedURLException        in case of url is malformed
     * @throws AutomationFrameworkException in case of any other test suite level issue
     */
    @Test(groups = "wso2.esb", description = "Test response with 202 and body is built by ESB and responds client "
            + "properly")
    public void testResponseWith202() throws AxisFault, MalformedURLException, AutomationFrameworkException {
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Content-type", "text/xml");
        requestHeader.put("SOAPAction", "urn:mediate");
        String message = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap"
                + ".org/soap/envelope/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body/>\n"
                + "</soapenv:Envelope>";

        HttpResponse response = HttpRequestUtil.
                doPost(new URL(getProxyServiceURLHttp("mockProxy")), message, requestHeader);

        Assert.assertTrue(response.getData().contains("Hello World"), "Expected response was not"
                + " received. Got " + response.getData());
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
