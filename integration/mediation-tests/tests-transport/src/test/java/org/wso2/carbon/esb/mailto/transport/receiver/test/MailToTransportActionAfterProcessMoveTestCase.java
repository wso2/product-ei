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
import org.wso2.esb.integration.common.utils.MailToTransportUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.exception.ESBMailTransportIntegrationTestException;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test move email in mailbox after receiving email to ESB successfully.
 */
public class MailToTransportActionAfterProcessMoveTestCase extends ESBIntegrationTest {

    private String emailSubject;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mailTransport" +
                File.separator + "mailTransportReceiver" + File.separator + "mail_transport_move.xml");

        // Since ESB reads all unread emails one by one, we have to delete
        // the all unread emails before run the test
        MailToTransportUtil.deleteAllUnreadEmailsFromGmail();
    }

    @Test(groups = {"wso2.esb"}, description = "Test email sent")
    public void testEmailReceivedActionAfterProcessMove() throws Exception {
        Date date = new Date();
        emailSubject = "Process Move : " + new Timestamp(date.getTime());
        boolean isEmailSend = MailToTransportUtil.sendMailAndCheckReceived(emailSubject);
        log.info("email sent : " + isEmailSend);
        assertTrue(isEmailSend, "Email has not sent successfully");
    }

    @Test(groups = {"wso2.esb"}, description = "Test email transport action after process move",
            dependsOnMethods = {"testEmailReceivedActionAfterProcessMove"})
    public void testEmailTransportActionAfterProcessMove()
            throws ESBMailTransportIntegrationTestException {

        assertTrue(MailToTransportUtil.searchStringInLog(contextUrls.getBackEndUrl(), emailSubject, getSessionCookie()),
                   "Unable to receive the email successfully");

        assertTrue(MailToTransportUtil.waitToCheckEmailReceived(emailSubject, "Trash"),
                   "Mail has not moved successfully");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();

    }
}
