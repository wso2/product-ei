/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import com.icegreen.greenmail.user.GreenMailUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.esb.integration.common.utils.clients.GreenMailClient;
import org.wso2.esb.integration.common.utils.servers.GreenMailServer;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test delete email in mailbox if failure occur while receiving email to ESB
 */
public class MailToTransportActionAfterFailureDELETETestCase extends ESBIntegrationTest {

    private String emailSubject;
    private static LogViewerClient logViewerClient;
    private static GreenMailClient greenMailClient;
    private static GreenMailUser greenMailUser;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mailTransport" +
                File.separator + "mailTransportReceiver" + File.separator +
                "mail_transport_actionafter_failure_delete.xml");
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
        greenMailUser = GreenMailServer.getPrimaryUser();
        greenMailClient = new GreenMailClient(greenMailUser);

        // Since ESB reads all unread emails one by one,
        // we have to delete all unread emails before running the test
        GreenMailServer.deleteAllEmails("imap");

    }

    @Test(groups = {"wso2.esb"}, description = "Test email transport received action after failure delete")
    public void testEmailTransportActionAfterFailureDELETE() throws Exception {
        logViewerClient.clearLogs();
        Date date = new Date();
        emailSubject = "Failure Delete : " + new Timestamp(date.getTime());
        greenMailClient.sendMail(emailSubject);

        assertTrue(Utils.checkForLog(logViewerClient, "Failed to process message", 10000),
                "Couldn't get the failure message!");

        assertTrue(GreenMailServer.checkEmailDeleted(emailSubject, "imap"), "Mail has not been deleted successfully");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();

    }
}

