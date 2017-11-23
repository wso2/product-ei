/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.security.test.tryit;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.testng.Assert.assertEquals;

/**
 * Test class to test whether is it possible to read files outside the JAR using '?wsdl2form&contentType=text/html&resource=.'.
 */
public class ResourceRetrievalUsingWsdl2FormTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Tests wsdl2form arbitrary file retrieval")
    public void testResourceRetrievalUsingWsdl2Form() throws InterruptedException, IOException {
        int expectedStatusCode = 400;
        String url = getProxyServiceURLHttp("echo") + "?wsdl2form&contentType=text/html&resource=.";
        URL connectionURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) connectionURL.openConnection();
        connection.setRequestMethod("GET");
        int actualStatusCode = connection.getResponseCode();
        assertEquals(actualStatusCode, expectedStatusCode,
                "Response status code should be " + expectedStatusCode + " but received " + actualStatusCode);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
        super.cleanup();
    }
}