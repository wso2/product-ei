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
package org.wso2.carbon.esb.websocket.inbound.transport.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.websocket.client.WebSocketTestClient;
import org.wso2.carbon.esb.websocket.server.WebSocketServer;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebSocketInboundTestCase extends ESBIntegrationTest {

    private WebSocketServer webSocketServer;
    private WebSocketTestClient webSocketTestClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        webSocketServer = new WebSocketServer(7474);
        webSocketServer.run();
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/websocket/synapse-websocket-inbound.xml");
    }

    @Test(groups = "wso2.esb",
          description = "Web Socket transport test", timeOut = 60000)
    public void webSocketInboundTest() throws Exception {
        int latchCountDownInSecs = 30;
        CountDownLatch latch = new CountDownLatch(1);
        webSocketTestClient = new WebSocketTestClient("ws://localhost:9091/", latch);
        webSocketTestClient.handhshake();
        String text = "{message:\"hello web socket test\"}";
        webSocketTestClient.sendText(text);
        latch.await(latchCountDownInSecs, TimeUnit.SECONDS);
        Assert.assertEquals(webSocketTestClient.getTextReceived(), text);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            if (webSocketTestClient != null) {
                webSocketTestClient.shutDown();
            }
        } catch (InterruptedException e) {
            log.error("Error while closing the Web Socket Client");
            //ignore
        }
        try {
            super.cleanup();
        } finally {
            if (webSocketServer != null) {
                webSocketServer.stop();
            }
        }

    }
}
