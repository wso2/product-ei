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

package org.wso2.carbon.esb.mediator.test.xslt;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class XSLTonEmptySoapBodyWithSourceXPath extends ESBIntegrationTest {

    private final SimpleHttpClient httpClient = new SimpleHttpClient();

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                "artifacts" + File.separator + "ESB" + File.separator + "mediatorconfig" + File.separator + "xslt"
                        + File.separator + "xslt_empty_soap_body.xml");
    }

    @Test(groups = "wso2.esb",
          description = "xslt mediator should be able to transform empty soap body messages when the source xpath is specified")
    public void testXSLTMediatorForEmptySOAPBody() throws Exception {

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("accept", "text/xml");

        HttpResponse response = httpClient.doGet(getApiInvocationURL("xsltTest"), requestHeaders);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);

        String payload = new String(outputStream.toByteArray());

        assertTrue(payload.contains("Hello"), "Response should contain 'Hello'");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
