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

package org.wso2.carbon.esb.mailto.transport.sender.test;

import com.icegreen.greenmail.user.GreenMailUser;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.exception.ESBMailTransportIntegrationTestException;
import org.wso2.esb.integration.common.utils.servers.GreenMailServer;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import javax.mail.MessagingException;
import javax.xml.stream.XMLStreamException;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test mails send though ESB using a secondary account
 */
public class SendMailWithSecondaryAccountThroughESBTestCase extends ESBIntegrationTest {

    private static GreenMailUser receiver;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        super.reloadSessionCookie();

        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator + "mailTransport" +
                File.separator + "mailTransportSender" + File.separator + "secondaryAccount" + File.separator +
                "mail_sender_secondary.xml");

        receiver = GreenMailServer.addUser("receiver@localhost", "receiver", "receiver");

        // Since ESB reads all unread emails one by one, we have to delete the all unread emails to run the test
        GreenMailServer.deleteAllEmails("imap", receiver);
    }

    @Test(groups = {"wso2.esb"}, description = "Test email sender with secondary account ")
    public void testEmailTransport()
            throws ESBMailTransportIntegrationTestException, XMLStreamException, AxisFault, MessagingException {
        Date date = new Date();
        String message = "Send Mail With Secondary" + new Timestamp(date.getTime());
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement request = AXIOMUtil.stringToOM(
                " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                " <emailSubject> Subject :" + message + "</emailSubject>\n" +
                "   </soapenv:Body>\n" +
                " </soapenv:Envelope>");

        axisServiceClient.sendReceive(request, getProxyServiceURLHttp("MailToTransportSecondaryAccount"), "mediate");
        assertTrue(GreenMailServer.isMailReceived("imap", receiver, message), "Mail not received");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        super.cleanup();
    }

}


