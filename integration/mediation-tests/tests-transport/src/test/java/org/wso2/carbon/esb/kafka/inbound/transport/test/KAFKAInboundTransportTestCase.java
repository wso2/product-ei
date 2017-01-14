/**
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.wso2.carbon.esb.kafka.inbound.transport.test;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.clients.inbound.endpoint.InboundAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import javax.jms.JMSException;
import kafka.javaapi.producer.Producer;
import java.util.Properties;

public class KAFKAInboundTransportTestCase extends ESBIntegrationTest {
    private LogViewerClient logViewerClient = null;
    private ServerConfigurationManager serverConfigurationManager;
    private InboundAdminClient inboundAdminClient;
    int beforeLogCount;

    Producer<String, String> producer = null;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
        inboundAdminClient = new InboundAdminClient(context.getContextUrls().getBackEndUrl(),getSessionCookie());
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        Properties properties = new Properties();
        properties.put("metadata.broker.list", "localhost:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        producer = new kafka.javaapi.producer.Producer<String, String>(producerConfig);
    }

    /** consume the message using highlevel configuration */
    @Test(groups = { "wso2.esb" }, description = "Successfully consuming highlevel message")
    public void testConsumeHighlevelMessage() throws Exception {

        try {
            addInboundEndpoint(esbUtils.loadResource("artifacts/ESB/kafka/inbound/transport/kafka_commit_synapse.xml"));
            this.beforeLogCount = logViewerClient.getAllSystemLogs().length;
            KeyedMessage<String, String> message = new KeyedMessage<String, String>("test", "" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                    "                  xmlns:ns= \"http://services.samples\"\n" +
                    "                  xmlns:xsd=\"http://services.samples/xsd\">\n" +
                    "    <soapenv:Header/>\n" +
                    "    <soapenv:Body>\n" +
                    "        <ns:getQuote >\n" +
                    "            <ns:request>\n" +
                    "                <ns:symbol>IBM</ns:symbol>\n" +
                    "            </ns:request>\n" +
                    "        </ns:getQuote>\n" +
                    "    </soapenv:Body>\n" +
                    "</soapenv:Envelope>" );
            producer.send(message);
            Thread.sleep(5000);
        } catch (Exception e) {
            log.info(e + ", Error while adding the inbound endpoint");
        }

        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        boolean status = false;
        for (int i = 0; i < (logs.length - beforeLogCount); i++) {
            if (logs[i].getMessage().contains("IBM")) {
                status = true;
                break;
            }
        }
        Assert.assertTrue(status, "Couldn't Consume messages from Queue");
        deleteInboundEndpoints();
    }

    /** consuming the message from the Topic */
    @Test(groups = { "wso2.esb" }, description = "Successfully consuming the message from the Topic")
    public void testConsumingMessageFromTopic() throws Exception {
        try {
            addInboundEndpoint(esbUtils.loadResource("artifacts/ESB/kafka/inbound/transport/kafka_commit_synapse1" +
                    ".xml"));
            addInboundEndpoint(esbUtils.loadResource("artifacts/ESB/kafka/inbound/transport/kafka_commit_synapse2" +
                    ".xml"));
            this.beforeLogCount = logViewerClient.getAllSystemLogs().length;
            KeyedMessage<String, String> message = new KeyedMessage<String, String>("test11", "" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                    "                  xmlns:ns= \"http://services.samples\"\n" +
                    "                  xmlns:xsd=\"http://services.samples/xsd\">\n" +
                    "    <soapenv:Header/>\n" +
                    "    <soapenv:Body>\n" +
                    "        <ns:getQuote >\n" +
                    "            <ns:request>\n" +
                    "                <ns:symbol>IBM</ns:symbol>\n" +
                    "            </ns:request>\n" +
                    "        </ns:getQuote>\n" +
                    "    </soapenv:Body>\n" +
                    "</soapenv:Envelope>");
            producer.send(message);
            Thread.sleep(5000);
        } catch (JMSException e) {
            log.info(e + ", Error while adding the inbound endpoint");
        }

        LogEvent[] logs = logViewerClient.getAllSystemLogs();
        int noOfMessage = 0;
        for (int i = 0; i < (logs.length - beforeLogCount); i++) {
            if (logs[i].getMessage().contains("IBM")) {
                noOfMessage++;
            }
        }
        boolean consumed = false;
        if(noOfMessage==2)
        {
            consumed= true;
        }
        Assert.assertTrue(consumed, "Couldn't Consume messages from the Topic");
        deleteInboundEndpoints();
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        producer.close();
        super.cleanup();
    }

}