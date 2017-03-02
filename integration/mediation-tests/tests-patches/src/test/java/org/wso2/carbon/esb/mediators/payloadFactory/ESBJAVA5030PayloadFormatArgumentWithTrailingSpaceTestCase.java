/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediators.payloadFactory;

import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This test class will test the functionality of payload factory arg values having xml value with a trailing space.
 * Earlier this gets identified as non xml, but since this is actually a xml, it needs to be identified as an xml.
 */
public class ESBJAVA5030PayloadFormatArgumentWithTrailingSpaceTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        // applying changes to esb - source view
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/payloadmediatype/" +
                                          "expressionForTrailingSpace.xml");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "invoke service - Trailing space trim check")
    public void testPayloadFactoryArgsWithTrailingSpaces()
            throws AxisFault, MalformedURLException, AutomationFrameworkException {

        //json request payload.
        String payload = "{\n" +
                         "  \"input\": { \"value\": \"<abc>sample</abc> \" }\n" +
                         "}";

        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String serviceURL = this.getApiInvocationURL("trailingSpaceAPI");

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                                                                                 new URL(serviceURL), writer, "application/json");

        assertNotNull(response, "Response is null");
        //should return the response without throwing any errors.
        assertTrue(response.contains("output\": \"{\"abc\":\"sample\"}"));
    }
}
