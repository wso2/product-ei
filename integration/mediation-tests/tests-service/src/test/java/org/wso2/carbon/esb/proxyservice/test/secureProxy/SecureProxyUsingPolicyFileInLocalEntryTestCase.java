/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.proxyservice.test.secureProxy;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

/**
 * Verifies whether a Proxy service can be secured using a policy file stored as a local entry.
 */
public final class SecureProxyUsingPolicyFileInLocalEntryTestCase extends ESBIntegrationTest {

    @BeforeClass
    protected void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb", description = "Verifies whether a Proxy service can be secured using a policy file "
                                             + "stored as a local entry.")
    public void testPolicyReferenceInWSDLBindings() throws IOException, InterruptedException {
        String epr = contextUrls.getServiceUrl() + "/SecureProxyWithPolicyInLocalEntryTestProxy?wsdl";
        final SimpleHttpClient httpClient = new SimpleHttpClient();
        HttpResponse response = httpClient.doGet(epr, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        response.getEntity().writeTo(bos);
        String wsdlResponse = new String(bos.toByteArray());
        CharSequence expectedTag = "Policy";
        Assert.assertTrue(wsdlResponse.contains(expectedTag),
                "Expected response not received. Receive response: " + wsdlResponse);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
