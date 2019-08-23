/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.mediator.test.iterate.IterateClient;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test that foreach will process the payload sequentially. Verify the request payload order against processed order.
 */
public class ForEachSequentialExecutionTestCase extends ESBIntegrationTest {

    private IterateClient client;
    private LogViewerClient logViewer;

    @BeforeClass
    public void setEnvironment() throws Exception {
        init();
        client = new IterateClient();
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Test foreach inline sequence to sequentially transform payload")
    public void testSequentialExecution() throws Exception {
        loadESBConfigurationFromClasspath(
                "/artifacts/ESB/mediatorconfig/foreach/foreach_simple.xml");
        logViewer.clearLogs();

        String response = client.send(getProxyServiceURLHttp("foreachSequentialExecutionTestProxy"), createMultipleSymbolPayLoad(10),
                "urn:getQuote");
        Assert.assertNotNull(response);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int forEachCount = 0;

        // Verify logs to check that the order of symbols is same as in the payload. The symbols should be as SYM[1-10]
        // as in payload. Since loop iterates from the last log onwards, verifying whether the symbols are in
        // SYM[1-10] order since logViewer
        for (int i = 0; i < logs.length; i++) {
            String message = logs[i].getMessage();
            if (message.contains("foreach = in")) {
                if (!message.contains("SYM" + forEachCount)) {
                    Assert.fail("Incorrect message entered ForEach scope. Could not find symbol SYM" + forEachCount + " Found : " + message);
                }
                forEachCount++;
            }
        }

        Assert.assertEquals(forEachCount, 10, "Count of messages entered ForEach scope is incorrect");

    }

    private OMElement createMultipleSymbolPayLoad(int iterations) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        OMNamespace omNs = fac.createOMNamespace("http://services.samples", "ns");
        OMElement method = fac.createOMElement("getQuote", omNs);

        for (int i = 0; i < iterations; i++) {
            OMElement chkPrice = fac.createOMElement("CheckPriceRequest", omNs);
            OMElement code = fac.createOMElement("Code", omNs);
            chkPrice.addChild(code);
            code.setText("SYM" + i);
            method.addChild(chkPrice);
        }
        return method;
    }

    @AfterClass
    public void close() throws Exception {
        client = null;
        super.cleanup();
    }
}
