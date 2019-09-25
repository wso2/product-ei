/*
*Copyright 2005, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.log;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.clients.logging.LoggingAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.Utils;

public class LogMediatorLevelTest extends ESBIntegrationTest {

    private LogViewerClient logViewer;
    private LoggingAdminClient logAdmin;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        logAdmin = new LoggingAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logViewer = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        verifyProxyServiceExistence("logMediatorLevelTestProxy");
        verifySequenceExistence("logCategoryTestSequence");


    }


    @Test(groups = "wso2.esb", description = "Tests level log")
    public void testSendingToDefinedEndpoint() throws Exception {

        logAdmin.updateLoggerData("org.apache.synapse", LoggingAdminClient.LogLevel.DEBUG.name(), true, false);
        logViewer.clearLogs();
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("logMediatorLevelTestProxy"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
        log.info(response);
        Thread.sleep(2000);
        logAdmin.updateLoggerData("org.apache.synapse", LoggingAdminClient.LogLevel.INFO.name(), true, false);
    }

    @Test(groups = "wso2.esb", description = "Tests System Logs")
    public void testSystemLogs() throws Exception {
        int beforeCount = logViewer.getAllRemoteSystemLogs().length;
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("logMediatorLevelTestProxy"),
                                                                     null, "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
        log.info(response);
        boolean logFound = Utils.checkForLog(logViewer,
                "*****TEST CUSTOM LOGGING MESSAGE TO SYSTEM LOGS TEST*****", 2);
        Assert.assertTrue(logFound, "System Log not found. LogViewer Admin service not working properly");
    }


    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        super.cleanup();
    }
}