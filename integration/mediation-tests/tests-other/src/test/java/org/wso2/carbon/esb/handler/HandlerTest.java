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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FileManager;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Test class to test a synapse handler.
 */
public class HandlerTest extends ESBIntegrationTest {

    private static final String JAR_NAME = "org.wso2.carbon.test.gateway-1.0.0.jar";
    private static final String CONF_NAME = "synapse-handlers.xml";
    private static final String LOCATION = "/artifacts/ESB/handler";

    private ServerConfigurationManager serverConfigurationManager;
    private LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager
                .copyToComponentDropins(new File(getClass().getResource(LOCATION + File.separator + JAR_NAME).toURI()));
        copyToComponentConf(getClass().getResource(LOCATION + File.separator + CONF_NAME).getPath(), CONF_NAME);
        serverConfigurationManager.restartForcefully();
        super.init();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewerClient.clearLogs();
    }

    @Test(groups = { "wso2.esb" },
          description = "Sending a Message Via proxy to check synapse handler logs")
    public void testSynapseHandlerExecution()
            throws IOException, EndpointAdminEndpointAdminException, LoginAuthenticationExceptionException,
            XMLStreamException, LogViewerLogViewerException {
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

    public void copyToComponentConf(String sourcePath, String fileName) throws IOException, URISyntaxException {
        String carbonHome = System.getProperty("carbon.home");
        String targetPath = carbonHome + File.separator + "conf";
        FileManager.copyResourceToFileSystem(sourcePath, targetPath, fileName);
    }

    public void removeFromComponentConf(String fileName) throws IOException, URISyntaxException {
        String carbonHome = System.getProperty("carbon.home");
        String filePath = carbonHome + File.separator + "conf" + File.separator + fileName;
        FileManager.deleteFile(filePath);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        serverConfigurationManager
                .removeFromComponentDropins(getClass().getResource(LOCATION + File.separator + JAR_NAME).getPath());
        removeFromComponentConf(getClass().getResource(LOCATION + File.separator + CONF_NAME).getPath());
        super.cleanup();
    }

}