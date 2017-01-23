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

package org.wso2.carbon.esb.mediator.test.foreach;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FixedSizeSymbolGenerator;

/**
 * Tests sending different number of large messages through foreach mediator
 */

public class ForEachLargeMessageTestCase extends ESBIntegrationTest {

    private String symbol;
    private LogViewerClient logViewer;

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/foreach_single_request.xml");
        symbol = FixedSizeSymbolGenerator.generateMessageMB(1);
        logViewer =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Tests large message in small number 5")
    public void testSmallNumbers() throws Exception {
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        OMElement response;
        for (int i = 0; i < 5; i++) {
            response =
                    axis2Client.sendCustomQuoteRequest(getMainSequenceURL(),
                            null, "IBM" + symbol);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.toString().contains("IBM"), "Incorrect symbol in response");
            response = null;
        }

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        int forEachCount = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();
            if (message.contains("foreach = in")) {
                if (!message.contains("IBM")) {
                    Assert.fail("Incorrect message entered ForEach scope. Could not find symbol IBM ..");
                }
                forEachCount++;
            }
        }

        Assert.assertEquals(forEachCount, 5,
                "Count of messages entered ForEach scope is incorrect");

    }

    @Test(groups = "wso2.esb", description = "Tests large message in large number 10")
    public void testLargeNumbers() throws Exception {
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        OMElement response;
        for (int i = 0; i < 10; i++) {
            response =
                    axis2Client.sendCustomQuoteRequest(getMainSequenceURL(),
                            null, "SUN" + symbol);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.toString().contains("SUN"), "Incorrect symbol in response");
        }

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        int forEachCount = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();
            if (message.contains("foreach = in")) {
                if (!message.contains("SUN")) {
                    Assert.fail("Incorrect message entered ForEach scope. Could not find symbol SUN ..");
                }
                forEachCount++;
            }
        }

        Assert.assertEquals(forEachCount, 10,
                "Count of messages entered ForEach scope is incorrect");
    }

    @Test(groups = "wso2.esb", description = "Tests large message 3MB")
    public void testLargeMessage() throws Exception {
        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        String symbol2 = FixedSizeSymbolGenerator.generateMessageMB(3);
        OMElement response;

        response =
                axis2Client.sendCustomQuoteRequest(getMainSequenceURL(),
                        null, "MSFT" + symbol2);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("MSFT"), "Incorrect symbol in response");

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;
        int forEachCount = 0;

        for (int i = (afterLogSize - beforeLogSize - 1); i >= 0; i--) {
            String message = logs[i].getMessage();
            if (message.contains("foreach = in")) {
                if (!message.contains("MSFT")) {
                    Assert.fail("Incorrect message entered ForEach scope. Could not find symbol MSFT ..");
                }
                forEachCount++;
            }
        }

        Assert.assertEquals(forEachCount, 1,
                "Count of messages entered ForEach scope is incorrect");
    }

    @AfterClass
    public void close() throws Exception {
        symbol = null;
        super.cleanup();
    }

}
