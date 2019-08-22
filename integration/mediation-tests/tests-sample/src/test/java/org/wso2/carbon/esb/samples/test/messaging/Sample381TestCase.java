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
package org.wso2.carbon.esb.samples.test.messaging;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.esb.samples.test.messaging.utils.MDDProducer;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.carbon.esb.samples.test.util.ESBSampleIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

public class Sample381TestCase extends ESBSampleIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void startJMSBrokerAndConfigureESB() throws Exception {

        super.init();
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        super.init();
        loadSampleESBConfiguration(381);
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        //reverting the changes done to esb sever
        super.cleanup();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Test JMS broker with topic")
    public void JMSBrokerTopicTest() throws Exception {
        int numberOfMsgToExpect = 5;

        LogViewerClient logViewerClient =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();

        Thread.sleep(5000);

        MDDProducer mddProducerMSTF = new MDDProducer();

        for (int i = 0; i < numberOfMsgToExpect; i++) {
            mddProducerMSTF.sendMessage("MSTF", "dynamicQueues/JMSBinaryProxy");
        }

        Thread.sleep(5000);

        boolean isRequestLogFound = false;

        LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();

        for (LogEvent event : logEvents) {
            if (event.getMessage().contains("MSTF")) {

                isRequestLogFound = true;
                break;
            }
        }

        Assert.assertTrue(isRequestLogFound, "Request log not found");

    }


}
