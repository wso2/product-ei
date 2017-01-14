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
package org.wso2.carbon.esb.jms.transport.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

public class JMSBrokerStartupTestCase extends ESBIntegrationTest {
    private ActiveMQServer activeMQServer = new ActiveMQServer();

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeTest(alwaysRun = true)
    public void startJMSBrokerAndConfigureESB() throws Exception {
        super.init();
        activeMQServer.startJMSBrokerAndConfigureESB();
    }

    @AfterTest(alwaysRun = true)
    public void close() throws Exception {
        activeMQServer.stopJMSBrokerRevertESBConfiguration();
    }
}



