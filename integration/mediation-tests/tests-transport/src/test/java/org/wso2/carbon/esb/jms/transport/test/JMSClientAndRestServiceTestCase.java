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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerManager;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerType;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.services.jaxrs.customersample.CustomerConfig;

import java.io.File;

/**
 * This class can be used to test JMS Client and REST Service scenario described on
 * http://docs.wso2.org/wiki/display/ESB470/Using+REST+with+a+Proxy+Service#UsingRESTwithaProxyService-RESTClientandRESTService
 */
public class JMSClientAndRestServiceTestCase extends ESBIntegrationTest {

    private TomcatServerManager tomcatServerManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        tomcatServerManager = new TomcatServerManager(
                CustomerConfig.class.getName(), TomcatServerType.jaxrs.name(), 8060);

        tomcatServerManager.startServer();  // staring tomcat server instance
        Thread.sleep(5000);
        super.init();

        //loading new ESB configuration
        loadESBConfigurationFromClasspath("/artifacts/ESB/jms/transport/jmsclient-and-restService.xml");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            tomcatServerManager.stop();
        } finally {
            super.cleanup();
        }

    }

    @Test(groups = "wso2.esb", description = "JMS front-end client sending message to " +
                                             "REST back-end service through ESB")
    public void sendRequest() throws Exception {

        String basedirLocation = System.getProperty("basedir") + File.separator + "target";

        // creation of jms queue message producer instance
        JMSQueueMessageProducer sender = new JMSQueueMessageProducer
                (JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());

        String queueName = "JmsClientAndReastServiceQueue";   // defining a queue name for ActiveMQ

        String message = "<Customer><name>WSO2</name></Customer>";  // message

        try {
            sender.connect(queueName);    //establishment of connection
            sender.pushMessage(message);  // push message to queue
        } finally {
            sender.disconnect();
        }

        // creation of jms queue message consumer instance
        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer
                (JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect(queueName);   //establishment of connection

            // ensure message pop operation is successful
            Assert.assertNotNull(consumer.popMessage(),
                                 "JMS Message Processor not send message to endpoint");

        } finally {
            consumer.disconnect();
        }
    }
}

