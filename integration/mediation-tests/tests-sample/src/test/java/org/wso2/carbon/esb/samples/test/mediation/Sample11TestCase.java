/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.samples.test.mediation;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;

import java.io.File;

/**
 * Sample 11: Using a Full Registry-Based Configuration and Sharing a Configuration Between
 * Multiple Instances
 */
public class Sample11TestCase extends ESBIntegrationTest {

    private String oldSynapseConfig;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        File sourceFile = new File(getESBResourceLocation() + File.separator +
                                   "samples" + File.separator + "synapse_sample_11.xml");

        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        oldSynapseConfig = synapseConfigAdminClient.getConfiguration();
        synapseConfigAdminClient.updateConfiguration(sourceFile);

        esbUtils.deleteSequence(contextUrls.getBackEndUrl(), getSessionCookie(), "main");
        esbUtils.deleteSequence(contextUrls.getBackEndUrl(), getSessionCookie(), "fault");

        ServerConfigurationManager serverManager = new ServerConfigurationManager(context);
        Thread.sleep(5000);
        serverManager.restartGracefully();
        super.init();

    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" }, description = "Using a Full Registry-Based Configuration and " +
                                                 "Sharing a Configuration Between Multiple Instances")
    public void testRegistryBasedConfigurations() throws Exception {
        LogViewerClient logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
            getMainSequenceURL(),
            getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
            "WSO2");

        Assert.assertNotNull(response, "Response is null");
        Assert.assertTrue(response.toString().contains("GetQuoteResponse"),
                          "GetQuoteResponse not found");

        LogEvent[] getLogsInfo = logViewerClient.getAllSystemLogs();
        boolean assertValue = false;
        for (LogEvent event : getLogsInfo) {
            if (event.getMessage().contains("message = This is a dynamic ESB configuration")) {
                assertValue = true;
                break;
            }
        }
        Assert.assertTrue(assertValue,
                          "Message [message = This is a dynamic ESB configuration] not found");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        synapseConfigAdminClient.updateConfiguration(oldSynapseConfig);

    }
}
