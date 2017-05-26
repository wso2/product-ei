/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.esb.mediators.header;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

/**
 * Testcase for WSAddressing support with header mediator
 */
public class ESBJAVA5098_WSAddressingSupportTest extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig" + File.separator + "header" +
                        File.separator + "WSAddressingSupport.xml");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb")
    public void testWSAddressingSupport() throws Exception {
        boolean hasWSAddressing = false;
        Reader data = new StringReader("<payload><element>Test</element></payload>");
        Writer writer = new StringWriter();

        //check for the availability of WS-Addressing as a header
        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data, new URL(getProxyServiceURLHttp(
                "WSAProxy")), writer, "application/xml");

        String expectedResponse = "<headerContent xmlns=\"http://ws.apache.org/ns/synapse\">" +
                                        "<soapenv:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" +
                                            "<wsa:From><wsa:Address>http://localhost:8480/test</wsa:Address></wsa:From>" +
                                            "<wsa:MessageID>urn:uuid:ef503c98-f6c7-4aa4-8e91-d76a2a7efaf4</wsa:MessageID>" +
                                            "<wsa:Action>urn:anonOutInOpResponse</wsa:Action>" +
                                            "<wsa:To>http://localhost:8480/test</wsa:To>" +
                                        "</soapenv:Header>" +
                                    "</headerContent>";

        if (response.contains(expectedResponse)) {
            hasWSAddressing = true;
        }
        Assert.assertTrue(hasWSAddressing, "WS-Addressing headers are not available with header mediator");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
