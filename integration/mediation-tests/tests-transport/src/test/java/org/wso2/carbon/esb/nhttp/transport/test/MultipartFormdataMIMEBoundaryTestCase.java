/**
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.nhttp.transport.test;


import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

/**
 *This test class is to check the presence of MIMEBoundary value in Content-Type header
 * when converting JSON payload to multipart/form-data
 * (https://github.com/wso2/product-ei/issues/780)
 */
public class MultipartFormdataMIMEBoundaryTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("artifacts" + File.separator + "ESB" + File.separator
                + "nhttp" + File.separator + "transport" + File.separator + "MultipartFormdataMIMEBoundaryTest.xml");
    }

    @Test(groups = { "wso2.esb" }, description = "Test for MIMEBoundary value in Content-Type header for multipart/form-data")
    public void testReturnContentType() throws Exception {

        String boundary = "boundary";
        String jsonPayload = "{\"action\":\"ping\"}";

        SimpleHttpClient httpClient = new SimpleHttpClient();

        HttpResponse response = httpClient.doPost(getApiInvocationURL("testMIMEBoundary"), null, jsonPayload, HTTPConstants.MEDIA_TYPE_APPLICATION_JSON);
        String contentTypeData = response.getEntity().getContentType().getValue();

        if (contentTypeData.contains(boundary)) {
            String[] pairs = contentTypeData.split(";");
            for (String pair : pairs) {
                if (pair.contains(boundary)) {
                    String[] boundaryDetails = pair.split("=");
                    Assert.assertTrue(boundaryDetails[1].contains("MIMEBoundary_"), "MIMEBoundary is not set in Content-Type header");
                }
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}
