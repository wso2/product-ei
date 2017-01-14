/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.proxyservice.test.proxyservices;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.io.*;


/**
 * Test class for issue
 * https://wso2.org/jira/browse/ESBJAVA-1193
 */
public class HttpsServiceViaHttpProxyTestCase extends ESBIntegrationTest {
    private SampleAxis2Server axisServer;
    private final String RESOURCE_NAME = "test_axis2_server_9015.xml";
    private final String MODIFIED_RESOURCE_NAME = "test_axis2_server_9015_test.xml";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init();
        changeConfiguration(RESOURCE_NAME);
        axisServer = new SampleAxis2Server(MODIFIED_RESOURCE_NAME);
        axisServer.start();
        axisServer.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        uploadSynapseConfig();
    }

    /**
     * Method to change the axis2 config file dynamically
     *
     * @param file : Config file
     * @throws java.io.IOException
     */
    private void changeConfiguration(String file) throws IOException {
        StringBuilder sb = new StringBuilder();
        File config =
                new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator +
                        "artifacts" + File.separator + "AXIS2" + File.separator + "config" +
                        File.separator + file);
        if (config != null) {
            String currentLine;
            BufferedReader br = new BufferedReader(new FileReader(config));
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.contains("REPLACE_CK")) {
                    currentLine = currentLine.replace("REPLACE_CK",
                            System.getProperty(ServerConstants.CARBON_HOME) +
                                    File.separator + "repository" + File.separator +
                                    "resources" + File.separator + "security" +
                                    File.separator + "wso2carbon.jks");
                } else if (currentLine.contains("REPLACE_TS")) {
                    currentLine = currentLine.replace("REPLACE_TS",
                            System.getProperty(ServerConstants.CARBON_HOME) +
                                    File.separator + "repository" + File.separator +
                                    "resources" + File.separator + "security" +
                                    File.separator + "client-truststore.jks");
                }
                sb.append(currentLine);
            }
            br.close();
        }
        File newConfig =
                new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator +
                        "artifacts" + File.separator + "AXIS2" + File.separator + "config" +
                        File.separator + MODIFIED_RESOURCE_NAME);
        if (newConfig.exists()) {
            FileUtils.deleteQuietly(newConfig);
        }

        FileUtils.touch(newConfig);
        OutputStream os = FileUtils.openOutputStream(newConfig);
        os.write(sb.toString().getBytes("UTF-8"));
        os.close();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Patch : ESB-JAVA 1193 : HTTPS request via HTTP proxy")
    public void testHttpsRequestViaHttpProxy() throws AxisFault {
        OMElement response;
        response =
                axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("TestProxy"),
                        null, "IBM");
        Assert.assertTrue(response.toString().contains("IBM"),
                "Asserting response for string 'IBM'");
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {

        File toDelete =
                new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator +
                        "artifacts" + File.separator + "AXIS2" + File.separator + "config" +
                        File.separator + MODIFIED_RESOURCE_NAME);
        if (toDelete.exists()) {
            FileUtils.deleteQuietly(toDelete);
        }
        axisServer.stop();
        axisServer = null;
        cleanup();
    }

    private void uploadSynapseConfig() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/patchAutomation/https_request_via_http_proxy_synapse.xml");
    }


}
