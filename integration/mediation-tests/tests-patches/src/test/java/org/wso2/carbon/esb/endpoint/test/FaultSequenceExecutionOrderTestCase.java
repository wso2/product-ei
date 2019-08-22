/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.endpoint.test;

import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case for checking invocation of correct fault sequence.
 */
public class FaultSequenceExecutionOrderTestCase extends ESBIntegrationTest {

    private SampleAxis2Server axis2Server;
    private LogViewerClient logViewer;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "esbjava4526"
                + File.separator + "esbjava4526.xml");
        axis2Server = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server.deployService(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE + "_timeout");
        axis2Server.start();
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = {"wso2.esb"}, description = "Correct Fault Sequence Invoke Test")
    public void testCorrectFaultSequenceExecution() throws Exception {

        String contentType = "application/xml";
        SimpleHttpClient httpClient = new SimpleHttpClient();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", contentType);
        httpClient.doPost(getMainSequenceURL() + "faultSequenceExecutionOrderTest", headers, "", contentType);
        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        boolean isImmediateOnly = false, isSuperSequecefalutExecuted = false;
        for (LogEvent logEvent : logs) {
            if (logEvent.getMessage().contains("cF = C Fault")) {
                isImmediateOnly = true;
            }
            if (logEvent.getMessage().contains("aF = A Fault")) {
                isSuperSequecefalutExecuted = true;
            }
        }
        Assert.assertTrue(isImmediateOnly);
        Assert.assertFalse(isSuperSequecefalutExecuted);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        axis2Server.stop();
        axis2Server = null;
        logViewer = null;
        super.cleanup();
    }
}
