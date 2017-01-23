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
 * This test class will test the functionality of payload factory arg values having html begin tag as a string value.
 * Earlier it was identified as XML and payload factory threw and exception. New functionality should identify that as
 * an String and process further(it checks for end ">" tag to verify xml or not as well as full xml parsing).  If we
 * need to avoid full xml parsing, then we can use "deepCheck" attribute in arg element to do that(this value defaults
 * to "true" if deep check is not needed then should specify as "false"). For example then arg element will look like
 * <arg deepCheck="false" evaluator="xml" expression="ctx:abc"/>
 * <p/>
 * This deepchecking will add some perf overhead if the value is actually and xml.
 */
public class ESBJAVA3573PayloadFormatWithBeginHtmlTagArgument extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        // applying changes to esb - source view
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/payloadmediatype/" +
                                          "expressionWithDeepCheck.xml");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = "wso2.esb", description = "invoke service - EvaluatorCheck")
    public void testPayloadFactoryArgsWithXmlBeginAndEndTags()
            throws AxisFault, MalformedURLException, AutomationFrameworkException {

        // json request payload.
        String payload = "{\n" +
                         "  \"input\": { \"value\": \"<êta>&$@%*\\\"\\\" '>\" }\n" +
                         "}";

        Reader data = new StringReader(payload);
        Writer writer = new StringWriter();

        String serviceURL = this.getApiInvocationURL("deepCheckAPI");

        String response = HttpURLConnectionClient.sendPostRequestAndReadResponse(data,
                                                                                 new URL(serviceURL), writer, "application/json");


        assertNotNull(response, "Response is null");
        //should return the response without throwing any errors.
        assertTrue(response.contains("\"output\": \"<êta>&$@%*\\\"\\\" '>\""));
    }
}
