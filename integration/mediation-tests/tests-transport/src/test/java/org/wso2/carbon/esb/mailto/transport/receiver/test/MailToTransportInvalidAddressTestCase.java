/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mailto.transport.receiver.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.servers.GreenMailServer;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test error log when wrong email address has given to get emails from
 */
public class MailToTransportInvalidAddressTestCase extends ESBIntegrationTest {

    private static LogViewerClient logViewerClient;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mailTransport" +
                File.separator + "mailTransportReceiver" + File.separator + "mail_transport_invalid_address.xml");
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        // Since ESB reads all unread emails one by one, we have to delete
        // the all unread emails before run the test
        GreenMailServer.deleteAllEmails("pop3");
    }


    @Test(groups = {"wso2.esb"}, description = "Test email transport with invalid address parameter and pop3 protocol")
    public void testEmailTransportInvalidAddress() throws Exception {
        logViewerClient.clearLogs();
        assertTrue(Utils.checkForLog(logViewerClient, "Error connecting to mail server for address", 10000),
                "Couldn't find the error message in log");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();

    }
}
