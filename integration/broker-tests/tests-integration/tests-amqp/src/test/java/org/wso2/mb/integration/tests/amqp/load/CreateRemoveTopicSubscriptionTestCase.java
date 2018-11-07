/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.load;

import org.testng.annotations.Test;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientConfigurationException;
import org.wso2.mb.integration.common.clients.exceptions.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.io.IOException;

/**
 * Test case to create and remove a topic subscriber.
 */
public class CreateRemoveTopicSubscriptionTestCase {

    /**
     * Creating and removing of a topic subscription
     *
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     * @throws AndesClientConfigurationException
     * @throws AndesClientException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void testCreateRemoveSubscription()
            throws NamingException, JMSException, IOException, AndesClientConfigurationException,
                   AndesClientException {
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.TOPIC, "TestTopic");

        AndesClient consumerClient = new AndesClient(consumerConfig, true);

        consumerClient.stopClient();
    }
}
