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

import org.apache.commons.lang.time.StopWatch;
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
 * Sample 9: Introduction to Dynamic Sequences with the Registry
 */
public class Sample9TestCase extends ESBIntegrationTest {
    private String oldSynapseConfig;
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        File sourceFile = new File(getESBResourceLocation() + File.separator +
                                   "samples" + File.separator + "synapse_sample_9.xml");

        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        oldSynapseConfig = synapseConfigAdminClient.getConfiguration();
        synapseConfigAdminClient.updateConfiguration(sourceFile);
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = { "wso2.esb" },
          description = "Introduction to Dynamic Sequences with the Registry")
    public void testDynamicSequenceWithRegistry() throws Exception {

        LogViewerClient logViewerClient =
            new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        boolean foundFirstMessage = false;
        boolean foundSecondMessage = false;
        int round = 1;
        while (!foundSecondMessage && stopWatch.getTime() < 20000L) {

            logViewerClient.clearLogs();
            axis2Client.sendSimpleStockQuoteRequest(
                getMainSequenceURL(),
                getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
                "WSO2");

            LogEvent[] getLogsInfo = logViewerClient.getAllSystemLogs();
            for (LogEvent event : getLogsInfo) {
                if (event.getMessage().contains("message = *** Test Message 1 ***")) {
                    foundFirstMessage = true;
                    log.info("Message [message = *** Test Message 1 ***] found at : " +
                             stopWatch.getTime());
                }
                if (event.getMessage().contains("message = *** Test Message 2 ***")) {
                    log.info("Message [message = *** Test Message 2 ***] found at : " +
                             stopWatch.getTime());
                    foundSecondMessage = true;
                    break;
                }
            }
            if (round == 1) {
                updateSequenceFile();
            }
            round++;
            Thread.sleep(4000);
        }

        stopWatch.stop();
        Assert.assertTrue(foundFirstMessage,
                          "Message [message = *** Test Message 1 ***] not found");
        Assert.assertTrue(foundSecondMessage,
                          "Message [message = *** Test Message 2 ***] not found");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

        SynapseConfigAdminClient synapseConfigAdminClient =
            new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        synapseConfigAdminClient.updateConfiguration(oldSynapseConfig);

        serverConfigurationManager.restoreToLastConfiguration();
    }

    private void updateSequenceFile() throws Exception {
        serverConfigurationManager = new ServerConfigurationManager(context);
        File sourceFile = new File(
            getESBResourceLocation() + File.separator + "sample_9" + File.separator +
            "dynamic_seq_1.xml"
        );
        File targetFile = new File(
            ServerConfigurationManager.getCarbonHome() + File.separator + "repository" +
            File.separator + "samples" + File.separator + "resources" + File.separator +
            "sequence" + File.separator + "dynamic_seq_1.xml"
        );

        serverConfigurationManager.applyConfiguration(sourceFile, targetFile, true, false);
    }
}
