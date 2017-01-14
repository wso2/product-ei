/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.rabbitmq.transport.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.esb.rabbitmq.utils.RabbitMQTestUtils;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.RabbitMQServer;

import java.io.File;

/**
 * Starts rabbitmq server, loads axi2.xml before running other tests in the package and stops server and restore axis2
 * config after all tests in the package is executed.
 */
public class RabbitMQBrokerStartupTestCase extends ESBIntegrationTest {

    private RabbitMQServer rabbitMQServer;
    private ServerConfigurationManager configurationManagerAxis2;

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeTest(alwaysRun = true)
    public void startRabbitMQBrokerAndConfigureESB() throws Exception {
        super.init();

        rabbitMQServer = RabbitMQTestUtils.getRabbitMQServerInstance();
        rabbitMQServer.start();
        rabbitMQServer.initialize();

        configurationManagerAxis2 =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        File customAxisConfigAxis2 = new File(getESBResourceLocation() + File.separator +
                "axis2config" + File.separator + "axis2.xml");
        configurationManagerAxis2.applyConfiguration(customAxisConfigAxis2);
        super.init();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterTest(alwaysRun = true)
    public void stopRabbitMQBrokerAndRestoreESB() throws Exception {
        rabbitMQServer.stop();
        configurationManagerAxis2.restoreToLastConfiguration();
    }
}

