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

import org.apache.axiom.om.OMElement;
import org.awaitility.Awaitility;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerManager;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerType;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.clients.logging.LoggingAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.services.jaxrs.customersample.CustomerConfig;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Check if the message processor runs the forwarder with the specified interval along with the cron interval
 * https://wso2.org/jira/browse/ESBJAVA-3034
 */
public class MSMPCronForwarderCase extends ESBIntegrationTest {

    private TomcatServerManager tomcatServerManager;
    private LoggingAdminClient logAdmin;
    private final int NUMBER_OF_MESSAGES = 4;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        // START THE ESB
        super.init();
        // START THE SERVER
        tomcatServerManager = new TomcatServerManager(
                CustomerConfig.class.getName(), TomcatServerType.jaxrs.name(), 8080);

        tomcatServerManager.startServer();  // staring tomcat server instance
        Awaitility.await()
                  .pollInterval(50, TimeUnit.MILLISECONDS)
                  .atMost(60, TimeUnit.SECONDS)
                  .until(isServerStarted());
        logAdmin = new LoggingAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Test Cron Forwarding of message processor")
    public void testMessageProcessorCronForwader() throws Exception {
        logAdmin.updateLoggerData("org.apache.synapse", LoggingAdminClient.LogLevel.DEBUG.name(), true, false);

        OMElement synapse = esbUtils.loadResource("/artifacts/ESB/jms/transport/MSMP_CRON_WITH_FORWARDER.xml");
        updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));

        // SEND THE REQUEST
        String addUrl = getProxyServiceURLHttp("MSMPRetrytest");
        String payload = "{\"name\":\"Jack\"}";

        Map<String, String>   headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        // need to send two request
        HttpResponse response1 = HttpRequestUtil.doPost(new URL(addUrl), payload, headers);
        HttpResponse response2 = HttpRequestUtil.doPost(new URL(addUrl), payload, headers );
        HttpResponse response3 = HttpRequestUtil.doPost(new URL(addUrl), payload, headers );
        HttpResponse response4 = HttpRequestUtil.doPost(new URL(addUrl), payload, headers );

        // IT HAS TO BE 202 ACCEPTED
        assertEquals(response1.getResponseCode(), 202, "ESB failed to send 202 even after setting FORCE_SC_ACCEPTED");
        assertEquals(response2.getResponseCode(), 202, "ESB failed to send 202 even after setting FORCE_SC_ACCEPTED");
        assertEquals(response3.getResponseCode(), 202, "ESB failed to send 202 even after setting FORCE_SC_ACCEPTED");
        assertEquals(response4.getResponseCode(), 202, "ESB failed to send 202 even after setting FORCE_SC_ACCEPTED");

        // WAIT FOR THE MESSAGE PROCESSOR TO TRIGGER
        Awaitility.await()
                  .pollInterval(50, TimeUnit.MILLISECONDS)
                  .atMost(60, TimeUnit.SECONDS)
                  .until(isLogWritten());
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        //undo logger change
        logAdmin.updateLoggerData("org.apache.synapse", LoggingAdminClient.LogLevel.INFO.name(), true, false);
        if (tomcatServerManager != null) {
            tomcatServerManager.stop();
        }
        super.cleanup();
    }

    private Callable<Boolean> isServerStarted() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                return tomcatServerManager.isRunning();
            }
        };
    }

    private Callable<Boolean> isLogWritten() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                LogViewerClient logViewerClient =
                        new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
                LogEvent[] logEvents = logViewerClient.getAllSystemLogs();

                boolean success = false;
                int msgCount = 0;
                for (int i = 0; i < logEvents.length; i++) {
                    if (logEvents[i].getMessage().contains("Jack")) {
                        msgCount = ++msgCount;
                        if (NUMBER_OF_MESSAGES == msgCount) {
                            success = true;
                            break;
                        }
                    }
                }
                return success;
            }
        };
    }
}
