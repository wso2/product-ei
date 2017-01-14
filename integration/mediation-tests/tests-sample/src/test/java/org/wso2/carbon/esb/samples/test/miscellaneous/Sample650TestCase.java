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
package org.wso2.carbon.esb.samples.test.miscellaneous;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.clients.localentry.LocalEntriesAdminClient;
import org.wso2.esb.integration.common.clients.sequences.SequenceAdminServiceClient;
import org.wso2.esb.integration.common.clients.tasks.TaskAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import java.io.File;

import static org.testng.Assert.assertTrue;

public class Sample650TestCase extends ESBIntegrationTest {

    private EndPointAdminClient endPointAdminClient;
    private LocalEntriesAdminClient localEntriesAdminClient;
    private SequenceAdminServiceClient sequenceAdminServiceClient;
    private TaskAdminClient taskAdminClient;
    private ServerConfigurationManager serverManager = null;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {

        super.init();
        serverManager = new ServerConfigurationManager(context);

        File existingDir = new File(FrameworkPathUtil.getCarbonHome() + File.separator +
                "repository" + File.separator
                + "samples" + File.separator + "synapse_sample_650.xml");

        File newDir = new File( FrameworkPathUtil.getCarbonHome() + File.separator +
                "repository" + File.separator
                + "samples" + File.separator + "default" );

        File targetDir = new File(FrameworkPathUtil.getCarbonHome() + File.separator +
                "repository" + File.separator
                + "samples" + File.separator + "synapse_sample_650.xml" + File.separator + "default");

        FileUtils.moveDirectory(existingDir, newDir);

        FileUtils.moveDirectory(newDir, targetDir);

        serverManager.applyConfiguration(new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "miscellaneous" + File.separator + "axis2.xml"));

       // serverManager.restartGracefully();
        super.init();

        endPointAdminClient = new EndPointAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());

        localEntriesAdminClient = new LocalEntriesAdminClient(contextUrls.getBackEndUrl(),
                getSessionCookie());

        sequenceAdminServiceClient = new SequenceAdminServiceClient(contextUrls.getBackEndUrl(),
                getSessionCookie());

        taskAdminClient = new TaskAdminClient(contextUrls.getBackEndUrl(),
                getSessionCookie());
    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Send sample request")
    public void testSendingToDefinedProxies() throws Exception {

        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("StockQuoteProxy"), null, "IBM");

        boolean ResponseContainsIBM = response.getFirstElement().toString().contains("IBM Company");

        Assert.assertEquals(ResponseContainsIBM, true, "response is invalid from StockQuoteProxy");

        response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("MainStockQuoteProxy"), null, "IBM");

        ResponseContainsIBM = response.getFirstElement().toString().contains("IBM Company");

        Assert.assertEquals(ResponseContainsIBM, true, "response is invalid from MainStockQuoteProxy");

        response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("CustomStockQuoteProxy1"), null, "IBM");

        ResponseContainsIBM = response.getFirstElement().toString().contains("IBM Company");

        Assert.assertEquals(ResponseContainsIBM, true, "response is invalid from CustomStockQuoteProxy1");

        response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("CustomStockQuoteProxy2"), null, "IBM");

        ResponseContainsIBM = response.getFirstElement().toString().contains("IBM Company");

        Assert.assertEquals(ResponseContainsIBM, true, "response is invalid from CustomStockQuoteProxy2");

    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test End points")
    public void testEndPoints() throws Exception {
        assertTrue(endPointAdminClient.getEndpointCount() == 1, "End points not added");
    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test local entries")
    public void testLocalEntries() throws Exception {
        assertTrue(localEntriesAdminClient.getEntryDataCount() > 1, "local entries not added");
    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test Sequences")
    public void testSequences() throws Exception {
        assertTrue(sequenceAdminServiceClient.getSequences().length > 3, "Sequences not added");
    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test tasks")
    public void testTasks() throws Exception {
        assertTrue(taskAdminClient.getScheduleTaskList().size() == 1, "tasks not added");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        Thread.sleep(5000);
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }
    }
}
