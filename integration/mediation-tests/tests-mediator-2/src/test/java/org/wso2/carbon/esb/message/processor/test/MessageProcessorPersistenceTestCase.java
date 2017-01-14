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
package org.wso2.carbon.esb.message.processor.test;


import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;

import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;import org.wso2.esb.integration.common.clients.mediation.SynapseConfigAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


import static java.io.File.separator;

/**
 * This is related to JIRA issue:    WSO2 Carbon / CARBON-13132
 * Message stores/processor does not persist properly via mediation persistence manager
 */

public class MessageProcessorPersistenceTestCase extends ESBIntegrationTest {

    private SynapseConfigAdminClient synapseConfigAdminClient;
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        synapseConfigAdminClient = new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    /**
     * To check the persistence of configuration with message processor, after restarting ESB
     * Test Artifacts: /artifacts/ESB/messageProcessorConfig/Message_Processor_Persistence_After_Restart_Synapse.xml
     *
     * @throws Exception
     */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.ALL
})
    @Test(groups = "wso2.esb")
    public void testMessagePersistenceAfterRestart() throws Exception {


        // Configuration which contains a message store and a message processor  - did this way to stop dropping XML tags.

        loadESBConfigurationFromClasspath(separator + "artifacts" + separator + "ESB" + separator
                                          + "messageProcessorConfig" + separator + "Message_Processor_Persistence_After_Restart_Synapse.xml");
        // Waits until the config get sets
        Thread.sleep(5000);
        serverConfigurationManager.restartGracefully();
        // Creating new variables for new environment
        super.init();
        synapseConfigAdminClient = new SynapseConfigAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        // Get configuration after restart
        String afterConfig = synapseConfigAdminClient.getConfiguration();

        Assert.assertTrue(afterConfig.contains("MyStore"), "Synapse Configuration doesn't contain message-store after restart");
        Assert.assertTrue(afterConfig.contains("SamplingProcessor"), "Synapse Configuration doesn't contain message-processor after restart");


    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
        serverConfigurationManager = null;
        synapseConfigAdminClient = null;

    }

}
