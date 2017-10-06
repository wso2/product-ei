/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.mb.test.amqp;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.mb.test.client.QueueReceiver;
import org.wso2.ei.mb.test.utils.ConfigurationReader;
import org.wso2.ei.mb.test.utils.JMSAcknowledgeMode;

import java.io.IOException;
import javax.jms.JMSException;

/**
 * Test cases for user authentication related scenarios for connection creation
 */
public class UserAuthTestCase extends BrokerTest {

    private ConfigurationReader invalidCredentialsConfig;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        // Loading client configs with invalid user credentials
        invalidCredentialsConfig = ConfigurationReader.getClientConfigForInvalidUser();

    }

    /**
     * Create a queue connection with invalid user credentials
     *
     * @throws Exception
     */
    @Test(groups = "wso2.mb", expectedExceptions = JMSException.class)
    public void performInvalidConnectionTestCase() throws Exception {
        QueueReceiver queueReceiver = new QueueReceiver("InvalidConnectionQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                invalidCredentialsConfig);
        Assert.assertNull(queueReceiver, "Invalid user based connection made.");
        Assert.fail("Expected exception not thrown for invalid user");

    }

    /**
     * Create a queue connection with valid user credentials
     *
     * @throws Exception
     */
    @Test(groups = "wso2.mb")
    public void performValidConnectionTestCase() throws Exception {
        QueueReceiver queueReceiver = null;
        try {
            queueReceiver = new QueueReceiver("ValidConnectionQueue", JMSAcknowledgeMode.AUTO_ACKNOWLEDGE,
                    configurationReader);
            Assert.assertNotNull(queueReceiver, "Queue connection is not successful.");
        } finally {
            if (queueReceiver != null) {
                queueReceiver.closeReceiver();
            }
        }
    }

    /**
     * Clean up after test case.
     */
    @AfterClass(alwaysRun = true)
    public void clean() throws IOException {
        super.cleanup();
    }

}
