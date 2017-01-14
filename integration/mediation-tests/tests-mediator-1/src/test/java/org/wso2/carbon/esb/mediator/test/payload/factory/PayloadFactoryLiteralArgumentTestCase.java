/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *
 */

package org.wso2.carbon.esb.mediator.test.payload.factory;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.xpath.XPathExpressionException;

import static org.testng.Assert.assertTrue;

public class PayloadFactoryLiteralArgumentTestCase extends ESBIntegrationTest {

    String payload = "{\"extract\": \"<hello/>\"}"; // actual payload {"extract": "<hello/>"}

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig"
                + File.separator + "payload" + File.separator + "factory" + File.separator + "literal_argument_payload_factory.xml");
    }

    @Test(groups = { "wso2.esb"}, description = "Testing Payload factory mediator with literal argument 'true'")
    public void testPayloadFactoryWithLiteralArgumentTrue()
            throws AxisFault, XPathExpressionException, MalformedURLException, AutomationFrameworkException {
        String serviceURL = getMainSequenceURL() + "literal/true";
        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                new URL(serviceURL), writer, "application/json");

        assertTrue(response.contains("<hello/>"));

    }

    @Test(groups = { "wso2.esb"}, description = "Testing Payload factory mediator with literal argument 'false'")
    public void testPayloadFactoryWithLiteralArgumentFalse()
            throws AxisFault, XPathExpressionException, MalformedURLException, AutomationFrameworkException {
        String serviceURL = getMainSequenceURL() + "literal/false";
        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                new URL(serviceURL), writer, "application/json");

        assertTrue(response.contains("{\"hello\":null}"));

    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }

}
