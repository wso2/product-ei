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
package org.wso2.carbon.esb.rabbitmq.message.store.jira;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.esb.rabbitmq.utils.RabbitMQTestUtils;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;
import org.wso2.esb.integration.common.utils.servers.RabbitMQServer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

//import java.io.File;

/**
 * This class is to test SSL transport of the rabbitMQ message store, in this test, we assume that rabbitMQ server is configured for SSL
 * but not validation client certificates, This is written as platform test and make it skipped by default because we can't ship
 * rabbitMQ server inside ESB
 */
public class ESBJAVA4569RabbiMQSSLStoreWithoutClientCertValidationTest extends ESBIntegrationTest {
    private static String url = "http://localhost:8480/rabbitMQRestWithoutClientCert/store";
    private final SimpleHttpClient httpClient = new SimpleHttpClient();

    private final Map<String, String> headers = new HashMap<String, String>(1);

    private final String payload =  "{\n" +
                                    "  \"email\" : \"jms@yomail.com\",\n" +
                                    "  \"firstName\" : \"Jms\",\n" +
                                    "  \"lastName\" : \"Broker\",\n" +
                                    "  \"id\" : 10\n" +
                                    "}";

    private LogViewerClient logViewer;

    private static final String logLine0 =
            "MESSAGE = ************rabbitMQRestWithoutClientCert IN, IN-Content-Type = application/json, IN-Test-Header-Field = TestHeaderValue";

    private RabbitMQServer rabbitMQServer;


    private File destinationConfig = null;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();

        rabbitMQServer = RabbitMQTestUtils.getRabbitMQServerInstance();

        rabbitMQServer.stop();
        modifyAndAddRabbitMQConfigs();
        rabbitMQServer.start();


        headers.put("Test-Header-Field", "TestHeaderValue");

        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/messageStore/rabbitMQ/SSL/RabbitMQMsgStoreSSLWithoutClientCertValidationTest.xml");
        updateESBConfiguration(synapse);

        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(),getSessionCookie());
        url = getApiInvocationURL("rabbitMQRestWithoutClientCert") + "/store";
    }

    @Test(groups = {"wso2.esb"}, description = "RabbitMQ message store support for SSL(without client certificate validation)" )
    public void testRabbitMQMessageStore() throws Exception {

        HttpResponse response = httpClient.doPost(url, headers, payload, "application/json");
        if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 202) {
            assertTrue(true);
        } else {
            assertTrue(false);
        }

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int i = 1;
        for (LogEvent log : logs) {
            if (log.getMessage().contains(logLine0)) {
                ++i;
            }
        }

        if (i == 2) {
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false);
        }

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        super.init();
        rabbitMQServer.stop();
        if (destinationConfig != null) {
            RabbitMQTestUtils.restoreConfigs(destinationConfig);
        }
        loadSampleESBConfiguration(0);
    }

    /**
     * Helper method to retrieve queue message from rabbitMQ
     *
     * @return result
     * @throws Exception
     */
    private static String consumeWithoutCertificate() throws Exception {
        String result = "";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5671);
        factory.useSslProtocol();

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        GetResponse chResponse = channel.basicGet("WithoutClientCertQueue", true);
        if(chResponse != null) {
            byte[] body = chResponse.getBody();
            result = new String(body);
        }
        channel.close();
        conn.close();
        return result;
    }

    /**
     * Helper method to copy configs before starting rabbitMQ
     *
     * @throws Exception
     */
    private void modifyAndAddRabbitMQConfigs() throws Exception {
        String basePath = TestConfigurationProvider.getResourceLocation() + "/artifacts/ESB/messageStore/rabbitMQ/SSL/";
        String cacertfile = basePath + "rabbitMQ/certs/cacert.pem";
        String certfile = basePath + "rabbitMQ/certs/cert.pem";
        String keyfile = basePath + "rabbitMQ/certs/key.pem";

        File configFile = new File(basePath + "rabbitMQ/configs/rabbitmqSSLWithoutClientCertValidation.config");

        String content = FileUtils.readFileToString(configFile, "UTF-8");
        content = content.replaceAll("%cacertfile%", cacertfile);
        content = content.replaceAll("%certfile%", certfile);
        content = content.replaceAll("%keyfile%", keyfile);
        File tempFile = new File(basePath + "rabbitMQ/configs/rabbitmqSSLWithoutClientCertValidationTMP.config");
        try {
            FileUtils.writeStringToFile(tempFile, content, "UTF-8");
            destinationConfig = new File(RabbitMQTestUtils.getRabbitmqHomePath() + "/../etc/rabbitmq/rabbitmq.config");
            RabbitMQTestUtils.copyConfig(tempFile, destinationConfig);
        } finally {
            tempFile.delete();
        }
    }
}
