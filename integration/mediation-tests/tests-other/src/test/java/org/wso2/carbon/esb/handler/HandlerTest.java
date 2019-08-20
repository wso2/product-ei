/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.handler;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
//import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.common.FileManager;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Test class to test a synapse handler.
 */
public class HandlerTest extends ESBIntegrationTest {

    private static final String CONF_NAME = "synapse-handlers.xml";
    private static final String LOCATION = "/artifacts/ESB/handler";

    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        copyToComponentConf(getClass().getResource(LOCATION + "/" + CONF_NAME).getPath(), CONF_NAME);
        serverConfigurationManager.restartForcefully();
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();
    }

    @Test(groups = { "wso2.esb" },
          description = "Sending a Message Via proxy to check synapse handler logs")
    public void testSynapseHandlerExecution()
            throws IOException {
        boolean requestInStatus = false;
        boolean requestOutStatus = false;
        boolean responseInStatus = false;
        boolean responseOutStatus = false;
        boolean handlerStatus = false;
        OMElement response = axis2Client
                .sendSimpleStockQuoteRequest(getProxyServiceURLHttp("handlerTestProxy"), null, "WSO2");
        Assert.assertNotNull(response);
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        for (LogEvent logEvent : logs) {
            String message = logEvent.getMessage();
            if (message.contains("handleRequestInFlow")) {
                requestInStatus = true;
            }

            if (message.contains("handleRequestOutFlow")) {
                requestOutStatus = true;
            }

            if (message.contains("handleResponseInFlow")) {
                responseInStatus = true;
            }

            if (message.contains("handleResponseOutFlow")) {
                responseOutStatus = true;
            }
        }

        if (requestInStatus && requestOutStatus && responseInStatus && responseOutStatus) {
            handlerStatus = true;
        }

        Assert.assertTrue(handlerStatus, "Synapse handler not working properly");

    }

    @Test(groups = {"wso2.esb"}, description = "Sending a message via proxy to check whether Synapse Handlers get "
            + "invoked when a SoapFault come as a response")
    public void testSynapseHandlerExecutionWhenSoapFaultRecieved() throws IOException,
            InterruptedException {
        boolean responseInStatus = false;
        boolean errorOnSoapFaultStatus = false;
        logViewerClient.clearLogs();
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("handlerTestProxyWithSoapfault"), null,
                    "WSO2");
            fail("This query must throw an exception since SoapFault come as response");
        } catch (AxisFault expected) {
            assertEquals(expected.getReason(), "Custom ERROR Message", "Custom ERROR Message mismatched");
        }

        errorOnSoapFaultStatus = Utils.checkForLogsWithPriority(logViewerClient, "INFO", "Fault Sequence Hit", 10);
        responseInStatus = Utils.checkForLogsWithPriority(logViewerClient, "INFO", "handleResponseInFlow", 10);

        Assert.assertTrue(errorOnSoapFaultStatus, "When SoapFault come as a response the fault sequence hasn't been "
                + "invoked because of FORCE_ERROR_ON_SOAP_FAULT property is not working properly");
        Assert.assertTrue(responseInStatus, "Synapse handler hasn't been invoked when a Soap Fault received");
    }

    private void copyToComponentConf(String sourcePath, String fileName) throws IOException {
        String carbonHome = System.getProperty("carbon.home");
        String targetPath = carbonHome + File.separator + "conf";
        FileManager.copyResourceToFileSystem(sourcePath, targetPath, fileName);
    }

    private void removeFromComponentConf(String fileName) {
        String carbonHome = System.getProperty("carbon.home");
        String filePath = carbonHome + File.separator + "conf" + File.separator + fileName;
        FileManager.deleteFile(filePath);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        removeFromComponentConf(getClass().getResource(LOCATION + "/" + CONF_NAME).getPath());
        super.cleanup();
    }
}