/*
* Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.ei.tests.connectors;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Integration test class for file connector
 */
public class FileConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private final Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment for test cases
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        String connectorName = System.getProperty("connector_name") + "-connector-" +
                System.getProperty("connector_version") + ".zip";
        init(connectorName);

        FtpsProvider.startFtpsServer();
        String connectionUri = FtpsProvider.getConnectionUri();

        String keystorePath = System.getProperty("carbon.home") + "/repository/resources/security/wso2carbon.jks";
        String truststorePath = System.getProperty("carbon.home") + "/repository/resources/security/client-truststore.jks";
        String source = connectionUri + File.separator + "in/?vfs.protection=P&amp;vfs.ssl.keystore=" + keystorePath +
                        "&amp;vfs.ssl.truststore=" + truststorePath +
                        "&amp;vfs.ssl.kspassword=" + Constants.KEYSTORE_PASSWORD +
                        "&amp;vfs.ssl.tspassword=" + Constants.TRUSTSTORE_PASSWORD +
                        "&amp;vfs.ssl.keypassword=" + Constants.KEY_PASSWORD + "&amp;vfs.passive=true";

        connectorProperties.put("source", source);
        connectorProperties.put("destination", System.getProperty("user.dir") + File.separator + "test-classes"
                                               + File.separator + "samples" + File.separator + "out");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
    }

    /**
     * Positive test case for copy file method using FTPS connection
     */
    @Test(groups = {"wso2.esb"}, description = "FileConnector copy file integration test using FTPS")
    public void testCopyFile() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:copy");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                                                       "FileCopyRequest.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertTrue(esbRestResponse.getBody().toString().contains("true"));
    }

    @AfterClass(description = "Shut down FTPS Server", alwaysRun = true)
    public void stopFtpsServer() {
        FtpsProvider.shutdownFtpsServer();
    }
}
