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

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import static org.testng.Assert.assertTrue;


/**
 * This class is to test move email protocol IMAP with ESB
 */
public class MailToTransportProtocolIMAPTestCase extends ESBIntegrationTest {

    private String emailSubject;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mailTransport" +
                File.separator + "mailTransportReceiver" + File.separator + "mail_transport_protocol_imap.xml");

        // Since ESB reads all unread emails one by one, we have to delete
        // the all unread emails before run the test
        MailToTransportUtil.deleteAllUnreadEmailsFromGmail();
    }

    @Test(groups = {"wso2.esb"}, description = "Test email sent")
    public void testEmailReceived() throws Exception {
        Date date = new Date();
        emailSubject = "IMAP Protocol " + new Timestamp(date.getTime());
        boolean isEmailSend = MailToTransportUtil.sendMailAndCheckReceived(emailSubject);
        log.info("email sent : " + isEmailSend);
        assertTrue(isEmailSend, "Email has not Sent Successfully");
    }

    @Test(groups = {"wso2.esb"}, description = "Test email transport with IMAP protocol",
            dependsOnMethods = {"testEmailReceived"} , enabled = false)
    public void testEmailTransportProtocolIMAP() throws Exception {
        assertTrue(MailToTransportUtil.searchStringInLog(
                contextUrls.getBackEndUrl(), emailSubject,getSessionCookie()), "Mail not found in inbox");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();

    }
}


